/**
 * 
 */
package com.ganji.as.thrift.protocol.cluster.load.balance;

import java.util.List;

import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNode;

/**
 * @author yikangfeng
 * @date 2015年7月22日
 */
public abstract class AbstractLoadBalance implements LoadBalance {

	@Override
	public ServerNode select(final List<ServerNode> serverNodes,final ThriftClientInvocation clientInvocation)
			throws Throwable {
		// TODO Auto-generated method stub
		if (serverNodes == null || serverNodes.isEmpty())
			return null;
		if (serverNodes.size() == 1)
			return serverNodes.get(0);
		return doSelect(serverNodes,clientInvocation);
	}

	abstract ServerNode doSelect(final List<ServerNode> serverNodes,final ThriftClientInvocation clientInvocation);
}
