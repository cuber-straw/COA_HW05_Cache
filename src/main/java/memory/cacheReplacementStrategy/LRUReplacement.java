package memory.cacheReplacementStrategy;

import memory.Cache;

import java.util.Arrays;

/**
 * 最近最少用算法，替换掉最久没有被引用的
 */
public class LRUReplacement extends ReplacementStrategy {

    /**
     * 判断是否命中，代码相同
     * @param start 起始位置
     * @param end 结束位置 闭区间
     * @return 若命中，返回其所在cache中的行号，若没命中，返回-1
     */
    @Override
    public int isHit(int start, int end, char[] addrTag) {
        // TODO
        Cache thisCache = Cache.getCache();

        // 首先所有行的timeStamp++，然后如果有命中的话，命中行的timeStamp设为最小值
        // 这样即使memory.write中对cache进行了不必要的访问，也不会改变不同行timeStamp之间的相对大小
        for (int i=start; i<end; i++){
            try {
                thisCache.cache.clPool[i].timeStamp ++;
            } catch (NullPointerException e) {
                // 可能会未被初始化，不用管
            }
        }
        for (int i=start; i<end; i++){
            if (Arrays.equals(addrTag, thisCache.cache.get(i).tag)){
                if (thisCache.cache.get(i).validBit){ // 成功命中
                    thisCache.cache.clPool[i].timeStamp = 1L;
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * 找到最小时间戳的行，替换
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return
     */
    @Override
    public int writeCache(int start, int end, char[] addrTag, char[] input) {
        // TODO
        Cache thisCache = Cache.getCache();

        // 当cache中有无效行时，直接写到无效行中
        for (int i=start; i<end; i++){
            if (!thisCache.cache.clPool[i].validBit){
                thisCache.cache.clPool[i].data = input;
                thisCache.cache.clPool[i].timeStamp = 1L;
                thisCache.cache.clPool[i].tag = addrTag;
                thisCache.cache.clPool[i].validBit = true;
                return i;
            }
        }

        // 如果cache中所有行都为有效行，替换掉最久没有被引用的，
        long maxTimeWithNoReference = thisCache.cache.clPool[start].timeStamp;
        int lineNumberOfMaxTimeWithNoReference = start;
        for (int i=start; i<end; i++){
            if (thisCache.cache.clPool[i].timeStamp > maxTimeWithNoReference){
                maxTimeWithNoReference = thisCache.cache.clPool[i].timeStamp;
                lineNumberOfMaxTimeWithNoReference = i;
            }
        }

        thisCache.cache.clPool[lineNumberOfMaxTimeWithNoReference].timeStamp = 1L;
        thisCache.cache.clPool[lineNumberOfMaxTimeWithNoReference].tag = addrTag;
        thisCache.cache.clPool[lineNumberOfMaxTimeWithNoReference].data = input;
        return -1;
    }

}