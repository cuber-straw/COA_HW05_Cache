package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

import java.util.Arrays;

/**
 * 直接映射 12位标记 + 10位块号 + 10位块内地址
 */
public class DirectMapping extends MappingStrategy{

    Transformer transformer = new Transformer();

    /**
     * @param blockNO 内存数据块的块号,应该是有22位
     * @return cache数据块号 22-bits  [前12位有效]
     */
    @Override
    public char[] getTag(int blockNO) {
        // TODO
        // 直接映射中，int块号由22位二进制串计算而来，其中前12位是cache中的tag位，后10位是其对应的cache中的行号
        // 先将block转化位22位01串，再取其前12位返回
        String blockNumber = transform(blockNO);
        char[] tag = new char[22];
        for (int i=0; i<22; i++){
            tag[i] = blockNumber.charAt(i);
        }
        return tag;
    }


    /**
     * 根据内存地址找到对应的行是否命中，直接映射不需要用到替换策略
     * @param blockNO 内存数据块的块号
     * @return -1 表示未命中 1  表示命中
     */
    @Override
    public int map(int blockNO) {
        // TODO
        Cache thisCache = Cache.getCache();
        int lineNO = blockNO % (int)Math.pow(2, 12); // 通过22位的blockNO得到低10位的行号，对2的12次方取模
        Cache.CacheLine cl = thisCache.cache.get(lineNO); // 获得cache中的对应行
        boolean equal = true;
        for (int i=0; i<12; i++){
            if (cl.tag[i] != getTag(blockNO)[i]){
                equal = false;
                break;
            }
        }
        if (equal){ // 判断对应行的tag是否与blockNO的tag相同
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 在未命中情况下重写cache，直接映射不需要用到替换策略
     * @param blockNO
     * @return
     */
    @Override
    public int writeCache(int blockNO) {
        // TODO
        if (map(blockNO) == -1){
            Cache thisCache = Cache.getCache();
            Memory thisMemory = Memory.getMemory();
            String blockNumber = transform(blockNO);
            int lineNO = blockNO % (int)Math.pow(2, 12);
            thisCache.cache.clPool[lineNO].data = thisMemory.read(blockNumber+"0000000000", 1024);
            thisCache.cache.clPool[lineNO].tag = getTag(blockNO);
        }
        return -1;
    }


}
