/**
 * 
 */
package com.ganji.as.thrift.protocol.client.socket.async.pool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;
import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNode;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNodesDiscovery;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNodesDiscoveryProvider;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public class SocketConnectionPoolProvider implements SocketConnectionPool,
		AutoCloseable {
	final private Logger LOGGER_;
	final private ClientBuildingConfig clientBuildingConfig_;
	final private AtomicBoolean isReady_ = new AtomicBoolean(false);
	final int tcpConnectionTimeout_;
	final private ConcurrentMap<String, ConcurrentMap<String/* identity */, SocketConnection>> transportPool_ = new ConcurrentHashMap<>();
	final private ServerNodesDiscovery serverNodesDiscovery_;

	static public enum ConnectionMode {
		HOST, CLUSTER
	}

	private ConnectionMode connectionMode_;

	public SocketConnectionPoolProvider(
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

	static final public SocketConnectionPoolFactory FACTORY = new SocketConnectionPoolFactory() {

		@Override
		public SocketConnectionPool createSocketConnectionPool(
				final ClientBuildingConfig clientBuildingConfig)
				throws Throwable {
			// TODO Auto-generated method stub
			return new SocketConnectionPoolProvider(clientBuildingConfig);
		}

	};

	private void prepare() throws Throwable {
		if (!(this.clientBuildingConfig_.getHosts() == null))
			this.connectionMode_ = ConnectionMode.HOST;
		if (!(this.clientBuildingConfig_.getDestAddr() == null || this.clientBuildingConfig_
				.getDestAddr().isEmpty()))
			this.connectionMode_ = ConnectionMode.CLUSTER;

		if (this.connectionMode_ == null)
			throw new IllegalStateException("Hosts or dest needs at least one.");

		int hostConnectionCoreSize = this.clientBuildingConfig_
				.getHostConnectionCoreSize();
		final InetSocketAddress hostAddress = (InetSocketAddress) this.clientBuildingConfig_
				.getHosts();

		final List<ServerNode> serverNodes = serverNodesDiscovery_.nodes();

		while (hostConnectionCoreSize > 0) {

			ConcurrentMap<String, SocketConnection> transports = null;

			if (this.connectionMode_ == ConnectionMode.HOST) {

				final String key = String.format("%s:%s",
						hostAddress.getHostName(), hostAddress.getPort());

				transports = transportPool_.get(key);

				if (transports == null || transports.isEmpty())
					transports = new ConcurrentHashMap<>();

				final SocketConnection proxy = createSocketConnection(
						hostAddress.getHostName(), hostAddress.getPort());
				transports.put(proxy.getIdentity(), proxy);

				transportPool_.putIfAbsent(key, transports);

			} else if (this.connectionMode_ == ConnectionMode.CLUSTER) {
				if (serverNodes != null && !serverNodes.isEmpty()) {
					for (final ServerNode serverNode_ : serverNodes) {
						if (serverNode_ == null)
							continue;
						final String key = String.format("%s:%s",
								serverNode_.getHost(), serverNode_.getPort());

						if (transports == null || transports.isEmpty())
							transports = new ConcurrentHashMap<>();

						final SocketConnection proxy = createSocketConnection(
								serverNode_.getHost(), serverNode_.getPort());
						transports.put(proxy.getIdentity(), proxy);

						transportPool_.putIfAbsent(key, transports);
					}
				}
			}

			hostConnectionCoreSize--;
		}

		isReady_.getAndSet(true);

		if (this.LOGGER_ != null) {
			if (this.LOGGER_.isInfoEnabled())
				this.LOGGER_
						.info("The socket connection pool prepare complete.");
		}
	}

	private SocketConnection createSocketConnection(final String hostName,
			final int port) throws IOException {

		return new SocketConnectionProxy(hostName, port,
				this.clientBuildingConfig_.getTcpConnectTimeout());
	}

	@Override
	public SocketConnection getSocketConnection(final LoadBalance loadBalance,
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
			socketConnection = getSocketConnectionByHostAndPort(
					selectedServerNode.getHost(), selectedServerNode.getPort());

		}
		return socketConnection;
	}

	@Override
	public SocketConnection getSocketConnectionByHostAndPort(final String host,
			final int port) throws Throwable {
		// TODO Auto-generated method stub
		if (host == null || host.isEmpty() || port <= 0)
			throw new IllegalArgumentException(
					"The parameter host or port is not valid.");
		final String hostAndPort = String.format("%s:%s", host, port);
		final ConcurrentMap<String, SocketConnection> transports = this.transportPool_
				.get(hostAndPort);

		for (final SocketConnection socketConnection : transports.values()) {
			if (socketConnection.isIdle())
				return socketConnection;
		}

		final int hostConnectionLimit = this.clientBuildingConfig_
				.getHostConnectionLimit();

		final int currentHostConnectionCount = transports.size();

		if (currentHostConnectionCount < hostConnectionLimit) {
			// create a new connection.
			final SocketConnection newSocketConnection = createSocketConnection(
					host, port);
			transports.putIfAbsent(newSocketConnection.getIdentity(),
					newSocketConnection);
			if (newSocketConnection.isIdle())
				return newSocketConnection;
		} else
			throw new NullPointerException(
					"No idle socket connection is obtained.");

		return null;

	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		final Collection<ConcurrentMap<String, SocketConnection>> values = this.transportPool_
				.values();
		if (values != null) {
			for (final ConcurrentMap<String, SocketConnection> map : values) {
				if (map == null || map.isEmpty())
					continue;
				for (final SocketConnection socketConnection : map.values()) {
					if (socketConnection == null)
						continue;
					socketConnection.get().close();
				}
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
	public boolean isReady() {
		// TODO Auto-generated method stub
		return isReady_.get();
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		this.destory();
	}

	@Override
	public void removeSocketConnection(final SocketConnection socketConnection) {
		// TODO Auto-generated method stub
		final String hostName = socketConnection.getHostName();
		final int port = socketConnection.getPort();
		final String socketConnectionIdentity = socketConnection.getIdentity();
		final ConcurrentMap<String, SocketConnection> connections = this.transportPool_
				.get(String.format("%s:%s", hostName, port));
		if (connections == null || connections.isEmpty())
			return;
		connections.remove(socketConnectionIdentity);
	}

}
