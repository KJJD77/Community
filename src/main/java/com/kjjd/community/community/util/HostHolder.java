package com.kjjd.community.community.util;

import com.kjjd.community.community.entity.User;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;

/**
 * 持有用户信息,用于代替session对象.
 */
@Component
public class HostHolder {
    private ThreadLocal<User> threadLocal=new ThreadLocal<>();

    public void setUser(User user)
    {
        threadLocal.set(user);
    }
    public User getUser()
    {
        return threadLocal.get();
    }
    public void clear() {
        threadLocal.remove();
    }
}
