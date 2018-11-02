package cn.sbx0.space.service;

import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseService {
    private static String DOMAIN; // 域名
    private static String KEY; // KEY
    public static List<String> COOKIE_NAMES = Arrays.asList("ID", "KEY", "NAME");

    // 获得某天最大时间 2017-10-15 23:59:59
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());;
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    // 获得某天最小时间 2017-10-15 00:00:00
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 去html标签
     *
     * @param content
     * @return
     */
    public static String killHTML(String content) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // script
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // style
        String regEx_html = "<[^>]+>"; // HTML tag

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(content);
        content = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(content);
        content = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(content);
        content = m_html.replaceAll(""); // 过滤html标签

        return content;
    }

    /**
     * cookie 创建
     *
     * @param name
     * @param value
     * @param day
     * @return
     */
    public static Cookie createCookie(String name, String value, int day) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(day * 24 * 60 * 60);
        return cookie;
    }


    /**
     * cookie 验证加密
     *
     * @param id
     * @return
     */
    public static String getKey(int id) {
        String result = getHash(id + KEY, "MD5");
        return result;
    }

    /**
     * 清空cookies
     *
     * @param response
     * @return
     */
    public static void removeCookies(HttpServletResponse response) {
        for (int i = 0; i < COOKIE_NAMES.size(); i++) {
            Cookie cookie = new Cookie(COOKIE_NAMES.get(i), null);
            cookie.setDomain(DOMAIN);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * 在一群Cookie中根据名称查找想要的
     *
     * @param name    Cookie名数组
     * @param cookies 一群Cookie
     * @return
     */
    public static Map<String, Cookie> getCookiesByName(List<String> name, Cookie[] cookies) {
        // 一群Cookie为空，放弃寻找
        if (cookies == null) return null;
        // 名字有几个就找几个
        Map<String, Cookie> getCookies = new HashMap<>();
        for (int i = 0; i < cookies.length; i++) { // 遍历一群Cookie
            for (int j = 0; j < name.size(); j++) { // 匹配名称
                if (cookies[i].getName().equals(name.get(j))) { // 找到一个
                    getCookies.put(name.get(j), cookies[i]); // 存下来
                    if (getCookies.size() == name.size()) break; // 全找到了
                }
            }
        }
        return getCookies;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     */
    public final static String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    // 公用方法

    /**
     * 检测字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean checkNullStr(String str) {
        if (str == null) return true;
        if (str.length() == 0) return true;
        if (str.trim() == "") return true;
        if (str.trim().length() == 0) return true;
        // 纯html标签
        if (killHTML(str).trim().length() == 0) return false;
        return false;
    }

    /**
     * 密码哈希
     *
     * @param source
     * @param hashType
     * @return
     */
    public static String getHash(String source, String hashType) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(hashType);
            md5.update(source.getBytes());
            for (byte b : md5.digest()) {
                sb.append(String.format("%02X", b)); // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Value("${sbx0.DOMAIN}")
    public void setDomain(String domain) {
        DOMAIN = domain;
    }

    @Value("${sbx0.KEY}")
    public void setKEY(String key) {
        KEY = key;
    }
}
