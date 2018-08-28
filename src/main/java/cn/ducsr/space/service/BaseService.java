package cn.ducsr.space.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class BaseService {

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
        String key = "CHINA NO.1";
        String result = getHash(key + id, "MD5");
        return result;
    }

    /**
     * 清除cookies
     *
     * @param cookies
     * @param response
     * @return
     */
    public static boolean removeCookies(Cookie[] cookies, HttpServletResponse response) {
        if (cookies == null) return false;
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i] == null) continue;
            cookies[i].setValue(null);
            cookies[i].setMaxAge(0);
            cookies[i].setPath("/");
            response.addCookie(cookies[i]);
        }
        return true;
    }

    /**
     * 根据Cookie名来查找Cookie
     *
     * @param name
     * @param cookies
     * @return
     */
    public static Cookie[] getCookiesByName(String[] name, Cookie[] cookies) {
        Cookie[] getCookies = new Cookie[name.length];
        int i = 0;
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (i < name.length) {
                for (int j = 0; j < name.length; j++) {
                    if (cookie.getName().equals(name[j])) {
                        getCookies[i] = cookie;
                        i++;
                    }
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

}
