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
        String blockNumber = transformer.intToBinary(Integer.toString(blockNO));
        char[] blockTag = new char[22];
        for (int i=0; i<22; i++){
            blockTag[i] = blockNumber.charAt(i);
        }
        return blockTag;
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
