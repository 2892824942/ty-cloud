package com.ty.mid.framework.core.util;

import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.util.rc.RC;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author suyouliang
 * @createDate 2021/7/13
 */
@Slf4j
public abstract class EncryptUtils {

    public static String encryptRC4(String plainText, String key) {
        Validator.requireNonEmpty(plainText, "plainText 不能为空");
        Validator.requireNonEmpty(key, "加密秘钥不能为空");
        return new RC(key.getBytes(StandardCharsets.UTF_8)).encrypt(plainText);
    }

    public static String decryptRC4(String cipherText, String key) {
        Validator.requireNonEmpty(cipherText, "cipherText 不能为空");
        Validator.requireNonEmpty(key, "加密秘钥不能为空");
        return new RC(key.getBytes(StandardCharsets.UTF_8)).decrypt(cipherText);
    }

    public static String encryptAES(@NonNull String plainText, @NonNull String aesKey) {
        return AesUtils.encrypt(plainText, aesKey);
    }

    public static byte[] encryptAES(@NonNull byte[] content, @NonNull String aesKey) {
        return AesUtils.encrypt(content, aesKey);
    }

    public static String decryptAES(@NonNull String cipherText, @NonNull String aesKey) {
        return AesUtils.decrypt(cipherText, aesKey);
    }

    public static byte[] decryptAES(@NonNull byte[] cipherContent, @NonNull String aesKey) {
        return AesUtils.decrypt(cipherContent, aesKey);
    }

    /**
     * RSA 加密
     *
     * @param plainText
     * @param publicKey
     * @return
     */
    public static String encryptRSA(String plainText, String publicKey) {
        Validator.requireNonEmpty(plainText, "文本不能为空");
        Validator.requireNonEmpty(publicKey, "公钥不能为空");

        byte[] decoded = Base64.getDecoder().decode(publicKey);
        try {
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
            return outStr;
        } catch (Exception e) {
            log.info("加密失败", e);
            throw new FrameworkException("加密失败");
        }
    }

    /**
     * RSA 解密
     *
     * @param encryptedString
     * @param privateKey
     * @return
     */
    public static String decryptRSA(String encryptedString, String privateKey) {
        Validator.requireNonEmpty(encryptedString, "密文不能为空");
        Validator.requireNonEmpty(privateKey, "私钥不能为空");

        byte[] decode = Base64.getDecoder().decode(encryptedString.getBytes(StandardCharsets.UTF_8));

        byte[] decodeKey = Base64.getDecoder().decode(privateKey.getBytes(StandardCharsets.UTF_8));

        try {

            RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decodeKey));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            String outStr = new String(cipher.doFinal(decode));
            return outStr;
        } catch (Exception e) {
            log.info("解密失败", e);
            throw new FrameworkException("解密失败");
        }
    }

}
