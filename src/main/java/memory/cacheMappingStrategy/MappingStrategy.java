package memory.cacheMappingStrategy;

import memory.cacheReplacementStrategy.ReplacementStrategy;

public abstract class MappingStrategy {

	protected ReplacementStrategy replacementStrategy;

	public void setReplacementStrategy(ReplacementStrategy replacementStrategy) {
		this.replacementStrategy = replacementStrategy;
	}

	/**
	 * 根据块号，结合具体的映射策略，计算数据块在Cache行中的Tag
	 * @param blockNO
	 * @return 长度为22
	 */
	public abstract char[] getTag(int blockNO);


	/**
	 * 根据目标数据内存地址前22位的int表示，进行映射与替换
	 * @param blockNO
	 * @return 返回cache中所对应的行，-1表示未命中
	 */
	public abstract int map(int blockNO);

	/**
	 * 未命中的情况下，将内存读取出的input数据写入cache
	 * @param blockNO
	 * @return 返回cache中所对应的行
	 */
	public abstract int writeCache(int blockNO);

	/**
	 * 将int类型的blockNumber转换位22位01串
	 * @param num 待转换的数
	 * @return 22位二进制字符串
	 */
	static String transform(int num){
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<22; i++){
			if (num / (int)Math.pow(2, 21-i) == 1){
				sb.append(1);
				num = num - (int)Math.pow(2, 21-i);
			} else {
				sb.append(0);
			}
		}
		return sb.toString();
	}
}
