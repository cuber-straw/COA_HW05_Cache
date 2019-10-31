package memory;

import memory.cacheMappingStrategy.AssociativeMapping;
import memory.cacheMappingStrategy.DirectMapping;
import memory.cacheMappingStrategy.MappingStrategy;
import memory.cacheMappingStrategy.SetAssociativeMapping;
import memory.cacheReplacementStrategy.FIFOReplacement;
import memory.cacheReplacementStrategy.LFUReplacement;
import memory.cacheReplacementStrategy.ReplacementStrategy;
import transformer.Transformer;

import java.util.Arrays;

/**
 * 高速缓存抽象类
 * TODO: 缓存机制实现
 */
public class Cache {	//

	public static final boolean isAvailable = true;			// 默认启用Cache

	public static final int CACHE_SIZE_B = 1 * 1024 * 1024;      // 1 MB 总大小

	public static final int LINE_SIZE_B = 1 * 1024; // 每一行 1 KB 最小可寻址单元

	public CacheLinePool cache = new CacheLinePool(CACHE_SIZE_B/LINE_SIZE_B); 	// 总大小1MB / 行大小1KB = 1024个行

	private static Cache cacheInstance = new Cache();

	private Cache() {}

	public static Cache getCache() {
		return cacheInstance;
	}

	public MappingStrategy mappingStrategy;

	Transformer transformer = new Transformer();

	/**
	 * 查询{@link Cache#cache}表以确认包含[sAddr, sAddr + len)的数据块是否在cache内
	 * 如果目标数据块不在Cache内，则将其从内存加载到Cache
	 * @param sAddr 数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
	 * @param len 待读数据的字节数，[sAddr, sAddr + len)包含的数据必须在同一个数据块内
	 * @return 数据块在Cache中的对应行号
	 */
	public int fetch(String sAddr, int len) {
		// TODO
		int blockNO = Integer.parseInt(transformer.binaryToInt("0000000000"+sAddr.substring(0, 22))); // 得到地址所在的块号
		// map 返回-1，说明未命中
		int rowNO = mappingStrategy.map(blockNO);
		if (rowNO == -1) { // 表示没有命中，要将数据写入cache，并返回写入的行号
			return mappingStrategy.writeCache(blockNO);
		} else {
			return rowNO;
		}
	}

	/**
	 * 读取[eip, eip + len)范围内的连续数据，可能包含多个数据块的内容
	 * @param eip 数据起始点(32位物理地址 = 22位块号 + 10位块内地址)
	 * @param len 待读数据的字节数
	 * @return
	 */
	public char[] read(String eip, int len){
		char[] data = new char[len];
		Transformer t = new Transformer();
		int addr =  Integer.parseInt(t.binaryToInt("0" + eip)); // 为什么这边要加0？因为地址要是正数，加0可以把以1开头的负数变成正数
		int upperBound = addr + len;
		int index = 0;
		while (addr < upperBound) {
			int nextSegLen = LINE_SIZE_B - (addr % LINE_SIZE_B);
			if (addr + nextSegLen >= upperBound) {
				nextSegLen = upperBound - addr;
			}
			int rowNO = fetch(t.intToBinary(String.valueOf(addr)), nextSegLen); // fetch应该返回正确的rowNO
			char[] cache_data = cache.get(rowNO).getData();
			int i=0;
			while (i < nextSegLen) {
				data[index] = cache_data[addr % LINE_SIZE_B + i];
				index++;
				i++;
			}
			addr += nextSegLen;
		}
		return data;
	}

	public void setStrategy(MappingStrategy mappingStrategy, ReplacementStrategy replacementStrategy) {
		this.mappingStrategy = mappingStrategy;
		this.mappingStrategy.setReplacementStrategy(replacementStrategy);
	}

	/**
	 * 从32位物理地址(22位块号 + 10位块内地址)获取目标数据在内存中对应的块号
	 * @param addr
	 * @return
	 */
	public int getBlockNO(String addr) {
		Transformer t = new Transformer();
		return Integer.parseInt(t.binaryToInt("0" + addr.substring(0, 22)));
	}

	/**
	 * 告知Cache某个连续地址范围内的数据发生了修改，缓存失效
	 * @param sAddr 发生变化的数据段的起始地址
	 * @param len 数据段长度
	 */
	public void invalid(String sAddr, int len) {
		int from = getBlockNO(sAddr);
		Transformer t = new Transformer();
		int to = getBlockNO(t.intToBinary(String.valueOf(Integer.parseInt(t.binaryToInt("0" + sAddr)) + len - 1)));

		for (int blockNO=from; blockNO<=to; blockNO++) {
			int rowNO = mappingStrategy.map(blockNO);
			if (rowNO != -1) {
				cache.get(rowNO).validBit = false;
			}
		}
	}

	/**
	 * 清除Cache全部缓存
	 */
	public void clear() {
		for (CacheLine line:cache.clPool) {
			if (line != null) {
				line.validBit = false;
			}
		}
	}

	/**
	 * 输入行号和对应的预期值，判断Cache当前状态是否符合预期
	 * 这个方法仅用于测试
	 * @param lineNOs
	 * @param validations
	 * @param tags
	 * @return
	 */
	public boolean checkStatus(int[] lineNOs, boolean[] validations, char[][] tags) {
		if (lineNOs.length != validations.length || validations.length != tags.length) {
			return false;
		}
		for (int i=0; i<lineNOs.length; i++) {
			CacheLine line = cache.get(lineNOs[i]);
			if (line.validBit != validations[i]) {
				return false;
			}
			if (!Arrays.equals(line.getTag(), tags[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 负责对CacheLine进行动态初始化
	 */
	public class CacheLinePool {
		/**
		 * @param lines Cache的总行数
		 */
		CacheLinePool(int lines) {
			clPool = new CacheLine[lines];
		}
		public CacheLine[] clPool;

		/**
		 * 根据行号获得特定的行
		 * @param lineNO 行号，十进制整数
		 * @return 如果对应行有数据，返回这一行，否则返回null
		 */
		public CacheLine get(int lineNO) {
			if (lineNO >= 0 && lineNO <clPool.length) {
				CacheLine l = clPool[lineNO];
				if (l == null) {
					clPool[lineNO] = new CacheLine();
					l = clPool[lineNO];
				}
				return l;
			}
			return null;
		}
	}

	/**
	 * Cache行，每行长度为(1+22+{@link Cache#LINE_SIZE_B})
	 */
	public class CacheLine {
		// 有效位，标记该条数据是否有效
		public boolean validBit = false;

		// 用于LFU算法，记录该条cache使用次数
		public int visited = 0;

		// 用于LRU和FIFO算法，记录该条数据时间戳
		public Long timeStamp = 0L;

		// 标记，占位长度为()22位，有效长度取决于映射策略：
		// 直接映射: 12 位
		// 全关联映射: 22 位
		// (2^n)-路组关联映射: 22-(10-n) 位
		// 注意，tag在物理地址中用高位表示，如：直接映射(32位)=tag(12位)+行号(10位)+块内地址(10位)，
		// 那么对于值为0b1111的tag应该表示为0000000011110000000000，其中前12位为有效长度
		public char[] tag = new char[22];

		// 数据，LINE_SIZE_B = 1024
		public char[] data = new char[LINE_SIZE_B];

		char[] getData() {
			return this.data;
		}
		char[] getTag() {
			return this.tag;
		}

	}


	public static void main(String[] args) {
		Memory memory = Memory.getMemory();
		Cache cache = Cache.getCache();
		cache.setStrategy(new AssociativeMapping(), new FIFOReplacement());

		char[] input1 = new char[1024 * 1024];
		char[] input2 = new char[1024];
		char[] input3 = new char[1024];
		Arrays.fill(input1, (char)0b11111111);
		Arrays.fill(input2, (char)0b01010101);
		Arrays.fill(input3, (char)0b01110111);
		String eip1 = "00000000000000000000000000000000";
		String eip2 = "00000010101000000000010000000000";
		String eip3 = "00000010011100000000010000000001";

		// 第一次写入
		memory.write(eip1, input1.length, input1);
		// cache里现在应该全是 0b11111111
		char[] dataRead = cache.read(eip1, 1024 * 1024);

		// 第二次写入
		memory.write(eip2, input2.length, input2);
		// cache中第一个块应该被替换，相应的tag需要改动
		dataRead = cache.read(eip2, 1024);
		cache.checkStatus(new int[]{0}, new boolean[]{true}, new char[][]{"0000001010100000000001".toCharArray()});

		// 第三次写入
		// cache中的第二个块和第三个块应该被替换，相应的tag需要改动
		memory.write(eip3, input3.length, input3);
		dataRead = cache.read(eip3, 1024);
		
		cache.clear();

	}
}
