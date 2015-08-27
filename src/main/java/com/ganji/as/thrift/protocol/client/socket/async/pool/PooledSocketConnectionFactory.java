/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author yikangfeng
 * @date 2015年8月27日
 */
class PooledSocketConnectionFactory implements
		PooledObjectFactory<SocketConnection> {

	static public PooledObjectFactory<SocketConnection> factory(
			final String hostName, final int port,
			final int tcpConnectionTimeout) {
		return new PooledSocketConnectionFactory(hostName, port,
				tcpConnectionTimeout);
	}

	final private String hostName_;
	final private int port_;
	final private int tcpConnectionTimeout_;

	private PooledSocketConnectionFactory(final String hostName,
			final int port, final int tcpConnectionTimeout) {
		this.hostName_ = hostName;
		this.port_ = port;
		this.tcpConnectionTimeout_ = tcpConnectionTimeout;
	}

	@Override
	public PooledObject<SocketConnection> makeObject() throws Exception {
		// TODO Auto-generated method stub
		return new DefaultPooledObject<SocketConnection>(
				new SocketConnectionProxy(this.hostName_, this.port_,
						this.tcpConnectionTimeout_));
	}

	@Override
	public void destroyObject(final PooledObject<SocketConnection> p)
			throws Exception {
		// TODO Auto-generated method stub
		SocketConnection socketConnectionProxy = p.getObject();
		socketConnectionProxy.setAlive(false);
		socketConnectionProxy.get().close();
		socketConnectionProxy.close();
		socketConnectionProxy = null;// Help GC.
	}

	@Override
	public boolean validateObject(final PooledObject<SocketConnection> p) {
		// TODO Auto-generated method stub
		return p.getObject() != null && p.getObject().isAlive()
				&& p.getObject().get().isOpen();
	}

	@Override
	public void activateObject(final PooledObject<SocketConnection> p)
			throws Exception {
		// TODO Auto-generated method stub
		final SocketConnection socketConnectionProxy = p.getObject();
		socketConnectionProxy.setAlive(true);
	}

	@Override
	public void passivateObject(final PooledObject<SocketConnection> p)
			throws Exception {
		// TODO Auto-generated method stub
		final SocketConnection socketConnectionProxy = p.getObject();
		socketConnectionProxy.setAlive(false);
	}

}
