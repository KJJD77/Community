package com.kjjd.community.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {
    @Autowired
    KafkaProducer kafkaProducer;
    @Test
    public void testKafka()
    {
        kafkaProducer.sendMessage("test1", "你好");
        kafkaProducer.sendMessage("test1", "在吗");

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(String topic,String context)
    {
        kafkaTemplate.send(topic,context);
    }

}
@Component
class KafkaConsumer{
    @KafkaListener(topics = {"test1"})
    public void handleMessage(ConsumerRecord record)
    {
        System.out.println(record.value());
    }

}
