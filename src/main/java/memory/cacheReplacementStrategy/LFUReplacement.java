package memory.cacheReplacementStrategy;

import memory.Cache;

import java.util.Arrays;

/**
 * 最近不经常使用算法
 * 每次命中后增加visited，visited记录该条cache的使用次数
 */
public class LFUReplacement extends ReplacementStrategy {

    @Override
    public int isHit(int start, int end, char[] addrTag) {
        // TODO
        Cache thisCache = Cache.getCache();
        for (int i=start; i<end; i++){
            if (Arrays.equals(addrTag, thisCache.cache.get(i).tag)){
                if (thisCache.cache.get(i).validBit){ // 成功命中
                    thisCache.cache.get(i).visited ++;
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 在未命中的情况下将内存中的数写入cache
     * 替换掉visited最小的
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
        // 首先检查有没有失效的行,如果有失效行，直接写到失效行去
        for (int i=start; i<end; i++){
            if (!thisCache.cache.clPool[i].validBit){
                thisCache.cache.clPool[i].data = input;
                thisCache.cache.clPool[i].visited = 1;
                thisCache.cache.clPool[i].tag = addrTag;
                thisCache.cache.clPool[i].validBit = true;
                return i;
            }
        }

        // 在所有行都是有效行的基础下，再去寻找visited最小的替换
        int minimumVisited = thisCache.cache.get(start).visited;
        int minimumVisitedLineNumber = start;
        for (int i=start; i<end; i++){
            if (thisCache.cache.get(i).visited < minimumVisited){
                minimumVisited = thisCache.cache.get(i).visited;
                minimumVisitedLineNumber = i;
            }
        }
        thisCache.cache.clPool[minimumVisitedLineNumber].data = input;
        thisCache.cache.clPool[minimumVisitedLineNumber].visited = 1;
        thisCache.cache.clPool[minimumVisitedLineNumber].tag = addrTag;
        return minimumVisitedLineNumber;
    }
}
