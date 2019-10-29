package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

/**
 * 4路-组相连映射 n=4,   14位标记 + 8位组号 + 10位块内地址
 * 256个组，每个组4行
 */
public class SetAssociativeMapping extends MappingStrategy{

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
        return -1;
    }

    @Override
    public int writeCache(int blockNO) {
        // TODO
        return -1;
    }
}










