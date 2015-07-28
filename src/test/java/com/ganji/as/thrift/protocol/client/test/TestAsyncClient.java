package com.ganji.as.thrift.protocol.client.test;
/**
 * 
 */


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * @author yikangfeng
 * @date   2015年7月23日 
 */
public class TestAsyncClient {
	 public static void main(String[] args) throws Exception {
		 TSocket socket;
	       try {
	           TAsyncClientManager clientManager = new TAsyncClientManager();
	           TNonblockingTransport transport = new TNonblockingSocket(
	                   "192.168.35.131",8020);
	           TProtocolFactory protocol = new TBinaryProtocol.Factory();
	           PostLimitServiceFinagle.AsyncClient asyncClient = new  PostLimitServiceFinagle.AsyncClient.Factory(clientManager, protocol).getAsyncClient(transport);
	           MethodCallback callBack=new MethodCallback();
	           final String param = "{\"category_script_index\":4,\"majorcategory_script_index\":-1,\"category_id\":2,\"city_code\":-1,\"user_id\":2}";
	           asyncClient.getPostLimit(param,"async-client", callBack);
	           System.out.println("Client calls .....");
	           Object res = callBack.getResult();
	           System.out.println("res=");
	           System.out.println((( PostLimitServiceFinagle.AsyncClient.getPostLimit_call) res)
	                   .getResult());
	           System.out.println("end");
	           
	           transport.close();
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
	 }
}
