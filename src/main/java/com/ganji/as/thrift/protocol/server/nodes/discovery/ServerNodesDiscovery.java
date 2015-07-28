/**
 * 
 */
package com.ganji.as.thrift.protocol.server.nodes.discovery;

import java.util.List;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public interface ServerNodesDiscovery extends AutoCloseable {
	List<ServerNode> nodesDiscovery(final String zkFullPath) throws Throwable;

	List<ServerNode> nodes();

	void close() throws Exception;

}
