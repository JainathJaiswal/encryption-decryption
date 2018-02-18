package com.jai;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class PasswordGenerator {

	private SecureRandom srand;
	@SuppressWarnings("rawtypes")
	private ArrayList validch;

	@SuppressWarnings("unchecked")
	public PasswordGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
		srand = new SecureRandom();
		validch = new ArrayList<>();
		for (int i = 65; i < 91; i++) {
			validch.add((char) i);// A-Z
			validch.add((char) (i + 32));// a-z
		}
		for (int i = 48; i < 58; i++) {
			validch.add((char) i);
		}
		for (int i = 35; i < 39; i++) {
			validch.add((char) i);
		}
		validch.add((char) 64);
		Collections.shuffle(validch);
	}

	public char randChar() {
		return (char) this.validch.get(srand.nextInt(this.validch.size()));
	}

	public static void main(String[] args)
			throws NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException {

		PasswordGenerator pg = new PasswordGenerator();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 15; i++) { // Passwords are 15 characters long
			sb.append(pg.randChar());
		}
		System.out.println(sb.toString());
		sb.setLength(0);
	}
}