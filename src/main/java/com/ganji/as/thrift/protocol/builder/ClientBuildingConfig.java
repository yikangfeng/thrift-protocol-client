/**
 * 
 */
package com.ganji.as.thrift.protocol.builder;

import java.net.SocketAddress;

import org.apache.thrift.protocol.TProtocolFactory;
import org.slf4j.Logger;

import com.ganji.as.thrift.protocol.client.intf.ThriftProtocolClientRetryPolicy;
import com.ganji.as.thrift.protocol.cluster.load.balance.LoadBalance;

/**
 * @author yikangfeng
 * @date 2015年7月20日
 */
public class ClientBuildingConfig {

	private String clientName;

	private Logger logger;

	private int tcpConnectTimeout;

	private int sendBufferSize;
	private int recvBufferSize;

	private SocketAddress hosts;

	private String destAddr;

	private TProtocolFactory codec;

	private int retries = 3;

	private ThriftProtocolClientRetryPolicy retryPolicy;

	private LoadBalance loadBalancePolicy;

	private int hostConnectionCoreSize = 4;// per host initial size

	private int hostConnectionMinIdle = 4;

	private int hostConnectionMaxIdle = 8;

	private int hostConnectionLimit = 8;// max total or max active

	private int hostConnectionMaxWaiters;

	private int maxWaitHostConnectionMillis = Integer.MAX_VALUE;// Millisecond

	private int hostConnectionIdleTime;

	private int hostConnectionMaxIdleTime;

	private int hostConnectionMaxLifeTime = 1800;// seconds

	private int hostConnectionBufferSize;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public int getTcpConnectTimeout() {
		return tcpConnectTimeout;
	}

	public void setTcpConnectTimeout(int tcpConnectTimeout) {
		this.tcpConnectTimeout = tcpConnectTimeout;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getRecvBufferSize() {
		return recvBufferSize;
	}

	public void setRecvBufferSize(int recvBufferSize) {
		this.recvBufferSize = recvBufferSize;
	}

	public SocketAddress getHosts() {
		return hosts;
	}

	public void setHosts(SocketAddress hosts) {
		this.hosts = hosts;
	}

	public String getDestAddr() {
		return destAddr;
	}

	public void setDestAddr(String destAddr) {
		this.destAddr = destAddr;
	}

	public TProtocolFactory getCodec() {
		return codec;
	}

	public void setCodec(TProtocolFactory codec) {
		this.codec = codec;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public ThriftProtocolClientRetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	public void setRetryPolicy(ThriftProtocolClientRetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	public int getHostConnectionCoreSize() {
		return hostConnectionCoreSize;
	}

	public void setHostConnectionCoreSize(int hostConnectionCoresize) {
		this.hostConnectionCoreSize = hostConnectionCoresize;
	}

	public int getHostConnectionLimit() {
		return hostConnectionLimit;
	}

	public void setHostConnectionLimit(int hostConnectionLimit) {
		this.hostConnectionLimit = hostConnectionLimit;
	}

	public int getHostConnectionIdleTime() {
		return hostConnectionIdleTime;
	}

	public void setHostConnectionIdleTime(int hostConnectionIdleTime) {
		this.hostConnectionIdleTime = hostConnectionIdleTime;
	}

	public int getHostConnectionMaxWaiters() {
		return hostConnectionMaxWaiters;
	}

	public void setHostConnectionMaxWaiters(int hostConnectionMaxWaiters) {
		this.hostConnectionMaxWaiters = hostConnectionMaxWaiters;
	}

	public int getHostConnectionMaxIdleTime() {
		return hostConnectionMaxIdleTime;
	}

	public void setHostConnectionMaxIdleTime(int hostConnectionMaxIdleTime) {
		this.hostConnectionMaxIdleTime = hostConnectionMaxIdleTime;
	}

	public int getHostConnectionMaxLifeTime() {
		return hostConnectionMaxLifeTime;
	}

	public void setHostConnectionMaxLifeTime(int hostConnectionMaxLifeTime) {
		this.hostConnectionMaxLifeTime = hostConnectionMaxLifeTime;
	}

	public int getHostConnectionBufferSize() {
		return hostConnectionBufferSize;
	}

	public void setHostConnectionBufferSize(int hostConnectionBufferSize) {
		this.hostConnectionBufferSize = hostConnectionBufferSize;
	}

	public LoadBalance getLoadBalancePolicy() {
		return loadBalancePolicy;
	}

	public void setLoadBalancePolicy(LoadBalance loadBalancePolicy) {
		this.loadBalancePolicy = loadBalancePolicy;
	}

	public int getHostConnectionMaxIdle() {
		return hostConnectionMaxIdle;
	}

	public void setHostConnectionMaxIdle(int hostConnectionMaxIdle) {
		this.hostConnectionMaxIdle = hostConnectionMaxIdle;
	}

	public int getMaxWaitHostConnectionMillis() {
		return maxWaitHostConnectionMillis;
	}

	public void setMaxWaitHostConnectionMillis(int maxWaitHostConnectionMillis) {
		this.maxWaitHostConnectionMillis = maxWaitHostConnectionMillis;
	}

	public int getHostConnectionMinIdle() {
		return hostConnectionMinIdle;
	}

	public void setHostConnectionMinIdle(int hostConnectionMinIdle) {
		this.hostConnectionMinIdle = hostConnectionMinIdle;
	}

}
