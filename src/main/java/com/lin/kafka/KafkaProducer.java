//package com.lin.kafka;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
///**
// * kafka生产
// * @author lin
// * @date   2021/5/10
// **/
//@Component
//public class KafkaProducer {
//
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    public void send(String topic, String msg) {
//        kafkaTemplate.send(topic, msg);
//    }
//}
