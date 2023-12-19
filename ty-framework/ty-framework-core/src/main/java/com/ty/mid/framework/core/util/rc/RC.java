package com.ty.mid.framework.core.util.rc;

import com.ty.mid.framework.common.util.Validator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RC {
    private final byte[] S = new byte[256];
    private final byte[] T = new byte[256];
    private final int keylen;

    public RC(final byte[] key) {
        if (key.length < 1 || key.length > 256) {
            throw new IllegalArgumentException(
                    "key must be between 1 and 256 bytes");
        } else {
            keylen = key.length;
            for (int i = 0; i < 256; i++) {
                S[i] = (byte) i;
                T[i] = key[i % keylen];
            }
            int j = 0;
            byte tmp;
            for (int i = 0; i < 256; i++) {
                j = (j + S[i] + T[i]) & 0xFF;
                tmp = S[j];
                S[j] = S[i];
                S[i] = tmp;
            }
        }
    }

    public String encrypt(String plainText) {
        Validator.requireNonEmpty(plainText, "明文不能为空");
        byte[] encrypt = this.encrypt(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypt);
    }

    public byte[] encrypt(final byte[] plainText) {
        final byte[] ciphertext = new byte[plainText.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        for (int counter = 0; counter < plainText.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) & 0xFF;
            k = S[t];
            ciphertext[counter] = (byte) (plainText[counter] ^ k);
        }
        return ciphertext;
    }

    public String decrypt(final String cipherText) {
        byte[] bytes = Base64.getDecoder().decode(cipherText);
        byte[] plainBytes = this.decrypt(bytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    public byte[] decrypt(final byte[] cipherText) {
        return encrypt(cipherText);
    }
}