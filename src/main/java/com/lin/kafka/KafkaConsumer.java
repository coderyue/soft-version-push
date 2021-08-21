//package com.lin.kafka;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
///**
// * kafka消费
// * @author lin
// * @date   2021/5/10
//**/
//@Component
//public class KafkaConsumer {
//
//    private Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
//
////    @KafkaListener(topics = "${kafka-topic.security-add}")
//    public void securityAdd(ConsumerRecord<String, String> record) {
//            Object value = record.value();
//        if (value != null) {
//            logger.info("received security-add msg>" + value);
//        }
//    }
//
////    @KafkaListener(topics = "${kafka-topic.origin-alarm-log}")
//    public void securityOriginAlarmLog(ConsumerRecord<String, String> record) {
//        Object value = record.value();
//        if (value != null) {
//            logger.info("received security origin-alarm-log msg>" + value);
//        }
//    }
//
//}
