package com.kjjd.community.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    Logger logger= LoggerFactory.getLogger(LoggerTests.class);
    @Test
    public void testLogger()
    {
        logger.debug("debug now");
        logger.info("info now");
        logger.warn("warn now");
        logger.error("error now");
    }
}
