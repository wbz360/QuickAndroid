package com.appdsn.qa.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.support.annotation.NonNull;

/**
 * 加密与解密的工具类
 *
 * Created by baozhong 2016/02/01
 *
 */
public final class CipherUtils {

	/**
	 * SHA-256 加密
	 * 
	 * @param data
	 * @return
	 */
	public static String sha256(@NonNull String data) {

		byte[] hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data.getBytes("UTF-8"));
			hash = md.digest();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes2hex2(hash);
	}

	/**
	 * MD5加密
	 */
	public static String md5(String data) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					data.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		return bytes2hex1(hash);
	}

	/**
	 * HMAC 加密 MAC算法可选以下多种算法 HmacMD5 HmacSHA1 HmacSHA256 HmacSHA384 HmacSHA512
	 * 
	 * 
	 */
	public static String HmacSHA256(String data, String key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(secretKey);
		byte[] hash = mac.doFinal(data.getBytes());

		return bytes2hex2(hash);
	}

	public static String bytes2hex1(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	public static String bytes2hex2(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		final String HEX = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			// 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(HEX.charAt((b >> 4) & 0x0f));
			// 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(HEX.charAt(b & 0x0f));
		}

		return sb.toString();
	}

	public static String bytes2hex3(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		StringBuilder hex = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public static String bytes2hex4(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		/**
		 * 第一个参数的解释，记得一定要设置为1 signum of the number (-1 for negative, 0 for zero,
		 * 1 for positive).
		 */
		BigInteger bigInteger = new BigInteger(1, bytes);
		return bigInteger.toString(16);
	}

}
