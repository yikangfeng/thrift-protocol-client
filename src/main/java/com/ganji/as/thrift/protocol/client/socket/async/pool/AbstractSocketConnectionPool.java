/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;

/**
 * @author yikangfeng
 * @date 2015年7月22日
 */
public abstract class AbstractSocketConnectionPool implements
		SocketConnectionPool {

	@Override
	public SocketConnection getSocketConnection(LoadBalance loadBalance,
			ThriftClientInvocation clientInvocation) throws Throwable {
		// TODO Auto-generated method stub
		return getInternalSocketConnection(loadBalance, clientInvocation);
	}

	@Override
	public SocketConnection getSocketConnectionByHostAndPort(String host,
			int port) throws Throwable {
		// TODO Auto-generated method stub
		return getInternalSocketConnectionByHostAndPort(host, port);
	}

	abstract protected SocketConnection getInternalSocketConnection(
			LoadBalance loadBalance, ThriftClientInvocation clientInvocation)
			throws Throwable;

	abstract protected SocketConnection getInternalSocketConnectionByHostAndPort(
			String host, int port) throws Throwable;

}
