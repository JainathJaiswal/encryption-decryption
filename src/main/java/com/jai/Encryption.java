package com.jai;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

	private SecretKeySpec secretKey;
	private Cipher cipher;

	public Encryption(String secret, int length, String algorithm)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		byte[] key = new byte[length];
		key = fixSecret(secret, length);
		this.secretKey = new SecretKeySpec(key, algorithm);
		this.cipher = Cipher.getInstance(algorithm);
	}

	private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
		if (s.length() < length) {
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) {
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8");
	}

	public void encryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Encrypting file: " + f.getPath());
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		this.writeToFile(f);
		System.out.println("Encrypted file: " + f.getPath());
	}

	public void writeToFile(File f) throws IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream in = new FileInputStream(f);
		byte[] input = new byte[(int) f.length()];
		in.read(input);

		FileOutputStream out = new FileOutputStream(f);
		byte[] output = this.cipher.doFinal(input);
		out.write(output);

		out.flush();
		out.close();
		in.close();
	}

	public static void encryptFiles(File[] files, Encryption ske) {

		for (File file : files) {
			if (file.isDirectory()) {
				encryptFiles(file.listFiles(), ske);
			} else {
				try {
					ske.encryptFile(file);
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
					System.err.println("Couldn't encrypt " + file.getName() + ": " + e.getMessage());
				}
			}
		}
	}

	public String encryptMessage(String encryptMessage)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		if (encryptMessage != null && !encryptMessage.isEmpty()) {
			byte[] getBytes = encryptMessage.getBytes(StandardCharsets.UTF_8);
			this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
			byte[] encryptBytes = this.cipher.doFinal(getBytes);
			return Base64.getEncoder().encodeToString(encryptBytes);
		} else {
			throw new RuntimeException("Input message cannot be null or empty!");
		}
	}

	public static void main1(String[] args) {
		File dir = new File("d://test");
		File[] filelist = dir.listFiles();

		Encryption ske;
		try {
			ske = new Encryption("!@#$MySecr3tPassw0rd", 16, "AES");
			encryptFiles(filelist, ske);
		} catch (UnsupportedEncodingException ex) {
			System.err.println("Couldn't create key: " + ex.getMessage());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void main(String[] args)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		Encryption ske;
		try {
			ske = new Encryption("B$Asq9k2FtKaQq5", 16, "AES");
			String enc = ske.encryptMessage("Jainath");
			System.out.println(enc);
		} catch (UnsupportedEncodingException ex) {
			System.err.println("Couldn't create key: " + ex.getMessage());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.err.println(e.getMessage());
		}
	}
}
