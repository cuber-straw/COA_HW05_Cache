package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;
import memory.cacheReplacementStrategy.*;
import java.util.Arrays;

public class AssociativeMapping extends MappingStrategy {  // 全相联映射

    Transformer transformer = new Transformer();
    /**
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前22位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        // TODO
        // 全关联映射，22位的块号就是cache的tag
        String blockNumber = transform(blockNO);
        char[] tag = new char[22];
        for (int i=0; i<22; i++){
            tag[i] = blockNumber.charAt(i);
        }
        return tag;
    }

    /**
     * 全关联映射，根据目标数据内存地址前22位的int表示,判断是否命中
     * 因为是全关联，所以需要检查从0到1023的所有行，看是否有行的tag与address的22位匹配
     * @param blockNO 内存地址前22位的int表示
     * @return 若命中，返回命中的行号，若不命中返回-1
     */
    @Override
    public int map(int blockNO) {
        // TODO
        Cache thisCache = Cache.getCache();
        ReplacementStrategy thisReplacementStrategy = thisCache.mappingStrategy.replacementStrategy;
        String blockNumber = transform(blockNO);
        // 我们要遍历cache的所有行，判断是否有行跟blockNO的二进制表示相同
        // cache 中一共有1024行
        return thisReplacementStrategy.isHit(0, 1024, blockNumber.toCharArray());
    }

    /**
     * 在未命中的情况下重写cache，有三种替换策略
     * @param blockNO 内存地址前22位的int表示
     * @return 返回cache中所对应的行
     */
    @Override
    public int writeCache(int blockNO) {
        // TODO
        String blockNumber = transform(blockNO);
        Cache thisCache = Cache.getCache();
        Memory thisMemory = Memory.getMemory();
        char[] data = thisMemory.read(blockNumber+"0000000000", 1024);
        ReplacementStrategy thisReplacementStrategy = thisCache.mappingStrategy.replacementStrategy;
        int lineNO = thisReplacementStrategy.writeCache(0, 1024, blockNumber.toCharArray(), data);
        thisCache.cache.clPool[lineNO].validBit = true;
        return lineNO;
    }
}
