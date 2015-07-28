/**
 * 
 */
package com.ganji.as.thrift.protocol.server.nodes.discovery;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.framework.state.ConnectionStateListener;
import com.netflix.curator.retry.ExponentialBackoffRetry;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public class ServerNodesDiscoveryProvider implements ServerNodesDiscovery {
	private final List<ServerNode> serverNodes_ = new ArrayList<>();
	private CuratorFramework zkClient_;
	static private final String ZK_FULL_PATH_DELIMITER = "!";
	static private final String ZK_FULL_PATH_NAMESPACE_SEPERATOR = "/";
	static private final Charset ZK_CHAR_SET = Charset.forName("UTF-8");
	private String zkHost_;
	private String zkNamespace_;
	private String zkNodePath_;

	final private Logger logger_;

	public ServerNodesDiscoveryProvider(
			final ClientBuildingConfig clientBuildingConfig) {
		this.logger_ = clientBuildingConfig.getLogger();
	}

	@Override
	public List<ServerNode> nodesDiscovery(final String zkFullPath)
			throws Throwable {
		// TODO Auto-generated method stub
		if (zkFullPath == null || zkFullPath.isEmpty())// host mode
			return this.nodes();

		if (zkClient_ == null) {
			synchronized (this) {
				if (zkClient_ == null)
					buildZKClient(zkFullPath);
			}
		}

		return doNodesDiscovery();
	}

	private List<ServerNode> doNodesDiscovery() throws Exception {
		if (logger_ != null) {
			if (logger_.isInfoEnabled())
				logger_.info("nodes discovery start...");
		}

		final List<String> serverNodes = registerPathNodeDataChangeWatcher(zkNodePath_);
		List<ServerNode> newestServerNodes = new ArrayList<>();
		for (final String nodeKey : serverNodes) {
			final byte[] nodeValueBytes = zkClient_.getData().forPath(
					String.format("%s/%s", zkNodePath_, nodeKey));
			if (nodeValueBytes == null || nodeValueBytes.length <= 0)
				continue;
			final String nodeValueInfo = new String(nodeValueBytes, ZK_CHAR_SET);
			final ServerNodeInfo serverNode = JSONObject.parseObject(
					nodeValueInfo, ServerNodeInfo.class);
			newestServerNodes.add(serverNode);
		}
		if (serverNodes != null)
			serverNodes.clear();

		synchronized (this) {
			this.serverNodes_.clear();
			this.serverNodes_.addAll(newestServerNodes);

			if (logger_ != null) {
				if (logger_.isInfoEnabled())
					logger_.info(String.format(
							"current discovery's server nodes count (%d)",
							this.serverNodes_.size()));
			}
		}

		if (logger_ != null) {
			if (logger_.isInfoEnabled())
				logger_.info("nodes discovery end...");

		}
		return this.nodes();
	}

	private Watcher createChildNodeChangeWatcher() {
		return new Watcher() {
			@Override
			public void process(final WatchedEvent event) {
				// TODO Auto-generated method stub
				if (event == null)
					return;
				if (event.getState() == Event.KeeperState.SyncConnected) {
					switch (event.getType()) {
					case NodeChildrenChanged: {
						try {
							doNodesDiscovery();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if (logger_ != null) {
								if (logger_.isInfoEnabled())
									logger_.info(String
											.format("To re acquire of active server nodes generate an error, cause:%s",
													e));
							}
						}
						break;
					}
					default:
						break;
					}
				}
			}

		};
	}

	private List<String> registerPathNodeDataChangeWatcher(final String path)
			throws Exception {
		return zkClient_.getChildren()
				.usingWatcher(createChildNodeChangeWatcher()).forPath(path);
	}

	private void buildZKClient(final String zkFullPath) {
		final String[] zkFullPathItems = zkFullPath
				.split(ZK_FULL_PATH_DELIMITER);
		zkHost_ = zkFullPathItems[1];
		zkNamespace_ = zkFullPathItems[2].substring(0, zkFullPathItems[2]
				.lastIndexOf(ZK_FULL_PATH_NAMESPACE_SEPERATOR));
		zkNodePath_ = zkFullPathItems[2].substring(zkFullPathItems[2]
				.lastIndexOf(ZK_FULL_PATH_NAMESPACE_SEPERATOR));
		if (zkClient_ == null) {
			try {
				zkClient_ = CuratorFrameworkFactory.builder()
						.connectString(zkHost_).namespace(zkNamespace_)
						.retryPolicy(new ExponentialBackoffRetry(1000, 3))
						.connectionTimeoutMs(20000).build();
				zkClient_.getConnectionStateListenable().addListener(
						new ConnectionStateListener() {

							@Override
							public void stateChanged(CuratorFramework client,
									ConnectionState newState) {
								// TODO Auto-generated method stub
								if (newState == ConnectionState.LOST) {
									try {
										synchronized (this) {
											while (!client
													.getZookeeperClient()
													.blockUntilConnectedOrTimedOut()) {
												if (logger_ != null) {
													if (logger_.isInfoEnabled())
														logger_.info("waitting reconnected...");
												}
												wait(1000);
											}
										}
										if (logger_ != null) {
											if (logger_.isInfoEnabled())
												logger_.info("to zookeeper reconnected.");
										}
										doNodesDiscovery();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										if (logger_ != null) {
											if (logger_.isInfoEnabled())
												logger_.info(String
														.format("To re connect to zookeeper to generate an error, cause:%s",
																e));
										}
									}
								}
							}

						});
				zkClient_.start();
			} catch (Throwable t) {
				zkClient_.close();
				throw new IllegalStateException(
						"building zookeeper client failed.");
			}

		}
	}

	static public ServerNodesDiscoveryFactory FACTORY = new ServerNodesDiscoveryFactory() {

		@Override
		public ServerNodesDiscovery createServerNodesDiscovery(
				final ClientBuildingConfig clientBuildingConfig) {
			// TODO Auto-generated method stub
			if (clientBuildingConfig == null)
				throw new NullPointerException(
						"The parameter clientBuildingConfig is not valid.");
			return new ServerNodesDiscoveryProvider(clientBuildingConfig);
		}

	};

	@Override
	public List<ServerNode> nodes() {
		// TODO Auto-generated method stub
		synchronized (this) {
			return Collections.unmodifiableList(this.serverNodes_);
		}
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (this.zkClient_ != null)
			this.zkClient_.close();
		if (this.serverNodes_ != null)
			this.serverNodes_.clear();

	}

	static public void main(String[] args) {
		try {
			String zkFullPath1 = "zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/as.postlimitservice.thrift!0";
			ServerNodesDiscoveryProvider.FACTORY.createServerNodesDiscovery(
					new ClientBuildingConfig()).nodesDiscovery(zkFullPath1);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
