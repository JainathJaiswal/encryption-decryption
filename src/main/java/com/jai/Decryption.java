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

public class Decryption {
	private SecretKeySpec secretKey;
	private Cipher cipher;

	public Decryption(String secret, int length, String algorithm)
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

	public void decryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Decrypting file: " + f.getPath());
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		this.writeToFile(f);
		System.out.println("Decrypted file: " + f.getPath());
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

	public static void decryptFiles(File[] files, Decryption ske) {

		for (File file : files) {
			if (file.isDirectory()) {
				decryptFiles(file.listFiles(), ske);
			} else {
				try {
					ske.decryptFile(file);
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
					System.err.println("Couldn't decrypt " + file.getName() + ": " + e.getMessage());
				}
			}
		}
	}

	public String decryptMessage(String decryptMessage)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		if (decryptMessage != null && !decryptMessage.isEmpty()) {
			byte[] getBytes = Base64.getDecoder().decode(decryptMessage);
			this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
			byte[] decryptBytes = this.cipher.doFinal(getBytes);
			return new String(decryptBytes, StandardCharsets.UTF_8);
		} else {
			throw new RuntimeException("Input message cannot be null or empty!");
		}
	}

	public static void main1(String[] args) {
		File dir = new File("d://test");
		File[] filelist = dir.listFiles();
		Decryption ske;
		try {
			ske = new Decryption("!@#$MySecr3tPassw0rd", 16, "AES");
			decryptFiles(filelist, ske); 
		} catch (UnsupportedEncodingException ex) {
			System.err.println("Couldn't create key: " + ex.getMessage());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void main(String[] args)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		Decryption ske;
		try {
			ske = new Decryption("B$Asq9k2FtKaQq5", 16, "AES");
			String message="lCKcayLx+VgJjrT6vZY/HQ==";
			String dec = ske.decryptMessage(message);
			System.out.println(dec);

		} catch (UnsupportedEncodingException ex) {
			System.err.println("Couldn't create key: " + ex.getMessage());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.err.println(e.getMessage());
		}
	}
}
