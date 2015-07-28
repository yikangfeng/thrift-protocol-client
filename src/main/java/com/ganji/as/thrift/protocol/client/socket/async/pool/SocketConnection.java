/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import org.apache.thrift.transport.TNonblockingTransport;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public interface SocketConnection extends AutoCloseable {
	String getHostName();

	int getPort();

	boolean isIdle();

	boolean isAlive();

	void setAlive(final boolean alive);

	String getIdentity();

	TNonblockingTransport get();
}
