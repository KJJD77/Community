package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.DiscussPost;
import com.kjjd.community.community.entity.Page;
import com.kjjd.community.community.service.ElasticsearchService;
import com.kjjd.community.community.service.LikeService;
import com.kjjd.community.community.service.UserService;
import com.kjjd.community.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    LikeService likeService;
    @Autowired
    UserService userService;
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException {
        List<DiscussPost> discussPostList = elasticsearchService.searchDiscussPost(keyword, page.getCurrent(), page.getLimit());
        List<Map<String ,Object>>discussPosts = new ArrayList<>();
        if(discussPostList!=null)
        for(DiscussPost post:discussPostList)
        {
            Map<String ,Object>map=new HashMap<>();
            map.put("post",post);
            map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
            map.put("user",userService.findUserById(post.getUserId()));

            discussPosts.add(map);
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(discussPostList == null ? 0 : discussPostList.size());
        return "/site/search";
    }

}
