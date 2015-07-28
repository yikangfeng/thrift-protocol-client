/**
 * 
 */
package com.ganji.as.thrift.protocol.builder;

import java.net.SocketAddress;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.slf4j.Logger;

import com.ganji.as.thrift.protocol.client.intf.ThriftProtocolClientRetryPolicy;
import com.ganji.as.thrift.protocol.client.policy.session.retry.ThriftProtocolSessionRetryPolicy;
import com.ganji.as.thrift.protocol.cluster.load.balance.ConsistentHashLoadBalance;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolService;
import com.ganji.as.thrift.protocol.service.ThriftProtocolServiceProxy;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public class ThriftProtocolClientBuilding extends ThriftProtocolClientBuilder {
	private ClientBuildingConfig clientBuildingConfig_;
	static final private LoadBalance defaultLoadBalancePolicy_ = new ConsistentHashLoadBalance();
	static final private ThriftProtocolClientRetryPolicy defaultRetryPolicy_ = new ThriftProtocolSessionRetryPolicy();
	ThriftProtocolClientBuilding() {
		this.clientBuildingConfig_ = new ClientBuildingConfig();
		this.clientBuildingConfig_.setLoadBalancePolicy(defaultLoadBalancePolicy_);// set
																		// default
																		// load
																		// balance
																		// policy.
		this.clientBuildingConfig_.setRetryPolicy(defaultRetryPolicy_);
		
	}

	@Override
	public ThriftProtocolClientBuilder name(final String name) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setClientName(name);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder logger(final Logger logger) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setLogger(logger);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder tcpConnectTimeout(
			final int tcpConnectTimeout) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setTcpConnectTimeout(tcpConnectTimeout);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder sendBufferSize(final int value) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setSendBufferSize(value);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder recvBufferSize(final int value) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setRecvBufferSize(value);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hosts(
			final SocketAddress inetSocketAddress) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setHosts(inetSocketAddress);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder dest(final String addr) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setDestAddr(addr);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder codec(TProtocolFactory protocolFactory) {
		// TODO Auto-generated method stub
		if (protocolFactory == null)
			protocolFactory = new TBinaryProtocol.Factory();
		clientBuildingConfig_.setCodec(protocolFactory);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder retries(final int value) {
		// TODO Auto-generated method stub
		if (value <= 0)
			throw new IllegalArgumentException("The parameter value must be >0");
		clientBuildingConfig_.setRetries(value);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hostConnectionCoreSize(final int value) {
		// TODO Auto-generated method stub
		if (value < 0)
			throw new IllegalArgumentException(
					"The parameter value must be >=0");
		clientBuildingConfig_.setHostConnectionCoreSize(value);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hostConnectionLimit(final int value) {
		// TODO Auto-generated method stub
		if (value <= 0)
			throw new IllegalArgumentException("The parameter value must be >0");
		clientBuildingConfig_.setHostConnectionLimit(value);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hostConnectionIdleTime(final int duration) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setHostConnectionIdleTime(duration);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hostConnectionMaxWaiters(
			final int nWaiters) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setHostConnectionMaxWaiters(nWaiters);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hostConnectionMaxIdleTime(
			final int duration) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setHostConnectionIdleTime(duration);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hostConnectionMaxLifeTime(
			final int duration) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setHostConnectionMaxLifeTime(duration);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder hostConnectionBufferSize(
			final int bufferSize) {
		// TODO Auto-generated method stub
		clientBuildingConfig_.setHostConnectionBufferSize(bufferSize);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder retryPolicy(
			ThriftProtocolClientRetryPolicy retryPolicy) {
		// TODO Auto-generated method stub
		if (retryPolicy == null)
			throw new NullPointerException("The session retry policy is null.");

		clientBuildingConfig_.setRetryPolicy(retryPolicy);
		return this;
	}

	@Override
	public ThriftProtocolClientBuilder loadBalancePolicy(
			LoadBalance loadBalancePolicy) {
		// TODO Auto-generated method stub
		if (loadBalancePolicy == null)
			throw new NullPointerException("The load balance policy is null.");
		clientBuildingConfig_.setLoadBalancePolicy(loadBalancePolicy);
		return this;
	}

	@Override
	<REQ, REP> ThriftProtocolService<REQ, REP> build() throws Throwable {
		// TODO Auto-generated method stub
		return new ThriftProtocolServiceProxy<REQ, REP>(
				this.clientBuildingConfig_).proxy_;
	}

}
