package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import transformer.Transformer;

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
