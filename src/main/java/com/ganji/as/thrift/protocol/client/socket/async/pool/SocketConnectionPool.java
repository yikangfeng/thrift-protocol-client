/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public interface SocketConnectionPool {
	boolean isReady();// Connecting pool is ready

	void removeSocketConnection(final SocketConnection socketConnection);

	SocketConnection getSocketConnection(final LoadBalance loadBalance,
			final ThriftClientInvocation clientInvocation) throws Throwable;

	SocketConnection getSocketConnectionByHostAndPort(final String host,
			final int port) throws Throwable;

	void destory();
}
