/**
 * 
 */
package com.ganji.as.thrift.protocol.cluster.load.balance;

import java.util.List;

import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNode;

/**
 * @author yikangfeng
 * @date 2015年7月21日
 */
public interface LoadBalance {
	ServerNode select(final List<ServerNode> serverNodes,final ThriftClientInvocation clientInvocation) throws Throwable;
}
