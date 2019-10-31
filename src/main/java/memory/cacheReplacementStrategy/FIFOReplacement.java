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
     * @return 命中返回1，没命中返回-1
     */
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
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return
     */
    @Override
    public int writeCache(int start, int end, char[] addrTag, char[] input) {
        // TODO
        return -1;
    }


}
