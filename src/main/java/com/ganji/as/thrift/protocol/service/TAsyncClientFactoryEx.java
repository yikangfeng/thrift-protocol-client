/**
 * 
 */
package com.ganji.as.thrift.protocol.service;

import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;

/**
 * @author yikangfeng
 * @date 2015年7月23日
 */
public interface TAsyncClientFactoryEx<T extends TAsyncClient> {
	public T getAsyncClient(final TProtocolFactory protocolFactory,
			final TAsyncClientManager asyncClientManager,
			final TNonblockingTransport transport);
}
