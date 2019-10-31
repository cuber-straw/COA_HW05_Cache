package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheReplacementStrategy.ReplacementStrategy;
import transformer.Transformer;

/**
 * 4路-组相连映射 n=4,   14位标记 + 8位组号 + 10位块内地址
 * 256个组，每个组4行
 */
public class SetAssociativeMapping extends MappingStrategy{


    Transformer transformer = new Transformer();

    /**
     *
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前14位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        // TODO
        // 4路组关联映射，1024行，256组（2的8次方）
        String blockNumber = transform(blockNO);
        char[] tag = new char[22];
        for (int i=0; i<14; i++){
            tag[i] = blockNumber.charAt(i);
        }
        for (int i=14; i<22; i++){
            tag[i] = '0';
        }
        return tag;
    }

    /**
     *
     * @param blockNO 目标数据内存地址前22位int表示
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        // TODO
        Cache thisCache = Cache.getCache();
        ReplacementStrategy thisReplaceStrategy = thisCache.mappingStrategy.replacementStrategy;
        String blockNumber = transform(blockNO);

        // blockNumber的前14位为tag位，后8位为组号
        // 我们判断是否命中，只要判断组号中的4行是否有tag一样的就行

        int setNumber = Integer.parseInt(transformer.binaryToInt("000000000000000000"+blockNumber.substring(14)));
        int start = 4*setNumber;
        int end = 4*setNumber + 4;
        return thisReplaceStrategy.isHit(start, end, (blockNumber.substring(0, 14)+"00000000").toCharArray());
    }

    @Override
    public int writeCache(int blockNO) {
        // TODO
        String blockNumber = transform(blockNO);
        Cache thisCache = Cache.getCache();
        Memory thisMemory = Memory.getMemory();
        char[] data = thisMemory.read(blockNumber+"0000000000", 1024);
        ReplacementStrategy thisReplaceStrategy = thisCache.mappingStrategy.replacementStrategy;

        int setNumber = Integer.parseInt(transformer.binaryToInt("000000000000000000"+blockNumber.substring(14)));
        int start = 4*setNumber;
        int end = (4+1)*setNumber;
        int lineNO = thisReplaceStrategy.writeCache(start, end, (blockNumber.substring(0, 14)+"00000000").toCharArray(), data);
        thisCache.cache.clPool[lineNO].validBit = true;
        return lineNO;
    }
}










