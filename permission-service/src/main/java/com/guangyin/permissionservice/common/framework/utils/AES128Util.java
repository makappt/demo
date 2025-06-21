package com.guangyin.permissionservice.common.framework.utils;

import cn.hutool.core.codec.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES128加密解密工具类
 * 提供基于AES-128算法的加密和解密功能，支持字节数组和字符串的加解密操作
 */
public class AES128Util {

    /**
     * 默认向量常量
     * 用于AES加密的初始化向量（IV），长度为16字节
     */
    public static final String IV = "akjsfakjshf@#!~&";

    /**
     * 秘钥,通过反转IV生成，用于AES加密和解密的密钥，长度为16字节（128位）
     */
    private static final String P_KEY = StringUtils.reverse(IV);

    private static final String AES_STR = "AES";
    /**
     * AES加密模式和填充方式常量
     * 使用CBC模式（Cipher Block Chaining）和PKCS5Padding填充方式
     * TODO 为什么使用PKCS5Padding这种填充方式？为什么不使用其他填充方式？
     */
    private static final String INSTANCE_STR = "AES/CBC/PKCS5Padding";

    /**
     * 加密字节数组
     * 使用AES-128算法对输入的字节数组进行加密
     *
     * @param content 需要加密的原内容
     * @return 加密后的字节数组；如果加密失败，返回null
     */
    public static byte[] aesEncrypt(byte[] content) {
        try {
            // 创建AES密钥规范对象，使用预定义的密钥P_KEY
            SecretKeySpec secretKeySpec = new SecretKeySpec(P_KEY.getBytes(), AES_STR);
            // 获取Cipher实例，指定加密模式为AES/CBC/PKCS5Padding
            Cipher cipher = Cipher.getInstance(INSTANCE_STR);
            // 创建初始化向量（IV）对象，用于CBC模式
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            // 初始化Cipher为加密模式，传入密钥和IV
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            // 执行加密操作，返回加密后的字节数组
            byte[] encrypted = cipher.doFinal(content);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密字节数组
     * 使用AES-128算法对输入的加密字节数组进行解密
     *
     * @param content 加密后的字节数组内容
     * @return 解密后的字节数组；如果解密失败，返回null
     */
    public static byte[] aesDecode(byte[] content) {
        try {
            // 创建AES密钥规范对象，使用预定义的密钥P_KEY
            SecretKeySpec secretKeySpec = new SecretKeySpec(P_KEY.getBytes(), AES_STR);
            // 创建初始化向量（IV）对象，使用UTF-8编码确保一致性
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            // 获取Cipher实例，指定解密模式为AES/CBC/PKCS5Padding
            Cipher cipher = Cipher.getInstance(INSTANCE_STR);
            // 初始化Cipher为解密模式，传入密钥和IV
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            // 执行解密操作，返回解密后的字节数组
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密字符串 128位
     *使用AES-128算法对输入的字符串进行加密，并将结果转换为Base64编码字符串
     *
     * @param content 需要加密的原内容
     * @return  加密后的Base64编码字符串；如果输入为空或加密失败，返回空字符串
     */
    public static String aesEncryptString(String content) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }
        try {
            // 创建AES密钥规范对象，使用预定义的密钥P_KEY
            SecretKeySpec secretKeySpec = new SecretKeySpec(P_KEY.getBytes(), AES_STR);
            // 获取Cipher实例，指定加密模式为AES/CBC/PKCS5Padding
            Cipher cipher = Cipher.getInstance(INSTANCE_STR);
            // 创建初始化向量（IV）对象
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            // 初始化Cipher为加密模式，传入密钥和IV
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            // 将字符串转换为UTF-8编码的字节数组并执行加密
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            // 将加密后的字节数组转换为Base64编码字符串返回
            return Base64.encode(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解密
     * 使用AES-128算法对输入的Base64编码字符串进行解密，并将结果转换为字符串
     *
     * @param content 加密后的Base64编码字符串内容
     * @return result  解密后的字符串；如果输入为空或解密失败，返回空字符串
     * @throws Exception
     */
    public static String aesDecodeString(String content) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }
        try {
            // 创建AES密钥规范对象，使用预定义的密钥P_KEY
            SecretKeySpec secretKeySpec = new SecretKeySpec(P_KEY.getBytes(), AES_STR);
            // 创建初始化向量（IV）对象，使用UTF-8编码确保一致性
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            // 获取Cipher实例，指定解密模式为AES/CBC/PKCS5Padding
            Cipher cipher = Cipher.getInstance(INSTANCE_STR);
            // 初始化Cipher为解密模式，传入密钥和IV
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            // 将Base64编码字符串解码为字节数组并执行解密
            byte[] result = cipher.doFinal(Base64.decode(content));
            // 将解密后的字节数组转换为UTF-8编码的字符串返回
            return new String(result, 0, result.length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

}
