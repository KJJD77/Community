package com.kjjd.community.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.kjjd.community.community.entity.Message;
import com.kjjd.community.community.entity.Page;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.service.MessageService;
import com.kjjd.community.community.service.UserService;
import com.kjjd.community.community.util.CommunityConstant;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.HostHolder;
import org.apache.catalina.Host;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model,Page page){
        User user =hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>>conversations=new ArrayList<>();
        if(conversationList!=null)
            for(Message message:conversationList)
            {
                Map<String,Object>map=new HashMap<>();
                map.put("conversation",message);
                User target = userService.findUserById((user.getId() != message.getFromId())?message.getFromId():message.getToId());
                map.put("target",target);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                conversations.add(map);
            }
        model.addAttribute("conversations",conversations);

        int letterUnreadCount=messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "/site/letter";
    }

    private List<Integer>getLetterIds(List<Message> messageList)
    {
        List<Integer>ans=new ArrayList<>();
        if(messageList!=null)
            for(Message message:messageList)
            {
                if(hostHolder.getUser().getId()==message.getToId() &&  message.getStatus()==0)
                    ans.add(message.getId());
            }
        return ans;
    }



    @RequestMapping(path="/letter/detail/{conversationId}",method =RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model)
    {
        page.setRows(messageService.findLetterCount(conversationId));
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);

        User user=hostHolder.getUser();
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters=new ArrayList<>();
        for(Message message:letterList)
        {
            Map<String,Object>map=new HashMap<>();
            map.put("letter",message);
            map.put("fromUser", userService.findUserById(message.getFromId()));
            letters.add(map);
        }
        List<Integer>ids=getLetterIds(letterList);
        if(!ids.isEmpty())
            messageService.readMessage(ids);
        model.addAttribute("letters",letters);
        model.addAttribute("target", getLetterTarget(conversationId));

        return "/site/letter-detail";
    }


    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    @RequestMapping(path="/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content)
    {
        User user=userService.selectByName(toName);
        if(user==null)
        {
            return CommunityUtil.getJSONString(1, "目标用户不存在!");
        }

        Message message=new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        if(hostHolder.getUser().getId()<user.getId())
            message.setConversationId(hostHolder.getUser().getId()+"_"+user.getId());
        else
            message.setConversationId(user.getId()+"_"+hostHolder.getUser().getId());
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(user.getId());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        // 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", unread);
        }
        model.addAttribute("commentNotice", messageVO);

        // 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unread);
        }
        model.addAttribute("likeNotice", messageVO);

        // 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if (message != null) {
            messageVO.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unread);
        }
        model.addAttribute("followNotice", messageVO);

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }

}
