package com.lin.util;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * 推送消息 工具类
 */
@Component
public class PushUtil {

    private Logger logger = LoggerFactory.getLogger(PushUtil.class);

    @Value("${push.url}")
    private String url;

    @Value("${push.token}")
    private String token;

    @Value("${push.topic}")
    private String topic;

    public boolean pushMsg(String title, String content) {
        if (StringUtils.hasText(title) && StringUtils.hasText(content)) {
            var params = new HashMap<>();
            params.put("token", token);
            params.put("topic", topic);
            params.put("title", title);
            params.put("content", content);
//            HttpUtil.post(url, JSONObject.toJSONString(params));

            HttpResponse response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .body(params)
                    .asEmpty();
            if (response.isSuccess()) {
                return true;
            }
            return false;
        }
        logger.info("=====content is empty====");
        return false;
    }

}
