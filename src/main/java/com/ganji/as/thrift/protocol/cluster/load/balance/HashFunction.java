/**
 * 
 */
package com.ganji.as.thrift.protocol.cluster.load.balance;

/**
 * @author yikangfeng
 * @date 2015年7月22日
 */
public interface HashFunction {
	long hash(final String key);
}
