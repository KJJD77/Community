package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.Message;
import com.kjjd.community.community.entity.Page;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.service.MessageService;
import com.kjjd.community.community.service.UserService;
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

import java.util.*;

@Controller
public class MessageController {
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

}
