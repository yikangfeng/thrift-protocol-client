/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public interface SocketConnectionPoolFactory {
	SocketConnectionPool createSocketConnectionPool(final ClientBuildingConfig clientBuildingConfig) throws Throwable;
}
