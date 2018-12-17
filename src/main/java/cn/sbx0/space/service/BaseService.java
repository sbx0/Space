package cn.sbx0.space.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseService<T, ID> {
    private static String DOMAIN; // 域名
    private static String KEY; // KEY
    public static List<String> COOKIE_NAMES = Arrays.asList("ID", "KEY", "NAME");
    public static final Integer PAGESIZE = 10;

    public abstract PagingAndSortingRepository<T, ID> getDao();

    /**
     * 提取request中的json
     *
     * @param request
     * @return
     */
    public static String getJson(HttpServletRequest request) {
        String str = "NULL";
        try {
            ServletInputStream is = request.getInputStream();
            int nRead = 1;
            int nTotalRead = 0;
            byte[] bytes = new byte[10240];
            while (nRead > 0) {
                nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
                if (nRead > 0) nTotalRead = nTotalRead + nRead;
            }
            str = new String(bytes, 0, nTotalRead, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 只显示ip的头和尾
     */
    public static String hideFullIp(String ip) {
        String[] ipNumber = ip.split("\\.");
        StringBuilder ipBuilder = new StringBuilder(ipNumber[0] + ".");
        for (int i = 1; i < ipNumber.length - 1; i++) {
            ipBuilder.append("*.");
        }
        ip = ipBuilder.toString();
        ip += ipNumber[ipNumber.length - 1];
        return ip;
    }

    /**
     * 获得某天最大时间 2017-10-15 23:59:59
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获得某天最小时间 2017-10-15 00:00:00
     */
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 去html标签
     */
    public static String killHTML(String content) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?</script>"; // script
        String regEx_style = "<style[^>]*?>[\\s\\S]*?</style>"; // style
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
     */
    public static String getKey(int id) {
        return getHash(id + KEY, "MD5");
    }

    /**
     * 清空cookies
     */
    public static void removeCookies(HttpServletResponse response) {
        for (String COOKIE_NAME : COOKIE_NAMES) {
            Cookie cookie = new Cookie(COOKIE_NAME, null);
            cookie.setDomain(DOMAIN);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * 在一群Cookie中根据名称查找想要的
     */
    static Map<String, Cookie> getCookiesByName(List<String> name, Cookie[] cookies) {
        // 一群Cookie为空，放弃寻找
        if (cookies == null) return null;
        // 名字有几个就找几个
        Map<String, Cookie> getCookies = new HashMap<>();
        for (Cookie cookie : cookies) { // 遍历一群Cookie
            for (int j = 0; j < name.size(); j++) { // 匹配名称
                if (cookie.getName().equals(name.get(j))) { // 找到一个
                    getCookies.put(name.get(j), cookie); // 存下来
                    if (getCookies.size() == name.size()) break; // 全找到了
                }
            }
        }
        return getCookies;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     */
    public static String getIpAddress(HttpServletRequest request) {
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
            for (String strIp : ips) {
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    // 都有的方法


    // 公用方法

    /**
     * 检测字符串是否为空
     */
    public static boolean checkNullStr(String str) {
        if (str == null) return true;
        if (str.length() == 0) return true;
        if (str.trim().equals("")) return true;
        if (str.trim().length() == 0) return true;
        // 纯html标签
        if (killHTML(str).trim().length() == 0) return false;
        return false;
    }

    /**
     * 密码哈希
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

    /**
     * 设置域名 配置cookie
     */
    @Value("${sbx0.DOMAIN}")
    public void setDomain(String domain) {
        DOMAIN = domain;
    }

    /**
     * 设置KEY 用于加密
     */
    @Value("${sbx0.KEY}")
    public void setKEY(String key) {
        KEY = key;
    }

    /**
     * 保存
     *
     * @param entity 实体类
     * @return 操作是否成功
     */
    @Transactional
    public boolean save(T entity) {
        try {
            getDao().save(entity);
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 批量保存
     *
     * @param entities 实体类列表
     * @return 操作是否成功
     */
    @Transactional
    public boolean saveAll(Iterable<T> entities) {
        try {
            getDao().saveAll(entities);
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public T findById(ID id) {
        try {
            return getDao().findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据ID查询存不存在
     *
     * @param id
     * @return 操作是否成功
     */
    public boolean existsById(ID id) {
        return getDao().existsById(id);
    }

    /**
     * 查询全部
     *
     * @return
     */
    public Iterable<T> findAll() {
        try {
            return getDao().findAll();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据ID列表查询全部
     *
     * @param ids
     * @return
     */
    public Iterable<T> findAllById(Iterable<ID> ids) {
        try {
            return getDao().findAllById(ids);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计数
     *
     * @return
     */
    public long count() {
        return getDao().count();
    }

    /**
     * 根据ID删除实体类
     *
     * @param id
     * @return 操作是否成功
     */
    @Transactional
    public boolean deleteById(ID id) {
        try {
            getDao().deleteById(id);
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 删除指定的实体
     *
     * @param entity
     * @return 操作是否成功
     */
    @Transactional
    public boolean delete(T entity) {
        try {
            getDao().delete(entity);
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 根据实体类删除
     *
     * @param entities
     * @return 操作是否成功
     */
    @Transactional
    public boolean deleteAll(Iterable<? extends T> entities) {
        try {
            getDao().deleteAll(entities);
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 删除所有
     *
     * @return 操作是否成功
     */
    @Transactional
    public boolean deleteAll() {
        try {
            getDao().deleteAll();
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 分页查询全部
     *
     * @param pageable
     * @return
     */
    public Page<T> findAll(Pageable pageable) {
        return getDao().findAll(pageable);
    }

    /**
     * 排序查询全部
     *
     * @param sort
     * @return
     */
    public Iterable<T> findAll(Sort sort) {
        return getDao().findAll(sort);
    }

    /**
     * 拼接Pageable
     *
     * @param page 页码
     * @param size 条数
     * @param sort 排序
     * @return
     */
    public static Pageable buildPageable(Integer page, Integer size, Sort sort) {
        // 页数控制
        if (page == null) page = 0;
        else if ((page - 1) >= 0) page = page - 1;
        else page = 0;
        // 条数控制
        if (size == null) size = PAGESIZE;
        if (size > 999) size = 999;
        if (size < 1) size = PAGESIZE;
        // 分页配置
        return PageRequest.of(page, size, sort);
    }

    /**
     * 构建Sort
     *
     * @param attribute 属性
     * @param direction 排序
     * @return
     */
    public Sort buildSort(String attribute, String direction) {
        switch (direction) {
            case "ASC": // 升序
                return new Sort(Sort.Direction.ASC, attribute);
            case "DESC": // 降序
                return new Sort(Sort.Direction.DESC, attribute);
            default: // 默认降序
                return new Sort(Sort.Direction.DESC, attribute);
        }
    }

}
