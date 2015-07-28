/**
 * 
 */
package com.ganji.as.thrift.protocol.service.intf;

import com.ganji.as.thrift.protocol.client.future.Future;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public interface ThriftProtocolService<REQ, REP> extends AutoCloseable {
	Future<REP> apply(final REQ clientRequest);
	void close() throws Exception;
}
