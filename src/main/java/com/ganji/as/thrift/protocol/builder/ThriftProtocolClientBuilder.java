/**
 * 
 */
package com.ganji.as.thrift.protocol.builder;

import java.net.SocketAddress;

import org.apache.thrift.protocol.TProtocolFactory;
import org.slf4j.Logger;

import com.ganji.as.thrift.protocol.client.intf.ThriftProtocolClientRetryPolicy;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolService;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public abstract class ThriftProtocolClientBuilder {
	static public <REQ, REP> ThriftProtocolService<REQ, REP> safeBuild(
			final ThriftProtocolClientBuilder clientBuilder) {
		if (clientBuilder == null)
			throw new NullPointerException(
					"Construction parameter object is not valid.");
		try {
			return clientBuilder.<REQ, REP> build();
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	static public ThriftProtocolClientBuilder get() {
		return new ThriftProtocolClientBuilding();
	}

	abstract public ThriftProtocolClientBuilder name(final String name);

	abstract public ThriftProtocolClientBuilder logger(final Logger logger);

	abstract public ThriftProtocolClientBuilder tcpConnectTimeout(
			final int tcpConnectTimeout);// ms

	abstract public ThriftProtocolClientBuilder sendBufferSize(final int value);

	abstract public ThriftProtocolClientBuilder recvBufferSize(final int value);

	abstract public ThriftProtocolClientBuilder hosts(
			final SocketAddress inetSocketAddress);

	abstract public ThriftProtocolClientBuilder dest(final String addr);

	abstract public ThriftProtocolClientBuilder codec(
			final TProtocolFactory protocolFactory);

	abstract public ThriftProtocolClientBuilder retries(final int value);

	abstract public ThriftProtocolClientBuilder retryPolicy(
			final ThriftProtocolClientRetryPolicy retryPolicy);

	abstract public ThriftProtocolClientBuilder loadBalancePolicy(
			final LoadBalance loadBalancePolicy);

	abstract public ThriftProtocolClientBuilder hostConnectionCoreSize(
			final int value);

	abstract public ThriftProtocolClientBuilder hostConnectionMinIdle(
			final int value);// min idle size
	
	abstract public ThriftProtocolClientBuilder hostConnectionMaxIdle(
			final int value);// max idle size
	
	abstract public ThriftProtocolClientBuilder hostConnectionLimit(
			final int value);// max size

	abstract public ThriftProtocolClientBuilder hostConnectionMaxWaiters(
			final int nWaiters);
	
	abstract public ThriftProtocolClientBuilder maxWaitHostConnectionMillis(
			final int maxWaitMillis);

	abstract public ThriftProtocolClientBuilder hostConnectionBufferSize(
			final int bufferSize);

	abstract public ThriftProtocolClientBuilder hostConnectionIdleTime(
			final int duration);

	abstract public ThriftProtocolClientBuilder hostConnectionMaxIdleTime(
			final int duration);

	abstract public ThriftProtocolClientBuilder hostConnectionMaxLifeTime(
			final int duration);

	abstract <REQ, REP> ThriftProtocolService<REQ, REP> build()
			throws Throwable;

}
