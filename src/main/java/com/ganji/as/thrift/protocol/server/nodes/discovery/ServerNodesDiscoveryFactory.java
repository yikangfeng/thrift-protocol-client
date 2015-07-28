/**
 * 
 */
package com.ganji.as.thrift.protocol.server.nodes.discovery;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public interface ServerNodesDiscoveryFactory {
	ServerNodesDiscovery createServerNodesDiscovery(final ClientBuildingConfig clientBuildingConfig);
}
