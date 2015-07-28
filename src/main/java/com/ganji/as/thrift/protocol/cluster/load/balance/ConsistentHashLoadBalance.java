/**
 * 
 */
package com.ganji.as.thrift.protocol.cluster.load.balance;

/**
 * @author yikangfeng
 * @date   2015年7月22日 
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ganji.as.thrift.protocol.client.request.ThriftClientInvocation;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNode;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServerNodeInfo;
import com.ganji.as.thrift.protocol.server.nodes.discovery.ServiceEndpoint;

public class ConsistentHashLoadBalance extends AbstractLoadBalance {
	final private ConcurrentMap<String, ConsistentHashSelector<ServerNode>> selectors = new ConcurrentHashMap<>();
	final private Random random_ = new Random();
	final private HashFunction hashFunction = new MD5HashFunction();

	@Override
	ServerNode doSelect(final List<ServerNode> serverNodes,
			final ThriftClientInvocation clientInvocation) {
		// TODO Auto-generated method stub
		final String key = serverNodes.get(random_.nextInt(serverNodes.size()))
				.toString();
		final int identityHashCode = System.identityHashCode(serverNodes);
		ConsistentHashSelector<ServerNode> selector = selectors.get(key);
		if (selector == null
				|| selector.getIdentityHashCode() != identityHashCode) {
			selectors.put(key, new ConsistentHashSelector<ServerNode>(
					hashFunction, 160, serverNodes));
			selector = selectors.get(key);
		}

		return selector.get(Arrays.toString(clientInvocation.getMessage()));
	}

	private static class ConsistentHashSelector<T> {
		private final HashFunction hashFunction;
		private final int identityHashCode;
		private final int numberOfReplicas; // 虚拟节点
		private final SortedMap<Long, T> circle = new TreeMap<Long, T>(); // 用来存储虚拟节点hash值

		// 到真实node的映射

		public ConsistentHashSelector(HashFunction hashFunction,
				int numberOfReplicas, Collection<T> nodes) {
			this.hashFunction = hashFunction;
			this.numberOfReplicas = numberOfReplicas;
			this.identityHashCode = System.identityHashCode(nodes);
			for (T node : nodes) {
				add(node);
			}
		}

		public int getIdentityHashCode() {
			return identityHashCode;
		}

		public void add(T node) {
			for (int i = 0; i < numberOfReplicas; i++) {
				circle.put(hashFunction.hash(node.toString() + i), node);
			}
		}

		@SuppressWarnings("unused")
		public void remove(T node) {
			for (int i = 0; i < numberOfReplicas; i++) {
				circle.remove(hashFunction.hash(node.toString() + i));
			}
		}

		/**
		 * 获得一个最近的顺时针节点
		 * 
		 * @param key
		 *            为给定键取Hash，取得顺时针方向上最近的一个虚拟节点对应的实际节点
		 * @return
		 */
		public T get(Object key) {
			if (circle.isEmpty()) {
				return null;
			}
			long hash = hashFunction.hash((String) key);
			if (!circle.containsKey(hash)) {
				SortedMap<Long, T> tailMap = circle.tailMap(hash); // //返回此映射的部分视图，其键大于等于
				// hash
				hash = tailMap.isEmpty() ? circle.firstKey() : tailMap
						.firstKey();
			}
			return circle.get(hash);
		}

		@SuppressWarnings("unused")
		public long getSize() {
			return circle.size();
		}
	}

	static public void main(String[] args) {
		final LoadBalance loadBalance = new ConsistentHashLoadBalance();
		List<ServerNode> serverNodes = new ArrayList<>();

		ServerNodeInfo serverNodeInfo1 = new ServerNodeInfo();
		ServiceEndpoint serviceEndpoint1 = new ServiceEndpoint();
		serviceEndpoint1.setHost("host1");
		serviceEndpoint1.setPort(8081);
		serverNodeInfo1.setServiceEndpoint(serviceEndpoint1);

		ServerNodeInfo serverNodeInfo2 = new ServerNodeInfo();
		ServiceEndpoint serviceEndpoint2 = new ServiceEndpoint();
		serviceEndpoint2.setHost("host2");
		serviceEndpoint2.setPort(8082);
		serverNodeInfo2.setServiceEndpoint(serviceEndpoint2);

		ServerNodeInfo serverNodeInfo3 = new ServerNodeInfo();
		ServiceEndpoint serviceEndpoint3 = new ServiceEndpoint();
		serviceEndpoint3.setHost("host3");
		serviceEndpoint3.setPort(8083);
		serverNodeInfo3.setServiceEndpoint(serviceEndpoint3);

		serverNodes.add(serverNodeInfo1);
		serverNodes.add(serverNodeInfo2);
		serverNodes.add(serverNodeInfo3);
		for (int i = 0; i < 1000; ++i) {
			try {

				System.out.println(loadBalance.select(serverNodes, null));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}