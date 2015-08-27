/**
 * 
 */
package com.ganji.as.thrift.protocol.client.intf;

import java.util.concurrent.TimeUnit;

import com.ganji.as.thrift.protocol.client.future.Future;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public interface ThriftProtocolClientRetryPolicy {
	void retry(final Future<?> future,
			final int retries,final long timeout,final TimeUnit unit);
}
