package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

import java.util.Arrays;

/**
 * 直接映射 12位标记 + 10位块号 + 10位块内地址
 */
public class DirectMapping extends MappingStrategy {

    Transformer transformer = new Transformer();

    /**
     * @param blockNO 内存数据块的块号,应该是有22位，然后被转化成int
     * @return cache数据块号 22-bits  [前12位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        // TODO
        // 直接映射中，int块号由22位二进制串计算而来，其中前12位是cache中的tag位，后10位是其对应的cache中的行号
        // 先将block转化位22位01串，再取其前12位返回
        String blockNumber = transform(blockNO);
        // 返回的tag有22个char，前12个是直接映射下的tag，后10个标记映射到的cache中的行号
        char[] tag = new char[22];
        for (int i = 0; i < 12; i++) {
            tag[i] = blockNumber.charAt(i);
        }

        // 由directingMapping的用例3可知，tag的无效位全都被赋值为0。
        // 直接映射中，tag后10位是多余的，这里赋值为0。
        for (int i = 12; i < 22; i++) {
            tag[i] = '0';
        }
        return tag;
    }


    /**
     * 根据内存地址找到对应的行是否命中，直接映射不需要用到替换策略
     *
     * @param blockNO 内存数据块的块号
     * @return -1 表示未命中 1  表示命中
     */
    @Override
    public int map(int blockNO) {
        // TODO
        Cache thisCache = Cache.getCache();
        int lineNO = blockNO % 1024; // 通过22位的blockNO得到低10位的行号，对1024取模
        Cache.CacheLine cl = thisCache.cache.get(lineNO); // 获得cache中的对应行
        boolean equal = true;
        if (!cl.validBit) {
            return -1;
        }
        try {
            for (int i = 0; i < 12; i++) {
                if (cl.tag[i] != getTag(blockNO)[i]) {
                    equal = false;
                    break;
                }
            }
        } catch (NullPointerException e) {
            equal = false;
        }
        if (equal) { // 判断对应行的tag是否与blockNO的tag相同
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 在未命中情况下重写cache，直接映射不需要用到替换策略
     * @param blockNO
     * @return 返回cache中所对应的行。事实上不需要用到返回值，这里直接返回-1了
     */
    @Override
    public int writeCache(int blockNO) {
        // TODO
        Cache thisCache = Cache.getCache();
        Memory thisMemory = Memory.getMemory();
        String blockNumber = transform(blockNO);
        int lineNO = blockNO % 1024;
        thisCache.cache.clPool[lineNO].validBit = true;
        thisCache.cache.clPool[lineNO].data = thisMemory.read(blockNumber + "0000000000", 1024);
        thisCache.cache.clPool[lineNO].tag = getTag(blockNO);
        return -1;
    }


}
