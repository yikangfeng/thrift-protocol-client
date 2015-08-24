# thrift-protocol-client
一个支持thrift协议的客户端工程，支持负载均衡、支持zookeeper service 节点自动发现、failover、异步连接池、异步调用模式等。
jdk version:jdk1.7+
QQ:814912127
QQ群:413012474
EMAIL:kangfeng01@126.com

build service to client example:

	final ThriftProtocolService<ThriftClientRequest, byte[]> serve = ThriftProtocolClientBuilder
				.safeBuild(ThriftProtocolClientBuilder
						.get()
						.codec(FACTORY)
						.dest("zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/test.postlimitservice.thrift!0")
						.hostConnectionLimit(2)
						.logger(LoggerFactory
								.getLogger("thrift-post-limit-service-client")));
