package com.ganji.as.thrift.protocol.finagle.client.test;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.slf4j.LoggerFactory;

import com.ganji.as.thrift.protocol.builder.ThriftProtocolClientBuilder;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolService;
import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.thrift.ThriftClientFramedCodec;
import com.twitter.finagle.thrift.ThriftClientRequest;
import com.twitter.util.Duration;

/**
 * 
 * @author zhangzhenyu
 *
 */
public abstract class BaseFinagleClient {

	private final static int HOST_MODE = 0;// host+port模式
	private final static int ZKPATH_MODE = 1;// zkPath模式
	protected static final int AWAIT_TIME = 10000;// 等待回调超时时间限制，单位毫秒
	private static Map<String, Service<ThriftClientRequest, byte[]>> hashCache = new HashMap<String, Service<ThriftClientRequest, byte[]>>();
	protected int cur_mode;

	protected String host;
	protected int port;
	protected String zkPath;

	public void init(String _host, int _port) {
		this.host = _host;
		this.port = _port;
		this.cur_mode = HOST_MODE;
		getServiceToClientRefrence();
	}

	public void init(String _zkPath) {
		this.zkPath = _zkPath;
		this.cur_mode = ZKPATH_MODE;
		getServiceToClientRefrence();
	}

	public void clear() {

	}

	protected TProtocolFactory getTProtocolFactory() {
		return new TBinaryProtocol.Factory();
	}

	private String getCacheKey() {
		StringBuffer sbKey = new StringBuffer();
		sbKey.append(this.cur_mode);
		sbKey.append("-");
		sbKey.append(this.host);
		sbKey.append("-");
		sbKey.append(this.port);
		sbKey.append("-");
		sbKey.append(this.zkPath);
		sbKey.append("-");
		return sbKey.toString();
	}

	protected Service<ThriftClientRequest, byte[]> getClientRequest() {
		if (this.cur_mode == HOST_MODE
				&& (StringUtils.isBlank(this.host) || this.port == 0))
			throw new RuntimeException("param is invalide");
		if (this.cur_mode == ZKPATH_MODE && (StringUtils.isBlank(this.zkPath)))
			throw new RuntimeException("param is invalide");

		String cacheKey = getCacheKey();
		if (hashCache.containsKey(cacheKey)
				&& hashCache.get(cacheKey).isAvailable())
			return hashCache.get(cacheKey);
		Service<ThriftClientRequest, byte[]> clientRequest = null;
		switch (this.cur_mode) {
		case HOST_MODE:
			InetSocketAddress inetSocketAddress = new InetSocketAddress(
					this.host, this.port);
			clientRequest = ClientBuilder.safeBuild(ClientBuilder.get()
					.hosts(inetSocketAddress)
					.codec(ThriftClientFramedCodec.get()).retries(3)
					.tcpConnectTimeout(Duration.fromSeconds(30))
					.hostConnectionLimit(100));
			break;
		case ZKPATH_MODE:
			clientRequest = ClientBuilder.safeBuild(ClientBuilder.get()
					.dest(this.zkPath).codec(ThriftClientFramedCodec.get())
					.retries(3).tcpConnectTimeout(Duration.fromSeconds(30))
					.hostConnectionLimit(100).keepAlive(true));
			break;
		default:
			break;
		}
		hashCache.put(cacheKey, clientRequest);
		return clientRequest;

	}

	static final private ConcurrentMap<String, ThriftProtocolService<com.ganji.as.thrift.protocol.client.request.ThriftClientRequest, byte[]>> serviceOfCache = new ConcurrentHashMap<>();

	protected ThriftProtocolService<com.ganji.as.thrift.protocol.client.request.ThriftClientRequest, byte[]> getServiceToClientRefrence() {
		if (this.cur_mode == HOST_MODE
				&& (StringUtils.isBlank(this.host) || this.port == 0))
			throw new RuntimeException("param is invalide");
		if (this.cur_mode == ZKPATH_MODE && (StringUtils.isBlank(this.zkPath)))
			throw new RuntimeException("param is invalide");

		final String cacheKey = getCacheKey();
		if (serviceOfCache.containsKey(cacheKey))
			return serviceOfCache.get(cacheKey);

		ThriftProtocolService<com.ganji.as.thrift.protocol.client.request.ThriftClientRequest, byte[]> serviceToClientRefrence = null;
		switch (this.cur_mode) {
		case HOST_MODE:
			final InetSocketAddress inetSocketAddress = new InetSocketAddress(
					this.host, this.port);
			serviceToClientRefrence = ThriftProtocolClientBuilder
					.safeBuild(ThriftProtocolClientBuilder
							.get()
							.codec(new TBinaryProtocol.Factory())
							.retries(6)
							.hosts(inetSocketAddress)
							.hostConnectionLimit(2)
							.logger(LoggerFactory
									.getLogger("thrift-protocol-client")));
			break;
		case ZKPATH_MODE:
			serviceToClientRefrence = ThriftProtocolClientBuilder
					.safeBuild(ThriftProtocolClientBuilder
							.get()
							.codec(new TBinaryProtocol.Factory())
							.retries(6)
							.dest(this.zkPath)
							.hostConnectionLimit(2)
							.logger(LoggerFactory
									.getLogger("thrift-protocol-client")));
			break;
		default:
			break;
		}
		serviceOfCache.putIfAbsent(cacheKey, serviceToClientRefrence);
		return serviceToClientRefrence;

	}

	/* added by yikangfeng */
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (final String key : serviceOfCache.keySet()) {
					if (key == null || key.isEmpty())
						continue;
					if (!(serviceOfCache.get(key) == null)) {
						try {
							serviceOfCache.get(key).close();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				serviceOfCache.clear();
			}

		});
	}
}
