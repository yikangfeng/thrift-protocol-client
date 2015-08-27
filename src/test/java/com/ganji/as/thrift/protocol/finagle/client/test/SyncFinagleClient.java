package com.ganji.as.thrift.protocol.finagle.client.test;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ganji.as.thrift.protocol.client.test.AchieveResult;
import com.ganji.as.thrift.protocol.client.test.AntiSpamProcess;
import com.ganji.as.thrift.protocol.client.test.OperationException;
import com.ganji.as.thrift.protocol.client.test.ParamterException;
import com.ganji.as.thrift.protocol.client.test.ResultScore;


public class SyncFinagleClient extends BaseFinagleClient implements ISyncClient {

	private static Logger LOGGER = LoggerFactory.getLogger("SyncFinagleClient");

	@Override
	public com.ganji.as.thrift.protocol.client.test.AchieveResult get(final String param, final String token)
			throws ParamterException, OperationException, TException {

		AntiSpamProcess.AbstractService serviceToClient = new AntiSpamProcess.ServiceToFinagleClient(
				getServiceToClientRefrence(), getTProtocolFactory());
		AchieveResult aResult = new AchieveResult();
		// 赋值为了保证超时时各个属性值有值
		aResult.setIsShot(false);
		aResult.setScore(ResultScore.VALIDATE);
		aResult.setDetail("");
		com.ganji.as.thrift.protocol.client.future.Future<AchieveResult> result = serviceToClient
				.get_(param, token);
		try {
			aResult = result.get();
			return aResult;
		} catch (Throwable e) {
			LOGGER.error("SyncFinagleClient&get excption.", e);
		}
		return aResult;
	}

}
