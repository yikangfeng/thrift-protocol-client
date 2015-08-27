package com.ganji.as.thrift.protocol.finagle.client.test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganji.as.thrift.protocol.builder.ThriftProtocolClientBuilder;
import com.ganji.as.thrift.protocol.client.future.Future;
import com.ganji.as.thrift.protocol.client.request.ThriftClientRequest;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolFunction;
import com.ganji.as.thrift.protocol.service.intf.ThriftProtocolService;
import com.ganji.as.thrift.protocol.client.test.AchieveResult;
import com.ganji.as.thrift.protocol.client.test.AntiSpamProcess;
import com.ganji.as.thrift.protocol.client.test.PostLimitServiceFinagle;

public class ThriftProtocolServeTest1 {

	final private Logger LOGGER = LoggerFactory
			.getLogger(ThriftProtocolServeTest1.class);
	final TProtocolFactory FACTORY = new TBinaryProtocol.Factory();

	public void testApp1() throws Throwable {
		final ThriftProtocolService<ThriftClientRequest, byte[]> serve_ = ThriftProtocolClientBuilder
				.safeBuild(ThriftProtocolClientBuilder
						.get()
						.codec(FACTORY)
						.retries(6)
						.dest("zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/as.synccombo.thrift!0")
						.hostConnectionLimit(2)
						.logger(LoggerFactory
								.getLogger("thrift-post-limit-service-client")));

		Thread thread1 = Executors.defaultThreadFactory().newThread(
				new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (true) {
							try {
								call(serve_);
							} catch (TException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				});

		Thread thread2 = Executors.defaultThreadFactory().newThread(
				new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (true) {
							try {
								call(serve_);
							} catch (TException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				});

		Thread thread3 = Executors.defaultThreadFactory().newThread(
				new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (true) {
							try {
								call(serve_);
							} catch (TException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				});

		Thread thread4 = Executors.defaultThreadFactory().newThread(
				new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (true) {
							try {
								call(serve_);
							} catch (TException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				});

		Thread thread5 = Executors.defaultThreadFactory().newThread(
				new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (true) {
							try {
								call(serve_);
							} catch (TException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				});

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
	}

	private void call(
			final ThriftProtocolService<ThriftClientRequest, byte[]> serve_)
			throws TException {
		TMemoryBuffer __memoryTransport__ = new TMemoryBuffer(512);
		TProtocol __prot__ = FACTORY.getProtocol(__memoryTransport__);
		__prot__.writeMessageBegin(new TMessage("get", TMessageType.CALL, 0));
		PostLimitServiceFinagle.getPostLimit_args __args__ = new PostLimitServiceFinagle.getPostLimit_args();
		String param = "{\"biztype\":\"feedcomment\",\"opetype\":\"get\",\"data\":{\"type\":\"feedcomment\",\"userid\":\"784529\",\"ip\":\"3232238870\",\"cookie\":\"2549519450401865457677-654644261\",\"content\":\"老乡说抽取习近平调试\",\"clienttype\":\"801\",\"useragent\":\"\"}}";
		__args__.setStrJSONParam(param);
		__args__.setToken("test-call");
		__args__.write(__prot__);
		__prot__.writeMessageEnd();

		byte[] __buffer__ = Arrays.copyOfRange(__memoryTransport__.getArray(),
				0, __memoryTransport__.length());
		final ThriftClientRequest __request__ = new ThriftClientRequest(
				__buffer__, false);

		Future<byte[]> future = serve_.apply(__request__);

		Future<AchieveResult> r = future
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

		try {
			final AchieveResult result = r.get();
			System.out.println("result score=" + result.getScore());
			System.out.println("result detail=" + result.getDetail());
			LOGGER.debug("result=" + result.toString());
		} catch (final Throwable t) {
			t.printStackTrace();
		}

	}

	static public void main(String[] args) {
		ThriftProtocolServeTest1 test = new ThriftProtocolServeTest1();
		try {
			test.testApp1();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			TimeUnit.HOURS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
