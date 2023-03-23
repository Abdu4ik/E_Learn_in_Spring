package com.tafakkoor.e_learn.utils;

import com.tafakkoor.e_learn.repository.AuthUserRepository;
import com.tafakkoor.e_learn.repository.TokenRepository;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class Main {
    private final AuthUserRepository authUserRepository;
    private final TokenRepository tokenRepository;

    public Main(AuthUserRepository authUserRepository, TokenRepository tokenRepository) {
        this.authUserRepository = authUserRepository;
        this.tokenRepository = tokenRepository;
    }

    private static final String SECRET_KEY = "EWT$@WEFYG%H$ETGE@R!T#$HJYYT$QGRWHNJU%$TJRUYRHFRYFJRYUYRHD";
    private static final String SALT_VALUE = "my-salt-vvadvdvdvdvadvadvadvadvavadvadvalue";

    public static void main(String[] args) throws Exception {
        String encryptedCookieValue = "eyJzdGF0dXMiOiIxMjM0NTY3ODkiLCJ1c2VybmFtZSI6ImFkbWluIiwiY2hhbmdlIjoiZjI2ZmE5Mjg5NTUzZjUxZGJhNDc0MDRlZjRiOWRkYjkiLCJzZXJpYWwiOiJzYW1zdW5nIiwibmJmIjoxNjE2ODIwMzczLCJleHAiOjE2MTY4MjA5NzMsImlhdCI6MTYxNjgyMDM3MywianRpIjoiM2Q2NjQxZDAtNDNhZS00YzAxLWIyNjYtYjk4ZTA4MjI0MzZkIn0=";
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedCookieValue);

        byte[] keyBytes = (SECRET_KEY + SALT_VALUE).getBytes();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(keyBytes);
        byte[] encryptionKeyBytes = new byte[16];
        System.arraycopy(hashBytes, 0, encryptionKeyBytes, 0, 16);
        SecretKeySpec encryptionKey = new SecretKeySpec(encryptionKeyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        String decryptedValue = new String(decryptedBytes);
        System.out.println(decryptedValue);
    }

}
