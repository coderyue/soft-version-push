package com.lin.schedule;

import com.lin.service.SoftUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 推送任务
 */
@RestController
@RequestMapping("/push")
public class PushSchedule {

    private Logger logger = LoggerFactory.getLogger(PushSchedule.class);

    @Autowired
    private SoftUpdateService softUpdateService;

    /**
     * 推送任务
     */
//    @Scheduled(cron = "${push.cron}")
    @Scheduled(fixedDelay = 300000)
    @RequestMapping("/test")
    public void push() throws IOException {
        logger.info("=========check version start=========");

        softUpdateService.myPush();

        logger.info("=========check version stop=========");
    }

}
