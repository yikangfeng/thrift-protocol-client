/**
 * 
 */
package com.ganji.as.thrift.protocol.service.intf;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public interface ThriftProtocolFunction<INPUT,OUTPUT> {
	OUTPUT apply(final INPUT __buffer__);
}
