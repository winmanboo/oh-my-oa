package com.winmanboo.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class DESUtils {
  private static final String secretKey = "12345678";
  private static final String encryptionAlgorithm = "DES";
  private static final String transformation = "DES";

  public static String encrypt(String content) throws Exception {
    // Cipher 密码，获取加密对象
    // transformation 表示使用什么类型来加密
    Cipher cipher = Cipher.getInstance(transformation);

    // 指定密钥规则
    // 第一个参数表示：密钥，key 的字节数组
    // 第二个参数表示：算法
    SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), encryptionAlgorithm);

    // 对加密进行初始化
    // 第一个参数：表示模式，有加密模式和解密模式
    // 第二个参数：表示密钥规则
    cipher.init(Cipher.ENCRYPT_MODE, spec);

    // 进行加密
    byte[] bytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    // 直接转字符串输出会乱码，因为字节出现了负数，所以用 Base64 进行编码返回
    return Base64.encodeBase64String(bytes);
  }

  public static String decrypt(String encryptData) throws Exception {
    Cipher cipher = Cipher.getInstance(transformation);
    SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), encryptionAlgorithm);
    cipher.init(Cipher.DECRYPT_MODE, spec);
    return new String(cipher.doFinal(Base64.decodeBase64(encryptData)));
  }

  public static void main(String[] args) throws Exception {
    System.out.println(DESUtils.encrypt("hello world")); // KNugLrX23UddguNoHIO7dw==
    System.out.println(DESUtils.decrypt("KNugLrX23UddguNoHIO7dw=="));
  }
}
