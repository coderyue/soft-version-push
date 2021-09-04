package com.lin.util;

import org.springframework.util.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * http工具类
 * @author lin
 * @date   2021/8/21
**/
public class HttpUtil {

    /**
     * 发送GET请求
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * 发送GET请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(buildUrlWithParams(url, params)))
                .header("Content-Type", "application/x-www-form-urlencoded").GET().build();
        return send(request);
    }

    /**
     * 给访问路径拼接参数
     *
     * @param url
     * @param params
     * @return
     */
    private static String buildUrlWithParams(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        // 若已有参数则和之前的参数合并
        sb.append(url.indexOf('?') == -1 ? '?' : '&');

        // 循环拼接参数
        params.forEach((key, value) -> {
            if (StringUtils.hasLength(value)) {
                value = URLEncoder.encode(value, StandardCharsets.UTF_8);
                sb.append(key).append('=').append(value).append('&');
            }
        });

        // 去掉最后的&
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param body
     * @return
     */
    public static String post(String url, String body) {
        return post(url, body, null);
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param body
     * @param params
     * @return
     */
    public static String post(String url, String body, Map<String, String> params) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(buildUrlWithParams(url, params)))
                .header("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
        return send(request);
    }

    /**
     * 发送带本地证书的POST请求
     *
     * @param url
     * @param data
     * @param certFile
     * @param certPwd
     * @return
     */
    public static String postWithCertificate(String url, String data, File certFile, char[] certPwd) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(data, StandardCharsets.UTF_8)).build();

        try {
            // 实例化SSL上下文
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 实例化密钥管理工厂
            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            // 实例化密钥库
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // 加载证书文件和密码
            keyStore.load(new FileInputStream(certFile), certPwd);
            // 初始化密钥管理工厂
            keyManagerFactory.init(keyStore, certPwd);
            // 初始化SSL上下文
            sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
            // 构建HTTP客户端实例
            HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送http请求
     *
     * @param request
     * @return
     */
    private static String send(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();

        try {
             return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
