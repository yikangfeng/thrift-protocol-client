/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public abstract class SocketConnectionPoolFactory {
	static private ServiceLoader<SocketConnectionPoolProvider> serviceLoader = ServiceLoader
			.load(SocketConnectionPoolProvider.class);
	static private SocketConnectionPoolFactory This;

	static public SocketConnectionPoolFactory factory() {
		if (This == null) {
			synchronized (SocketConnectionPoolFactory.class) {
				if (This == null) {
					This = new SocketConnectionPoolFactory() {
					};
				}
			}
		}
		return This;
	}

	public SocketConnectionPool createSocketConnectionPool(
			final ClientBuildingConfig clientBuildingConfig) throws Throwable {
		if (clientBuildingConfig == null)
			throw new NullPointerException("The client config is illegal.");

		SocketConnectionPool socketConnectionPool = null;
		SocketConnectionPoolProvider _socketConnectionProvider;
		final Iterator<SocketConnectionPoolProvider> serviceLoaderIterator = serviceLoader
				.iterator();
		while (serviceLoaderIterator.hasNext()) {
			_socketConnectionProvider = serviceLoaderIterator.next();
			if (!(_socketConnectionProvider == null))
				return socketConnectionPool = _socketConnectionProvider
						.provider(clientBuildingConfig);
		}
		return socketConnectionPool;
	}
}
