package com.kjjd.community.community;

import com.kjjd.community.community.dao.Alphadao;
import com.kjjd.community.community.dao.AlphadaoHibernatelmpl;
import com.kjjd.community.community.service.Alphaservice;
import org.apache.catalina.core.StandardContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	@Test
	public void applicationContexttest() {
		System.out.println(applicationContext);

		Alphadao alphadaohi = applicationContext.getBean("Hibernatelmpl",Alphadao.class);
		alphadaohi.select();

		Alphadao alphadaoh2 = applicationContext.getBean(Alphadao.class);
		alphadaoh2.select();
	}
	@Autowired
	@Qualifier("Hibernatel")
	private Alphadao alphadao;
	@Autowired
	private Alphaservice alphaservice;
	@Autowired
	private SimpleDateFormat simpleDateFormat;
	@Test
	public void Servicetest() {
		Alphaservice alphaservice = applicationContext.getBean(Alphaservice.class);
		alphaservice=applicationContext.getBean(Alphaservice.class);
	}
	@Test
	public void DItest() {
		System.out.println(alphadao);
		System.out.println(alphaservice);
		System.out.println(simpleDateFormat);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
