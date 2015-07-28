package com.ganji.as.thrift.protocol.client.test;
/**
 * 
 */


import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.ganji.as.thrift.protocol.client.test.OperationException;
import com.ganji.as.thrift.protocol.client.test.ParamterException;

public class TestClient {

	public void startClient() throws IOException {
		TTransport transport;
		TSocket socket;
		try {
			socket = new TSocket("192.168.35.131", 8020);
		    transport = new TFramedTransport(socket);
			TBinaryProtocol protocol = new TBinaryProtocol(transport);

			PostLimitServiceFinagle.Client client = new PostLimitServiceFinagle.Client(
					protocol);
			transport.open();
			String param = "{\"category_script_index\":4,\"majorcategory_script_index\":-1,\"category_id\":2,\"city_code\":-1,\"user_id\":2}";
			OperateResult result = null;
			try {
				result = client.getPostLimit(param, "test");
			} catch (ParamterException | OperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			transport.close();

			System.out.println(result.getData());
			System.out.println(result.isSuccess());
			System.out.println(result.getErrorMsg());
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		TestClient client = new TestClient();
		client.startClient();
	}

}