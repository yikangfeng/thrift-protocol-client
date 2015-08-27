package com.ganji.as.thrift.protocol.finagle.client.test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class FinagleTest{

	public static void main(String[] args) throws InterruptedException{
		String token="toas_asd_102";
		String zkFullPath="";
		zkFullPath="zk!bw-kvm-cy-01.dns.ganji.com:2181!/soa/services/as.synccombo.thrift!0";
		SyncFinagleClient syncClient = new SyncFinagleClient();
		syncClient.init(zkFullPath);
		MyThread thread = new MyThread(syncClient,token,"线程1");
		MyThread thread2 = new MyThread(syncClient,token,"线程2");
		MyThread thread3 = new MyThread(syncClient,token,"线程3");
		MyThread thread4 = new MyThread(syncClient,token,"线程4");
		MyThread thread5 = new MyThread(syncClient,token,"线程5");
		Thread demo= new Thread(thread);
		Thread demo2= new Thread(thread2);
		Thread demo3= new Thread(thread3);
		Thread demo4= new Thread(thread4);
		Thread demo5= new Thread(thread5);
		demo.start();
		demo2.start();
		demo3.start();
		demo4.start();
		demo5.start();
		
		
		TimeUnit.HOURS.sleep(20);
		
	}
	
	
}
