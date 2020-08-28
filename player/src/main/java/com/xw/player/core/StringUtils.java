package com.xw.player.core;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理
 * @author Ryan
 */
public class StringUtils {

    private StringUtils() { /* cannot be instantiated */
    }

    /**
     * Check a string is empty or not.
     * @param str
     * @return boolean
     */
    public static boolean isStringEmpty(String str) {
        return str == null || str.length() == 0 || str.trim().length() == 0;
    }

    /**
     * <p>
     * Checks if a String is whitespace, empty ("") or null.
     * </p>
     * 
     * <pre>
     * StringUtils.isBlank(null) = true
     * StringUtils.isBlank("") = true
     * StringUtils.isBlank(" ") = true
     * StringUtils.isBlank("bob") = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsNull(String str) {

        return isBlank(str) || str.equalsIgnoreCase("null");

    }

    /**
     * <p>
     * Checks if the String contains only unicode digits. A decimal point is not
     * a unicode digit and returns false.
     * </p>
     * <p>
     * <code>null</code> will return <code>false</code>. An empty String
     * (length()=0) will return <code>true</code>.
     * </p>
     * 
     * <pre>
     * StringUtils.isNumeric(null) = false
     * StringUtils.isNumeric(&quot;&quot;) = true
     * StringUtils.isNumeric(&quot; &quot;) = false
     * StringUtils.isNumeric(&quot;123&quot;) = true
     * StringUtils.isNumeric(&quot;12 3&quot;) = false
     * StringUtils.isNumeric(&quot;ab2c&quot;) = false
     * StringUtils.isNumeric(&quot;12-3&quot;) = false
     * StringUtils.isNumeric(&quot;12.3&quot;) = false
     * </pre>
     *
     * @param str
     *            the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (equalsNull(str)) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param plainText
     *            被编码的字串
     * @return 空串，如果不能进行MD5编码
     */
    @NonNull
    public static String md5Helper2(String plainText) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        md.update(plainText.getBytes());
        byte b[] = md.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();// 32位的加密
    }

    /**
     * @param plainText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String md5Helper(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();// 32位的加密
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getSubStr(String str, int subNu, String replace) {

        int strLength = str.length();
        if (strLength >= subNu) {
            str = str.substring((strLength - subNu), strLength);
        } else {
            for (int i = strLength; i < subNu; i++) {
                str += replace;
            }
        }
        return str;
    }

    public static String getUUIDString(String tBrand, String tSeries,
            String tUnique, int subNu, String replace) {
        return StringUtils.md5Helper(getSubStr(tBrand, subNu, replace)
                + getSubStr(tSeries, subNu, replace)
                + getSubStr(tUnique, subNu, replace));
    }

    public static String encodeStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        if(equalsNull(strNum)){
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher(strNum);
        return matcher.matches();
    }

    /**
     * 判断整数（int）
     */
    public static boolean isInteger(String str) {
        if (equalsNull(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断浮点数（double 和 float）
     */
    public static boolean isDouble(String str) {
        if (equalsNull(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 字符串变大写
     * @param s
     * @return
     */
    public static String stringChangeCapital(String s) {
        if (equalsNull(s)) {
            return "";
        }
        char[] c = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            if (c[i] >= 'a' && c[i] <= 'z') {
                c[i] = Character.toUpperCase(c[i]);
            } else if (c[i] >= 'A' && c[i] <= 'Z') {
                c[i] = Character.toLowerCase(c[i]);
            }
        }
        return String.valueOf(c);
    }

    /**
     * 逆序每隔3位添加一个逗号
     * @param str
     *            :"31232"
     * @return
     *         :"31,232"
     */
    public static String addComma3(String str) {
        if (equalsNull(str)) {
            return "";
        }
        str = new StringBuilder(str).reverse().toString(); // 先将字符串颠倒顺序
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * 3 + 3 > str.length()) {
                str2 += str.substring(i * 3, str.length());
                break;
            }
            str2 += str.substring(i * 3, i * 3 + 3) + ",";
        }
        if (str2.endsWith(",")) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        // 最后再将顺序反转过来
        return new StringBuilder(str2).reverse().toString();
    }

    public static String handlerStr(String str, int length) {
        if (null != str && !"".equals(str)) {
            if (str.length() > 15) {
                String s = str.substring(0, 15) + "...";
                return s;
            }
            return str;
        }
        return "";
    }

    /**
     *判断字符串是否为颜色类型
     * @param colorString
     * @return
     */
    public static boolean isColorString(String colorString) {
        if (equalsNull(colorString)) {
            return false;
        }
        String regEx = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}|[0-9a-fA-F]{8})$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(colorString);
        return m.find();
    }

    /**
     * null字符串转为空字符串，防止抛异常
     * @param str
     * @return
     */
    public static String nullToEmptyStr(String str) {
        return StringUtils.equalsNull(str) ? "" : str;
    }

    /**
     * 删除url中的某个参数
     * @param url 请求链接
     * @param parameter 参数
     * @return
     */
    public static String urlDeleteParameter(String url, String parameter) {

        if (equalsNull(url) || equalsNull(parameter)) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        int startIndex = url.indexOf(parameter + "=");
        if (startIndex > 0) {
            sb.append(url.substring(0, startIndex));
            int endIndex = url.indexOf("&", startIndex);
            if (endIndex > 0) {
                sb.append(url.substring(endIndex + 1));
            }
            return sb.toString();
        }
        return url;
    }

    /**
     * 按指定的字节数截取字符串（一个中文字符占3个字节，一个英文字符或数字占1个字节）
     * @param sourceString 源字符串
     * @param cutBytes 要截取的字节数
     * @return
     */
    public static String cutString(String sourceString, int cutBytes) {
        if(equalsNull(sourceString)) {
            return "";
        }

        int lastIndex = 0;
        boolean stopFlag = false;

        int totalBytes = 0;
        for(int i = 0; i < sourceString.length(); i++) {
            String s = Integer.toBinaryString(sourceString.charAt(i));
            if(s.length() > 8) {
                totalBytes += 3;
            } else {
                totalBytes += 1;
            }

            if(totalBytes == cutBytes) {
                lastIndex = i;
                stopFlag = true;
                break;
            } else if(totalBytes > cutBytes) {
                lastIndex = i - 1;
                stopFlag = true;
                break;
            }
        }

        if(!stopFlag) {
            return sourceString;
        } else {
            return sourceString.substring(0, lastIndex + 1);
        }
    }

    /**
     * 将接口请求参数转换成map，例如：npid=&cpid=&bpid=2e37b94转换成{"npid":"", "cpid":"", "bpid":"2e37b94"}
     * @param s
     * @return
     */
    public static Map<String, String> trans2Map(String s){
        Map<String, String> map = new HashMap<>();
        if (StringUtils.equalsNull(s)){
            return map;
        }
        String[] params = s.split("&");
        String[] item;
        for (int i = 0; i < params.length; i++) {
            if (StringUtils.equalsNull(params[i])){
                continue;
            }
            item = params[i].split("=");
            if (item == null || item.length <= 2){
                continue;
            }
            map.put(item[0], item[1]);
        }
        return map;
    }
}
