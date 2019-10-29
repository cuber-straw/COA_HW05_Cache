package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

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
        for (int i=0; i<12; i++){
            tag[i] = blockNumber.charAt(i);
        }
        return tag;
    }


    /**
     * 根据内存地址找到对应的行是否命中，直接映射不需要用到替换策略
     * @param blockNO
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        // TODO
        return -1;
    }

    /**
     * 在未命中情况下重写cache，直接映射不需要用到替换策略
     * @param blockNO
     * @return
     */
    @Override
    public int writeCache(int blockNO) {
        // TODO
        return -1;
    }


}
