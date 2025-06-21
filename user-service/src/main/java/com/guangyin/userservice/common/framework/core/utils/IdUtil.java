package com.guangyin.userservice.common.framework.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import com.guangyin.userservice.common.framework.core.exception.MicroServiceBusinessException;
import org.apache.commons.lang3.StringUtils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Objects;

/**
 * 雪花算法id生成器
 * 使用AES-128算法对生成的ID进行加密和解密，增强安全性。
 * 支持对多个加密ID的字符串进行解密。
 *
 * NOTE 雪花算法：其核心思想是将64位二进制数划分为多个部分，分别表示时间戳、数据中心ID、机器ID和序列号，从而保证ID的唯一性和有序性。
 *
 * ID结构（64位）：
 * 时间戳（41位）：表示从某个起始时间到当前时间的毫秒差，用于保证ID随时间递增。
 * 数据中心ID（5位）：表示数据中心的编号，支持最多32个数据中心。
 * 机器ID（5位）：表示机器的编号，支持每个数据中心最多32台机器。
 * 序列号（12位）：表示同一毫秒内的序列号，支持每毫秒生成4096个ID。
 */
public class IdUtil {

    /**
     * 工作id 也就是机器id
     */
    private static final long workerId;

    /**
     * 数据中心id
     */
    private static final long dataCenterId;

    /**
     * 序列号
     */
    private static long sequence;

    /**
     * 初始时间戳
     */
    private static final long startTimestamp = 1288834974657L;

    /**
     * 工作id长度为5位
     */
    private static final long workerIdBits = 5L;

    /**
     * 数据中心id长度为5位
     */
    private static final long dataCenterIdBits = 5L;

    /**
     * 工作id最大值
     */
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 数据中心id最大值
     */
    private static final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    /**
     * 序列号长度
     */
    private static final long sequenceBits = 12L;

    /**
     * 序列号最大值
     */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作id需要左移的位数，12位
     */
    private static final long workerIdShift = sequenceBits;

    /**
     * 数据id需要左移位数 12+5=17位
     */
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间戳需要左移位数 12+5+5=22位
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 上次时间戳，初始值为负数
     */
    private static long lastTimestamp = -1L;

    /**
     * 初始化工作ID和数据中心ID，确保在类加载时生成唯一值
     */
    static {
        // 通过机器网络接口信息生成机器编号，并与最大值取模，确保在有效范围内
        workerId = getMachineNum() & maxWorkerId;
        dataCenterId = getMachineNum() & maxDataCenterId;
        // 初始化序列号为0
        sequence = 0L;
    }

    /**
     * 获取机器编号 通过网络接口信息生成唯一的机器标识
     *
     * @return
     */
    private static long getMachineNum() {
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            // 获取所有网络接口信息
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            // 捕获网络接口获取异常并打印堆栈信息
            e1.printStackTrace();
        }
        // 遍历网络接口，将接口信息拼接到字符串中
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            sb.append(ni.toString());
        }
        // 计算字符串的哈希值作为机器编号
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }

    /**
     * 获取下一个毫秒时间戳
     * 如果当前时间戳小于或等于上次时间戳，则循环等待直到获取更大的时间戳
     *
     * @param lastTimestamp
     * @return
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取系统时间戳
     *
     * @return
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 生成唯一ID，处理时间戳回退问题，并通过位运算拼接ID。
     * 使用雪花算法生成64位唯一ID，处理时间戳回退和序列号溢出问题
     *
     * @return
     */
    public synchronized static Long get() {
        long timestamp = timeGen();
        // 获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 获取当前时间戳如果等于上次时间戳
        // 说明：还处在同一毫秒内，则在序列号加1；否则序列号赋值为0，从0开始。
        // 0 - 4095
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        //将上次时间戳值刷新
        lastTimestamp = timestamp;

        /**
         * 返回结果：
         * (timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
         * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
         */
        return ((timestamp - startTimestamp) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * 对ID进行加密,使用AES-128算法。
     *
     * @return
     */
    public static String encrypt(Long id) {
        if (Objects.nonNull(id)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.putLong(0, id);
            byte[] content = byteBuffer.array();
            byte[] encrypt = AES128Util.aesEncrypt(content);
            return Base64.encode(encrypt);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 对ID进行解密，使用AES-128算法。
     *
     * @param decryptId
     * @return
     */
    public static Long decrypt(String decryptId) {
        if (StringUtils.isNotBlank(decryptId)) {
            byte[] encrypt = Base64.decode(decryptId);
            byte[] content = AES128Util.aesDecode(encrypt);
            if (ArrayUtil.isNotEmpty(content)) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(content);
                return byteBuffer.getLong();
            }
            throw new MicroServiceBusinessException("AES128Util.aesDecode fail");
        }
        throw new MicroServiceBusinessException("the decryptId can not be empty");
    }
}
