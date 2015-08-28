package com.ganji.as.thrift.protocol.client.socket.async.pool;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;
import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.client.socket.async.pool.SimpleSocketConnectionPoolProviderImpl.ConnectionMode;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNode;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNodesDiscovery;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNodesDiscoveryProvider;

/**
 * @author yikangfeng
 * @date 2015年8月27日
 */
class GracefulSocketConnectionPoolProviderImpl extends
		AbstractSocketConnectionPool implements AutoCloseable {
	final private ConcurrentMap<String, ObjectPool<SocketConnection>> transportPool_ = new ConcurrentHashMap<>();
	final private Logger LOGGER_;
	final private ClientBuildingConfig clientBuildingConfig_;
	final private AtomicBoolean isReady_ = new AtomicBoolean(false);
	final int tcpConnectionTimeout_;

	final private ServerNodesDiscovery serverNodesDiscovery_;
	private ConnectionMode connectionMode_;

	public GracefulSocketConnectionPoolProviderImpl(
			final ClientBuildingConfig clientBuildingConfig) throws Throwable {
		this.LOGGER_ = clientBuildingConfig.getLogger();
		this.clientBuildingConfig_ = clientBuildingConfig;
		this.serverNodesDiscovery_ = ServerNodesDiscoveryProvider.FACTORY
				.createServerNodesDiscovery(this.clientBuildingConfig_);
		this.serverNodesDiscovery_.nodesDiscovery(this.clientBuildingConfig_
				.getDestAddr());
		this.tcpConnectionTimeout_ = clientBuildingConfig
				.getTcpConnectTimeout();

		prepare();
	}

	private void prepare() throws Throwable {
		if (!(this.clientBuildingConfig_.getHosts() == null))
			this.connectionMode_ = ConnectionMode.HOST;
		if (!(this.clientBuildingConfig_.getDestAddr() == null || this.clientBuildingConfig_
				.getDestAddr().isEmpty()))
			this.connectionMode_ = ConnectionMode.CLUSTER;

		if (this.connectionMode_ == null)
			throw new IllegalStateException("Hosts or dest needs at least one.");

		final InetSocketAddress hostAddress = (InetSocketAddress) this.clientBuildingConfig_
				.getHosts();

		final int tcpConnectionTimeout = this.clientBuildingConfig_
				.getTcpConnectTimeout();

		if (this.connectionMode_ == ConnectionMode.HOST)
			prepareHostModeTransportPool(hostAddress, tcpConnectionTimeout);
		else if (this.connectionMode_ == ConnectionMode.CLUSTER)
			prepareClusterModeTransportPool(tcpConnectionTimeout);

		isReady_.getAndSet(true);

		if (this.LOGGER_ != null) {
			if (this.LOGGER_.isInfoEnabled())
				this.LOGGER_
						.info("The socket connection pool prepare complete.");
		}
	}

	private void prepareHostModeTransportPool(
			final InetSocketAddress hostAddress, final int tcpConnectionTimeout)
			throws Exception {
		final String key = String.format("%s:%s", hostAddress.getHostName(),
				hostAddress.getPort());

		ObjectPool<SocketConnection> transportPool = transportPool_.get(key);

		if (transportPool == null)
			transportPool = createSocketConnectionPool(
					hostAddress.getHostName(), hostAddress.getPort(),
					this.clientBuildingConfig_.getTcpConnectTimeout());

		prepareTransportPool(transportPool,
				this.clientBuildingConfig_.getHostConnectionCoreSize());
		transportPool_.putIfAbsent(key, transportPool);
	}

	private void prepareClusterModeTransportPool(final int tcpConnectionTimeout)
			throws Exception {
		final List<ServerNode> serverNodes = serverNodesDiscovery_.nodes();
		if (serverNodes == null || serverNodes.isEmpty()) {
			for (final ServerNode serverNode_ : serverNodes) {
				if (serverNode_ == null)
					continue;
				final String key = String.format("%s:%s",
						serverNode_.getHost(), serverNode_.getPort());

				ObjectPool<SocketConnection> transportPool = transportPool_
						.get(key);

				if (transportPool == null)
					transportPool = createSocketConnectionPool(
							serverNode_.getHost(), serverNode_.getPort(),
							this.clientBuildingConfig_.getTcpConnectTimeout());

				prepareTransportPool(transportPool,
						this.clientBuildingConfig_.getHostConnectionCoreSize());

				transportPool_.putIfAbsent(key, transportPool);
			}
		}
	}

	static private void prepareTransportPool(
			final ObjectPool<SocketConnection> transportPool,
			int transportPoolInitialSocketConnectionSize) throws Exception {
		if (transportPool == null)
			return;
		if (transportPoolInitialSocketConnectionSize <= 0)
			((GenericObjectPool<SocketConnection>) transportPool).preparePool();
		else {
			do {
				transportPool.addObject();
			} while ((--transportPoolInitialSocketConnectionSize) == 0);// false
																		// exit.
		}
	}

	private ObjectPool<SocketConnection> createSocketConnectionPool(
			final String hostName, final int port,
			final int tcpConnectionTimeout) {
		return new GenericObjectPool<SocketConnection>(
				getPooledSocketConnectionFactory(hostName, port,
						tcpConnectionTimeout),
				(GenericObjectPoolConfig) createSocketConnectionPoolConfig(),
				createSocketConnectionAbandonedConfig());
	}

	private PooledObjectFactory<SocketConnection> getPooledSocketConnectionFactory(
			final String hostName, final int port,
			final int tcpConnectionTimeout) {
		return PooledSocketConnectionFactory.factory(hostName, port,
				tcpConnectionTimeout);
	}

	private BaseObjectPoolConfig createSocketConnectionPoolConfig() {
		final GenericObjectPoolConfig socketConnectionPoolConfig = new GenericObjectPoolConfig();
		socketConnectionPoolConfig.setMaxTotal(this.clientBuildingConfig_
				.getHostConnectionLimit());
		if (this.clientBuildingConfig_.getHostConnectionCoreSize() > 0)
			socketConnectionPoolConfig.setMinIdle(this.clientBuildingConfig_
					.getHostConnectionCoreSize());
		if (this.clientBuildingConfig_.getHostConnectionMaxIdle() > 0)
			socketConnectionPoolConfig.setMaxIdle(this.clientBuildingConfig_
					.getHostConnectionMaxIdle());
		socketConnectionPoolConfig.setMaxWaitMillis(this.clientBuildingConfig_
				.getMaxWaitHostConnectionMillis());
		socketConnectionPoolConfig.setNumTestsPerEvictionRun(Integer.MAX_VALUE);
		socketConnectionPoolConfig.setMinEvictableIdleTimeMillis(this.clientBuildingConfig_
				.getHostConnectionMinIdle());
		socketConnectionPoolConfig.setTimeBetweenEvictionRunsMillis(1 * 60000L);
		return socketConnectionPoolConfig;
	}

	private AbandonedConfig createSocketConnectionAbandonedConfig() {
		final AbandonedConfig socketConnectionAbandonedConfig = new AbandonedConfig();
		socketConnectionAbandonedConfig.setRemoveAbandonedOnBorrow(true);
		socketConnectionAbandonedConfig.setRemoveAbandonedOnMaintenance(true);
		if (this.clientBuildingConfig_.getHostConnectionMaxLifeTime() > 0)
			socketConnectionAbandonedConfig
					.setRemoveAbandonedTimeout(this.clientBuildingConfig_
							.getHostConnectionMaxLifeTime());

		return socketConnectionAbandonedConfig;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return isReady_.get();
	}

	@Override
	public void removeSocketConnection(final SocketConnection socketConnection) {
		// TODO Auto-generated method stub
		if (socketConnection == null)
			return;
		final String hostName = socketConnection.getHostName();
		final int port = socketConnection.getPort();
		final ObjectPool<SocketConnection> transportPool = this.transportPool_
				.get(String.format("%s:%s", hostName, port));
		if (transportPool == null)
			return;

		try {
			transportPool.invalidateObject(socketConnection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (this.LOGGER_ != null) {
				if (this.LOGGER_.isInfoEnabled())
					this.LOGGER_.info(
							"Error while removing socket connection,reasons.",
							e);
			}
		}
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		final Collection<ObjectPool<SocketConnection>> values = this.transportPool_
				.values();
		if (values != null && !values.isEmpty()) {
			for (final ObjectPool<SocketConnection> transportPool : values) {
				if (transportPool == null)
					continue;
				transportPool.close();
			}
		}
		this.transportPool_.clear();

		if (this.serverNodesDiscovery_ != null) {
			try {
				this.serverNodesDiscovery_.close();
			} catch (final Exception ignored) {
			}
		}
	}

	@Override
	protected SocketConnection getInternalSocketConnection(
			final LoadBalance loadBalance,
			final ThriftClientInvocation clientInvocation) throws Throwable {
		// TODO Auto-generated method stub
		SocketConnection socketConnection = null;
		if (this.connectionMode_ == ConnectionMode.HOST) {
			final InetSocketAddress hostAddress = (InetSocketAddress) this.clientBuildingConfig_
					.getHosts();
			socketConnection = getSocketConnectionByHostAndPort(
					hostAddress.getHostName(), hostAddress.getPort());
		} else if (this.connectionMode_ == ConnectionMode.CLUSTER) {
			final List<ServerNode> serverNodes = serverNodesDiscovery_.nodes();
			final ServerNode selectedServerNode = loadBalance.select(
					serverNodes, clientInvocation);
			if (this.LOGGER_ != null) {
				if (this.LOGGER_.isInfoEnabled())
					this.LOGGER_.info(String.format(
							"Load balancing strategy selected node is {%s}",
							selectedServerNode));
			}
			if (selectedServerNode == null)
				return socketConnection;

			socketConnection = getSocketConnectionByHostAndPort(
					selectedServerNode.getHost(), selectedServerNode.getPort());

		}
		return socketConnection;
	}

	@Override
	protected SocketConnection getInternalSocketConnectionByHostAndPort(
			final String host, final int port) throws Throwable {
		// TODO Auto-generated method stub
		if (host == null || host.isEmpty() || port <= 0)
			throw new IllegalArgumentException(
					"The parameter host or port is not valid.");

		final SocketConnection availableSocketConnection = internalAcquireSocketConnection(
				host, port);
		if (availableSocketConnection == null)
			throw new NullPointerException(
					"No idle socket connection is obtained.");

		return availableSocketConnection;
	}

	private SocketConnection internalAcquireSocketConnection(final String host,
			final int port) throws Exception {
		final String key = String.format("%s:%s", host, port);
		final int tcpConnectionTimeout = this.clientBuildingConfig_
				.getTcpConnectTimeout();
		ObjectPool<SocketConnection> transportPool = this.transportPool_
				.get(key);
		if (transportPool == null) {
			this.transportPool_.putIfAbsent(
					key,
					transportPool = createSocketConnectionPool(host, port,
							tcpConnectionTimeout));
			prepareTransportPool(transportPool,
					this.clientBuildingConfig_.getHostConnectionCoreSize());
		}
		try {
			return transportPool.borrowObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (this.LOGGER_ != null) {
				if (this.LOGGER_.isInfoEnabled())
					this.LOGGER_
							.info("Error while acquire socket connection from pool,reasons:",
									e);
			}
			return null;
		}
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		this.destory();
	}

	@Override
	public void returnSocketConnection(final SocketConnection socketConnection) {
		// TODO Auto-generated method stub
		if (socketConnection == null)
			return;
		final SocketConnection socketConnectionProxy = socketConnection;
		final String key = String.format("%s:%s",
				socketConnectionProxy.getHostName(),
				socketConnectionProxy.getPort());
		final ObjectPool<SocketConnection> transportPool = this.transportPool_
				.get(key);
		if (transportPool == null)
			return;
		try {
			transportPool.returnObject(socketConnection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (this.LOGGER_ != null) {
				if (this.LOGGER_.isInfoEnabled())
					this.LOGGER_
							.info("Error while return socket connection to pool,reasons:",
									e);
			}
		}
	}

}
