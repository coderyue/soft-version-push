package com.lin.controller;

import com.alibaba.fastjson.JSONObject;
import com.lin.mapper.clickhouse.ClickhouseMapper;
import com.lin.mapper.mysql.MysqlMapper;
import com.lin.util.HttpUtil;
import com.lin.util.PushUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * test
 * @author lin
 * @date   2021/5/10
**/
@RestController
@RequestMapping("/test")
public class TestController {

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @Resource
    private MysqlMapper mysqlMapper;

    @Resource
    private ClickhouseMapper clickhouseMapper;

    @Value("${push.url}")
    private String url;

    @Value("${push.token}")
    private String token;

    @Value("${push.topic}")
    private String topic;

    @Autowired
    private PushUtil pushUtil;


    @RequestMapping("/datasource")
    public String testDatasource() {
        Integer mysql = mysqlMapper.test();
        Integer clickhouse = clickhouseMapper.test();
        logger.info("mysql:" + mysql + " clickhouse:" + clickhouse);
        return "success";
    }

    /**
     * 测试发送
     * @return
     */
    @RequestMapping("/send")
    public String testSendMessage() {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("topic", topic);
        params.put("title", "soft name" + Math.random());
        params.put("content", "content detail" + Math.random());
        String body = HttpUtil.post(url, JSONObject.toJSONString(params));
        JSONObject jsonObject = JSONObject.parseObject(body);
        logger.info("code:" + jsonObject.get("code"));
        logger.info("msg:" + jsonObject.get("msg"));
        logger.info("data:" + jsonObject.get("data"));
        logger.info("count", jsonObject.get("count"));
        return body;
    }

    /***
     * 发送消息
     * @param title
     * @param content
     * @return
     */
    @RequestMapping("/sendMessage")
    public boolean sendMessage(String title, String content) {
        if (StringUtils.hasText(title) && StringUtils.hasText(content)) {
//            var params = new HashMap<>();
//            params.put("token", token);
//            params.put("topic", topic);
//            params.put("title", title);
//            params.put("content", content);
//            HttpUtil.post(url, JSONObject.toJSONString(params));
            pushUtil.pushMsg(title, content);
            logger.info("push success, title:" + title + ", content" + content);
            return true;
        }
        logger.info("=====content is empty====");
        return false;
    }

}
