package com.pintu.pub.util;

import java.security.*;

public class HMAC_MD5 {
	/**
	 * Digest to be returned upon completion of the HMAC_MD5.
	 */
	private byte digest[];

	/**
	 * Inner Padding.
	 */
	private byte kIpad[];

	/**
	 * Outer Padding.
	 */
	private byte kOpad[];

	/**
	 * Outer and general purpose MD5 object.
	 */
	private MessageDigest md5;
	/**
	 * Inner MD5 object.
	 */
	private MessageDigest innerMD5;

	/**
	 * Constructor
	 * 
	 * @throws NoSuchAlgorithmException
	 *             if the MD5 implementation can't be found. If this occurs or
	 *             you need a faster implementation see www.bouncycastle.org for
	 *             something much better.
	 */
	public HMAC_MD5(byte key[]) throws NoSuchAlgorithmException {
		md5 = MessageDigest.getInstance("MD5");
		innerMD5 = MessageDigest.getInstance("MD5");
		int kLen = key.length;

		// if key is longer than 64 bytes reset it to key=MD5(key)
		if (kLen > 64) {
			md5.update(key);
			key = md5.digest();
		}

		kIpad = new byte[64]; // inner padding - key XORd with ipad

		kOpad = new byte[64]; // outer padding - key XORd with opad

		// start out by storing key in pads
		System.arraycopy(key, 0, kIpad, 0, kLen);
		System.arraycopy(key, 0, kOpad, 0, kLen);

		// XOR key with ipad and opad values
		for (int i = 0; i < 64; i++) {
			kIpad[i] ^= 0x36;
			kOpad[i] ^= 0x5c;
		}

		clear(); // Initialize the first digest.
	}

	/**
	 * Clear the HMAC_MD5 object.
	 */
	public void clear() {
		innerMD5.reset();
		innerMD5.update(kIpad); // Intialize the inner pad.

		digest = null; // mark the digest as incomplete.
	}

	/**
	 * HMAC_MD5 function.
	 * 
	 * @param text
	 *            Text to process
	 * 
	 * @param key
	 *            Key to use for HMAC hash.
	 * 
	 * @return hash
	 */
	public void addData(byte text[]) {
		addData(text, 0, text.length);
	}

	/**
	 * HMAC_MD5 function.
	 * 
	 * @param text
	 *            Text to process
	 * 
	 * @param textStart
	 *            Start position of text in text buffer.
	 * @param textLen
	 *            Length of text to use from text buffer.
	 * @param key
	 *            Key to use for HMAC hash.
	 * 
	 * @return hash
	 */
	public void addData(byte text[], int textStart, int textLen) {
		innerMD5.update(text, textStart, textLen); // then text of datagram.
	}

	public byte[] sign() {
		md5.reset();

		/*
		 * the HMAC_MD5 transform looks like:
		 * 
		 * MD5(K XOR opad, MD5(K XOR ipad, text))
		 * 
		 * where K is an n byte key ipad is the byte 0x36 repeated 64 times opad
		 * is the byte 0x5c repeated 64 times and text is the data being
		 * protected
		 */

		// Perform inner MD5

		digest = innerMD5.digest(); // finish up 1st pass.

		// Perform outer MD5

		md5.reset(); // Init md5 for 2nd pass.
		md5.update(kOpad); // Use outer pad.
		md5.update(digest); // Use results of first pass.
		digest = md5.digest(); // Final result.

		return digest;
	}

	/**
	 * Validate a signature against the current digest. Compares the hash
	 * against the signature.
	 * 
	 * @param signature
	 * 
	 * @return True if the signature matches the calculated hash.
	 */
	public boolean verify(byte signature[]) {
		// The digest may not have been calculated. If it's null, force a
		// calculation.
		if (digest == null)
			sign();

		int sigLen = signature.length;
		int digLen = digest.length;

		if (sigLen != digLen)
			return false; // Different lengths, not a good sign.

		for (int i = 0; i < sigLen; i++)
			if (signature[i] != digest[i])
				return false; // Mismatch. Misfortune.

		return true; // Signatures matched. Perseverance furthers.
	}

	/**
	 * Return the digest as a HEX string.
	 * 
	 * @return a hex representation of the MD5 digest.
	 */
	public String toString() {
		// If not already calculated, do so.
		if (digest == null)
			sign();

		StringBuffer r = new StringBuffer();
		final String hex = "0123456789ABCDEF";
		byte b[] = digest;

		for (int i = 0; i < 16; i++) {
			int c = ((b[i]) >>> 4) & 0xf;
			r.append(hex.charAt(c));
			c = ((int) b[i] & 0xf);
			r.append(hex.charAt(c));
		}

		return r.toString();
	}
}
