/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;

import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public class SocketConnectionProxy extends AbstractQueuedSynchronizer implements
		SocketConnection {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2713764671634469626L;
	final private String identity_;
	private boolean alive_;
	final private String hostName_;
	final private int port_;
	final private TNonblockingTransport transport_;

	public SocketConnectionProxy(final String hostName, final int port,
			final int tcpConnectionTimeout) throws IOException {
		this.identity_ = UUID.randomUUID().toString();
		this.hostName_ = hostName;
		this.port_ = port;
		this.transport_ = new TNonblockingSocket(hostName, port,
				tcpConnectionTimeout);
		this.alive_ = true;
	}

	protected boolean isHeldExclusively() {
		return getState() == 1;
	}

	protected boolean tryAcquire(int acquires) {
		assert acquires == 1; // Otherwise unused
		if (compareAndSetState(0, acquires)) {
			setExclusiveOwnerThread(Thread.currentThread());
			return true;
		}
		return false;
	}

	protected boolean tryRelease(int releases) {
		assert releases == 1; // Otherwise unused
		if (getState() == 0)
			throw new IllegalMonitorStateException();
		setExclusiveOwnerThread(null);
		setState(0);
		return true;

	}

	// Contains a conditional queue
	Condition newCondition() {
		return new ConditionObject();
	}

	@Override
	public boolean isIdle() {
		return tryAcquire(1) && isAlive();
	}

	@Override
	public TNonblockingTransport get() {
		// TODO Auto-generated method stub
		return this.transport_;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			this.release(0);
		} catch (final Throwable ignored) {
		}
	}

	@Override
	public String getIdentity() {
		// TODO Auto-generated method stub
		return this.identity_;
	}

	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		return this.alive_;
	}

	@Override
	public void setAlive(boolean alive) {
		this.alive_ = alive;
	}

	@Override
	public String getHostName() {
		return this.hostName_;
	}

	@Override
	public int getPort() {
		return this.port_;
	}

}
