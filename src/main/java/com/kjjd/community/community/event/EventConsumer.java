package com.kjjd.community.community.event;

import com.alibaba.fastjson.JSONObject;
import com.kjjd.community.community.dao.DiscussPostMapper;
import com.kjjd.community.community.entity.DiscussPost;
import com.kjjd.community.community.entity.Event;
import com.kjjd.community.community.entity.Message;
import com.kjjd.community.community.service.ElasticsearchService;
import com.kjjd.community.community.service.MessageService;
import com.kjjd.community.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    ElasticsearchService elasticsearchService;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record)
    {
        if(record == null || record.value() == null)
        {
            logger.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event==null)
        {
            logger.error("格式错误");
            return;
        }
        Message message=new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());

        Map<String, Object> content=new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        Map<String, Object> data = event.getData();
        if(!data.isEmpty()) {
            for(Map.Entry<String,Object> entry:data.entrySet())
            {
                content.put(entry.getKey(),entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
    //发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handDiscussPost(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("格式错误");
            return;
        }
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);
    }

}

