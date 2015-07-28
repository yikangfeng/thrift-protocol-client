/**
 * 
 */
package com.ganji.as.thrift.protocol.cluster.load.balance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author yikangfeng
 * @date 2015年7月22日
 */
public class MD5HashFunction implements HashFunction {
	final private MessageDigest md5;

	public MD5HashFunction() {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("no md5 algorythm found");
		}
	}

	public long hash(final String key) {
		md5.reset();
		md5.update(key.getBytes());
		byte[] bKey = md5.digest();
		long res = ((long) (bKey[3] & 0xFF) << 24)
				| ((long) (bKey[2] & 0xFF) << 16)
				| ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
		return res & 0xffffffffL;
	}
}
