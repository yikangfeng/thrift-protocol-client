package com.ganji.as.thrift.protocol.finagle.client.test;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganji.as.thrift.protocol.builder.ClientBuildingConfig;
import com.ganji.as.thrift.protocol.builder.ThriftProtocolClientBuilder;
import com.ganji.as.thrift.protocol.client.future.Future;
import com.ganji.as.thrift.protocol.client.request.ThriftClientRequest;
import com.ganji.as.thrift.protocol.cluster.load.balance.ConsistentHashLoadBalance;
import com.ganji.as.thrift.protocol.service.ThriftProtocolServe;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolFunction;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolService;
import com.ganji.as.thrift.protocol.client.test.AchieveResult;
import com.ganji.as.thrift.protocol.client.test.AntiSpamProcess;
import com.ganji.as.thrift.protocol.client.test.OperateResult;

import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ThriftProtocolServeTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public ThriftProtocolServeTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ThriftProtocolServeTest.class);
	}

	/**
	 * Rigourous Test :-)
	 * 
	 * @throws Throwable
	 */
	final private Logger LOGGER = LoggerFactory
			.getLogger(ThriftProtocolServeTest.class);
	final TProtocolFactory FACTORY = new TBinaryProtocol.Factory();

	public void testApp1() throws Throwable {
		final ThriftProtocolService<ThriftClientRequest, byte[]> serve_ = ThriftProtocolClientBuilder
				.safeBuild(ThriftProtocolClientBuilder
						.get()
						.codec(FACTORY)
						.hosts(new InetSocketAddress("192.168.2.119", 8020))
						.hostConnectionLimit(2)
						.logger(LoggerFactory
								.getLogger("thrift-post-limit-service-client")));

		TMemoryBuffer __memoryTransport__ = new TMemoryBuffer(512);
		TProtocol __prot__ = FACTORY.getProtocol(__memoryTransport__);
		__prot__.writeMessageBegin(new TMessage("getPostLimit",
				TMessageType.CALL, 0));
		PostLimitServiceFinagle.getPostLimit_args __args__ = new PostLimitServiceFinagle.getPostLimit_args();
		__args__.setStrJSONParam("{\"category_script_index\":4,\"user_id\":2,\"category_id\":2,\"majorcategory_script_index\":-1,\"city_code\":-1}");
		__args__.setToken("test-call");
		__args__.write(__prot__);
		__prot__.writeMessageEnd();

		byte[] __buffer__ = Arrays.copyOfRange(__memoryTransport__.getArray(),
				0, __memoryTransport__.length());
		final ThriftClientRequest __request__ = new ThriftClientRequest(
				__buffer__, false);

		Future<byte[]> future = serve_.apply(__request__);

		Future<OperateResult> r = future
				.flatMap(new ThriftProtocolFunction<byte[], com.ganji.as.thrift.protocol.client.future.Future<OperateResult>>() {

					public com.ganji.as.thrift.protocol.client.future.Future<OperateResult> apply(
							byte[] __buffer__) {
						// TODO Auto-generated method stub
						TMemoryInputTransport __memoryTransportReturn__ = new TMemoryInputTransport(
								__buffer__);
						TProtocol __protReturn__ = FACTORY
								.getProtocol(__memoryTransportReturn__);
						try {
							OperateResult operateResult = (new PostLimitServiceFinagle.Client(
									__protReturn__)).recv_getPostLimit();

							return Future.value(operateResult);
						} catch (Exception e) {
							return Future.exception(e);
						}
					}

				});

		final OperateResult result = r.get();
		Assert.assertNotNull(result);
		System.out.println("result=" + result.toString());
		LOGGER.debug("result=" + result.toString());
	}

	public void testApp() throws Throwable {

		final ThriftProtocolService<ThriftClientRequest, byte[]> serve = ThriftProtocolClientBuilder
				.safeBuild(ThriftProtocolClientBuilder
						.get()
						.codec(FACTORY)
						.dest("zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/as.postlimitservice.thrift!0")
						.hostConnectionLimit(2)
						.logger(LoggerFactory
								.getLogger("thrift-post-limit-service-client")));

		final ThriftProtocolService<ThriftClientRequest, byte[]> serve2 = ThriftProtocolClientBuilder
				.safeBuild(ThriftProtocolClientBuilder
						.get()
						.codec(FACTORY)
						.dest("zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/as.whitelist.thrift!0")
						.hostConnectionLimit(2)
						.logger(LoggerFactory
								.getLogger("socket-white-list-client")));

		final ThriftProtocolService<ThriftClientRequest, byte[]> serve3 = ThriftProtocolClientBuilder
				.safeBuild(ThriftProtocolClientBuilder
						.get()
						.codec(FACTORY)
						.dest("zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/as.bannedwords.thrift!0")
						.hostConnectionLimit(2)
						.logger(LoggerFactory
								.getLogger("socket-bannedwords-client")));

		final ThriftProtocolService<ThriftClientRequest, byte[]> serve4 = ThriftProtocolClientBuilder
				.safeBuild(ThriftProtocolClientBuilder
						.get()
						.codec(FACTORY)
						.dest("zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/as.extract.thrift!0")
						.hostConnectionLimit(2)
						.logger(LoggerFactory
								.getLogger("socket-extract-client")));

		for (int i = 0; i < 10; ++i) {

			TMemoryBuffer __memoryTransport__ = new TMemoryBuffer(512);
			TProtocol __prot__ = FACTORY.getProtocol(__memoryTransport__);
			__prot__.writeMessageBegin(new TMessage("getPostLimit",
					TMessageType.CALL, 0));
			PostLimitServiceFinagle.getPostLimit_args __args__ = new PostLimitServiceFinagle.getPostLimit_args();
			__args__.setStrJSONParam("{\"category_script_index\":4,\"user_id\":2,\"category_id\":2,\"majorcategory_script_index\":-1,\"city_code\":-1}");
			__args__.setToken("test-call");
			__args__.write(__prot__);
			__prot__.writeMessageEnd();

			byte[] __buffer__ = Arrays.copyOfRange(
					__memoryTransport__.getArray(), 0,
					__memoryTransport__.length());
			final ThriftClientRequest __request__ = new ThriftClientRequest(
					__buffer__, false);

			Future<byte[]> future = serve.apply(__request__);

			Future<OperateResult> r = future
					.flatMap(new ThriftProtocolFunction<byte[], com.ganji.as.thrift.protocol.client.future.Future<OperateResult>>() {

						public com.ganji.as.thrift.protocol.client.future.Future<OperateResult> apply(
								byte[] __buffer__) {
							// TODO Auto-generated method stub
							TMemoryInputTransport __memoryTransportReturn__ = new TMemoryInputTransport(
									__buffer__);
							TProtocol __protReturn__ = FACTORY
									.getProtocol(__memoryTransportReturn__);
							try {
								OperateResult operateResult = (new PostLimitServiceFinagle.Client(
										__protReturn__)).recv_getPostLimit();

								return Future.value(operateResult);
							} catch (Exception e) {
								return Future.exception(e);
							}
						}

					});

			final OperateResult result = r.get();
			Assert.assertNotNull(result);
			System.out.println("result=" + result.toString());
			LOGGER.debug("result=" + result.toString());

			TMemoryBuffer __memoryTransport1__ = new TMemoryBuffer(512);
			TProtocol __prot1__ = FACTORY.getProtocol(__memoryTransport1__);
			__prot1__.writeMessageBegin(new TMessage("get", TMessageType.CALL,
					0));
			PostLimitServiceFinagle.getPostLimit_args __args1__ = new PostLimitServiceFinagle.getPostLimit_args();
			__args1__
					.setStrJSONParam("{\"whitelisttype\":\"Feed\",\"categoryindex\":-1,\"majorcategoryindex\":-1,\"username\":\"\",phone:\"\",qq:\"\",ip:\"\",userid:\"78459\",msn:\"\",cookie:\"\",companyname:\"\",companylocation:\"\",email:\"\"}");
			__args1__.setToken("test-white-list-call");
			__args1__.write(__prot1__);
			__prot1__.writeMessageEnd();

			byte[] __buffer1__ = Arrays.copyOfRange(
					__memoryTransport1__.getArray(), 0,
					__memoryTransport1__.length());
			final ThriftClientRequest __request1__ = new ThriftClientRequest(
					__buffer1__, false);

			Future<byte[]> future1 = serve2.apply(__request1__);

			Future<AchieveResult> r1 = future1
					.flatMap(new ThriftProtocolFunction<byte[], com.ganji.as.thrift.protocol.client.future.Future<AchieveResult>>() {

						public com.ganji.as.thrift.protocol.client.future.Future<AchieveResult> apply(
								byte[] __buffer__) {
							// TODO Auto-generated method stub
							TMemoryInputTransport __memoryTransportReturn__ = new TMemoryInputTransport(
									__buffer__);
							TProtocol __protReturn__ = FACTORY
									.getProtocol(__memoryTransportReturn__);
							try {
								AchieveResult operateResult = (new AntiSpamProcess.Client(
										__protReturn__)).recv_get();

								return Future.value(operateResult);
							} catch (Exception e) {
								return Future.exception(e);
							}
						}

					});

			final AchieveResult result1 = r1.get();
			Assert.assertNotNull(result1);
			System.out.println("result1=" + result1.toString());
			LOGGER.debug("result1=" + result1.toString());

			TMemoryBuffer __memoryTransport3__ = new TMemoryBuffer(512);
			TProtocol __prot3__ = FACTORY.getProtocol(__memoryTransport3__);
			__prot3__.writeMessageBegin(new TMessage("get", TMessageType.CALL,
					0));
			PostLimitServiceFinagle.getPostLimit_args __args3__ = new PostLimitServiceFinagle.getPostLimit_args();
			__args3__
					.setStrJSONParam("{\"sourcekey\":\"webim\",\"contents\":[{\"columnkey\":\"MainSiteMessage\",\"categoryindex\":-1,\"majorcategoryindex\":-1,\"badwordlevel\":-1,\"columnvalue\":\"我的IM小大保健学\",\"citycode\":-1}]}");
			__args3__.setToken("test-banned-words-call");
			__args3__.write(__prot3__);
			__prot3__.writeMessageEnd();

			byte[] __buffer3__ = Arrays.copyOfRange(
					__memoryTransport3__.getArray(), 0,
					__memoryTransport3__.length());
			final ThriftClientRequest __request3__ = new ThriftClientRequest(
					__buffer3__, false);

			Future<byte[]> future3 = serve3.apply(__request3__);

			Future<AchieveResult> r3 = future3
					.flatMap(new ThriftProtocolFunction<byte[], com.ganji.as.thrift.protocol.client.future.Future<AchieveResult>>() {

						public com.ganji.as.thrift.protocol.client.future.Future<AchieveResult> apply(
								byte[] __buffer__) {
							// TODO Auto-generated method stub
							TMemoryInputTransport __memoryTransportReturn__ = new TMemoryInputTransport(
									__buffer__);
							TProtocol __protReturn__ = FACTORY
									.getProtocol(__memoryTransportReturn__);
							try {
								AchieveResult operateResult = (new AntiSpamProcess.Client(
										__protReturn__)).recv_get();

								return Future.value(operateResult);
							} catch (Exception e) {
								return Future.exception(e);
							}
						}

					});

			final AchieveResult result3 = r3.get();
			Assert.assertNotNull(result3);
			System.out.println("result1=" + result3.toString());
			LOGGER.debug("result1=" + result3.toString());

			TMemoryBuffer __memoryTransport4__ = new TMemoryBuffer(512);
			TProtocol __prot4__ = FACTORY.getProtocol(__memoryTransport4__);
			__prot4__.writeMessageBegin(new TMessage("get", TMessageType.CALL,
					0));
			PostLimitServiceFinagle.getPostLimit_args __args4__ = new PostLimitServiceFinagle.getPostLimit_args();
			__args4__
					.setStrJSONParam("{contents: [{columnkey:\"content\",content:\"老乡说抽取13158745985调试\",type:\"qq|phone|url\",ignoreganji:\"true\"}]}");
			__args4__.setToken("test-call");
			__args4__.write(__prot4__);
			__prot4__.writeMessageEnd();

			byte[] __buffer4__ = Arrays.copyOfRange(
					__memoryTransport4__.getArray(), 0,
					__memoryTransport4__.length());
			final ThriftClientRequest __request4__ = new ThriftClientRequest(
					__buffer4__, false);

			Future<byte[]> future4 = serve4.apply(__request4__);

			Future<AchieveResult> r4 = future4
					.flatMap(new ThriftProtocolFunction<byte[], com.ganji.as.thrift.protocol.client.future.Future<AchieveResult>>() {

						public com.ganji.as.thrift.protocol.client.future.Future<AchieveResult> apply(
								byte[] __buffer__) {
							// TODO Auto-generated method stub
							TMemoryInputTransport __memoryTransportReturn__ = new TMemoryInputTransport(
									__buffer__);
							TProtocol __protReturn__ = FACTORY
									.getProtocol(__memoryTransportReturn__);
							try {
								AchieveResult operateResult = (new AntiSpamProcess.Client(
										__protReturn__)).recv_get();

								return Future.value(operateResult);
							} catch (Exception e) {
								return Future.exception(e);
							}
						}

					});

			final AchieveResult result4 = r4.get();
			Assert.assertNotNull(result4);
			System.out.println("result1=" + result4.toString());
			LOGGER.debug("result1=" + result4.toString());

		}

		serve.close();
	}

}
