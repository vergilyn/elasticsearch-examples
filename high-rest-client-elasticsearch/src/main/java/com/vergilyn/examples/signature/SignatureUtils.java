package com.vergilyn.examples.signature;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>签名计算规则：
 *   1. 参数值为空、"sign"、"sign_type"，不参与签名；
 *   2. 签名5min内有效；防止同一请求数据被多次重复请求
 *   3. 请求数据ascii排序；
 *   4. 必须字段: "sign、"timestamp"；"sign_type"为空默认MD5
 *
 * </p>
 * @author VergiLyn
 * @blog http://www.cnblogs.com/VergiLyn/
 * @date 2018/4/14
 * @see <a href="https://github.com/wxpay/WXPay-SDK-Java/blob/master/src/main/java/com/github/wxpay/sdk/WXPayUtil.java">（代码参考）微信支付sdk</a>
 * @see <a href="https://www.cnblogs.com/codelir/p/5327462.html">开放api接口签名验证</a>
 */
public class SignatureUtils {

    public enum SignType {
        MD5("MD5"), HMACSHA256("HmacSHA256");
        public final String algorithm;
        SignType(String algorithm) {
            this.algorithm = algorithm;
        }
    }

    public enum SignSpecialField {
        TIMESTAMP("timestamp"), SIGN("sign"), SIGN_TYPE("sign_type");
        public final String field;
        SignSpecialField(String field) {
            this.field = field;
        }
    }

    public static final String FAIL     = "FAIL";
    public static final String SUCCESS  = "SUCCESS";

    /** 签名有效时长: 5 min = 300000ms */
    public static final int SING_VALID_TIME = 300000;
    public static final String CHARSET_NAME = "UTF-8";

    /**
     * 获取签名类型, 默认{@link SignType#MD5}
     * @param data
     * @return
     */
    public static SignType signType(Map<String, String> data){
        SignType signType;
        if (data.containsKey(SignSpecialField.SIGN_TYPE.field)){
            String type = data.get(SignSpecialField.SIGN_TYPE.field);
            signType = SignType.HMACSHA256.algorithm.equalsIgnoreCase(type) ? SignType.HMACSHA256 : SignType.MD5;
        }else {
            signType = SignType.MD5;
        }
        return signType;
    }

    /**
     * 判断签名是否正确，必须包含"sign"字段，否则返回false。
     * 如果包含"sign_type"字段，则用对应类型验证签名, 否则用MD5
     *
     * @param data Map类型数据
     * @param appSecret API密钥
     * @return 签名是否正确
     * @throws Exception
     */
    public static boolean isSignatureValid(Map<String, String> data, String appSecret) throws Exception {
        return isSignatureValid(data, appSecret, signType(data));
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。
     *
     * @param data Map类型数据
     * @param appSecret API密钥
     * @param signType 签名方式
     * @return 签名是否正确
     * @throws Exception
     */
    public static boolean isSignatureValid(Map<String, String> data, String appSecret, SignType signType) throws Exception {
        if (!data.containsKey(SignSpecialField.SIGN.field) ) {
            return false;
        }

        String timestamp = data.get(SignSpecialField.TIMESTAMP.field);
        if (timestamp == null || Long.valueOf(timestamp) < System.currentTimeMillis() - SING_VALID_TIME){
            return false;
        }

        String sign = data.get(SignSpecialField.SIGN.field);
        return generateSignature(data, appSecret, signType).equals(sign);
    }

    /**
     * 生成MD5签名
     *
     * @param data 待签名数据
     * @param appSecret API密钥
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String appSecret) throws Exception {
        return generateSignature(data, appSecret, SignType.MD5);
    }

    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data 待签名数据
     * @param appSecret API密钥
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String appSecret, SignType signType) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray); // ascii排序
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(SignSpecialField.SIGN.field) || k.equals(SignSpecialField.SIGN_TYPE.field)) {
                continue;
            }
            if (data.get(k).trim().length() > 0){ // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
            }
        }
        sb.append("appSecret=").append(appSecret);
        if (SignType.MD5.equals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        }
        else if (SignType.HMACSHA256.equals(signType)) {
            return HMACSHA256(sb.toString(), appSecret);
        }
        else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        java.security.MessageDigest md = MessageDigest.getInstance(SignType.MD5.algorithm);
        byte[] array = md.digest(data.getBytes(CHARSET_NAME));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(SignType.HMACSHA256.algorithm);
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(CHARSET_NAME), SignType.HMACSHA256.algorithm);
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes(CHARSET_NAME));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
}