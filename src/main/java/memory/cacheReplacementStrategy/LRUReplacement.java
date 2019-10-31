package memory.cacheReplacementStrategy;

import memory.Cache;

import java.util.Arrays;

/**
 * 最近最少用算法
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
        return -1;
    }

}





























