package com.huhaoyu.thu.common;

import org.apache.commons.lang3.CharEncoding;
import org.apache.shiro.codec.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午5:01.
 */

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static String encrypt(String content, String secret, String algorithm, String mode, String iv) {
        try {
            byte[] byteOfContent = content.getBytes(CharEncoding.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
            IvParameterSpec ivParameter = new IvParameterSpec(iv.getBytes());
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameter);
            byte[] raw = cipher.doFinal(byteOfContent);
            return Hex.encodeToString(raw);

        } catch (NoSuchPaddingException | UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            logger.error("cannot encrypt content " + content + " with algorithm " + algorithm, e);
        }
        return null;
    }

    public static String decrypt(String encrypted, String secret, String algorithm, String mode, String iv) {
        try {
            byte[] bytes = Hex.decode(encrypted);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
            IvParameterSpec ivParameter = new IvParameterSpec(iv.getBytes());
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameter);
            byte[] result = cipher.doFinal(bytes);
            return new String(result, CharEncoding.UTF_8);

        } catch (NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException
                | InvalidKeyException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
            logger.error("cannot decrypt content " + encrypted + "with algorithm " + algorithm);
        }
        return null;
    }


}
