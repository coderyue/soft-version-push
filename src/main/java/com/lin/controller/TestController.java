package com.lin.controller;

import com.lin.mapper.clickhouse.ClickhouseMapper;
import com.lin.mapper.mysql.MysqlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @Value("${kafka-topic.security-update}")
    private String topic;

    @RequestMapping("/datasource")
    public String testDatasource() {
        Integer mysql = mysqlMapper.test();
        Integer clickhouse = clickhouseMapper.test();
        logger.info("mysql:" + mysql + " clickhouse:" + clickhouse);
        return "success";
    }


}
