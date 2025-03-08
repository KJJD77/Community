package com.kjjd.community.community.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
//@Scope("prototype")
@Service
public class Alphaservice {
    public Alphaservice()
    {
        System.out.println("实例化");
    }
    @PostConstruct
	public void init() {
        System.out.println("初始化");
    }
    @PreDestroy
    public void destroy() {
        System.out.println("销毁");
    }
}
