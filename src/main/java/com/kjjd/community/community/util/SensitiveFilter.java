package com.kjjd.community.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    // 替换符
    private static final String REPLACEMENT = "***";
    private TrieNode root=new TrieNode();
    @PostConstruct
    public void init() {
        try(
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ){
            String s;
            while((s=bufferedReader.readLine())!=null)
            {
                this.addKeyword(s);
            }
        }
        catch (Exception e)
        {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }
    //添加敏感字
    private void addKeyword(String s)
    {
        TrieNode temp=root;
        int len=s.length();
        for(int i=0;i<len;i++)
        {
            Character c=s.charAt(i);
            TrieNode subNode;
            if(temp.getSubNode(c)==null)
            {
                subNode=new TrieNode();
                temp.addSubNode(c,subNode);
            }
            else
                subNode=temp.getSubNode(c);
            temp=subNode;
            if(i==len-1)
            {
                temp.setKeywordEnd(true);
            }
        }
    }
    //过滤
    public String filter(String s)
    {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        TrieNode tempNode=root;
        int l=0;
        int r=0;
        int len=s.length();
        StringBuilder sb=new StringBuilder();
        while(r<len) {
            Character c = s.charAt(r);
            if(isSymbol(c))
            {
                if(tempNode==root)
                {
                    sb.append(c);
                    l++;
                    r++;
                }
                else {
                    r++;
                }
                continue;
            }
            tempNode= tempNode.getSubNode(c);
            if (tempNode == null) {
                sb.append(s.charAt(l));
                r=++l;
                tempNode=root;
            }
            else if(tempNode.isKeywordEnd)
            {
                sb.append(REPLACEMENT);
                l=++r;
                tempNode=root;
            }
            else
                r++;
        }
        sb.append(s.substring(l));
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //字典树
    private class TrieNode{

        private boolean isKeywordEnd =false;
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        public void addSubNode(Character c,TrieNode node)
        {
            subNodes.put(c,node);
        }

        public TrieNode getSubNode(Character c)
        {
            return subNodes.get(c);
        }

    }

}
