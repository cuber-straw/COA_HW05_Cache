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
     * 根据目标数据内存地址前22位的int表示,判断是否命中
     * @param blockNO 内存地址前22位的int表示
     * @return 若命中，返回一个自己想返回的数，我这里返回1，若不命中返回-1
     */
    @Override
    public int map(int blockNO) {
        // TODO
        Cache thisCache = Cache.getCache();
        ReplacementStrategy thisStrategy = thisCache.mappingStrategy.replacementStrategy;
        String blockNumber = transform(blockNO);
        // 我们要遍历cache的所有行，判断是否有行跟blockNO的二进制表示相同
        // cache 中一共有1024行
        if (thisStrategy.isHit(0, 1023, blockNumber.toCharArray()) == 1){
            return 1;
        }
        return -1;
    }

    /**
     * 在未命中的情况下重写cache，有三种替换策略
     * @param blockNO 内存地址前22位的int表示
     * @return 返回cache中所对应的行
     */
    @Override
    public int writeCache(int blockNO) {
        // TODO
        Cache thisCache = Cache.getCache();
        ReplacementStrategy thisStrategy = thisCache.mappingStrategy.replacementStrategy;
        return -1;
    }
}
