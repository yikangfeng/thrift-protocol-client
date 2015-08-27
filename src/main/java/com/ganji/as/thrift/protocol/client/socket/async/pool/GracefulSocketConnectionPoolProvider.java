/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;

/**
 * @author yikangfeng
 * @date 2015年8月27日
 */
public class GracefulSocketConnectionPoolProvider implements
		SocketConnectionPoolProvider {

	@Override
	public SocketConnectionPool provider(
			final ClientBuildingConfig clientBuildingConfig) throws Throwable {
		// TODO Auto-generated method stub
		return new GracefulSocketConnectionPoolProviderImpl(
				clientBuildingConfig);
	}

}
