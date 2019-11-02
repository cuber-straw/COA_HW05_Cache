package memory.cacheReplacementStrategy;

import memory.Cache;

import java.util.Arrays;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {

    /**
     * 在start-end范围内查找是否命中
     * 对于全关联映射，起始行为第0行，结束行为第1024行
     * @param start 起始行
     * @param end 结束行 闭区间
     * @return 命中返回命中的行号，没命中返回-1
     */
    @Override
    public int isHit(int start, int end, char[] addrTag) {
        // TODO
        Cache thisCache = Cache.getCache();
        for (int i=start; i<end; i++){
            if (Arrays.equals(addrTag, thisCache.cache.get(i).tag)){
                if (thisCache.cache.get(i).validBit){ // 成功命中
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 在未命中的情况下将内存中的数写入cache
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return 返回写入到cache中的哪一行
     */
    @Override
    public int writeCache(int start, int end, char[] addrTag, char[] input) {
        // TODO
        Cache thisCache = Cache.getCache();

        // 如果cache命中，不需要重写，那么所有行的timeStamp是否增加不影响它们的相对大小
        // 所以这里只在没命中需要重写的时候增加timeStamp
        for (int i=start; i<end; i++){
            thisCache.cache.clPool[i].timeStamp ++;
        }
        // 当cache中有无效行时，可以直接写到无效行中
        for (int i=start; i<end; i++){
            if (!thisCache.cache.clPool[i].validBit){
                thisCache.cache.clPool[i].data = input;
                thisCache.cache.clPool[i].timeStamp = 1L;
                thisCache.cache.clPool[i].tag = addrTag;
                thisCache.cache.clPool[i].validBit = true;
                return i;
            }
        }

        // 当cache所有有行有占满后，寻找timeStamp最大的行替换掉
        long maxTimeStamp = thisCache.cache.clPool[start].timeStamp;
        int maxTimeStampLineNum = start;
        for (int i=start; i<end; i++){
            if (thisCache.cache.clPool[i].timeStamp > maxTimeStamp){
                maxTimeStamp = thisCache.cache.clPool[i].timeStamp;
                maxTimeStampLineNum = i;
            }
        }
        thisCache.cache.clPool[maxTimeStampLineNum].timeStamp = 1L;
        thisCache.cache.clPool[maxTimeStampLineNum].tag = addrTag;
        thisCache.cache.clPool[maxTimeStampLineNum].data = input;
        return maxTimeStampLineNum;
    }


}
