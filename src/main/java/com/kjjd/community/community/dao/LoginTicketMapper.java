package com.kjjd.community.community.dao;

import com.kjjd.community.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Update(  {"update login_ticket set status = #{status} where ticket=#{ticket}"})
    public int Update_Status(String ticket,int status);

    @Select({"select id,user_id,ticket,status,expired from login_ticket where  ticket = #{loginTicket}"} )
    public LoginTicket selectByTicket(String loginTicket);

    @Insert({"insert into login_ticket (user_id,ticket,status,expired) ",
            " values(#{userId},#{ticket},#{status},#{expired})"})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int insertLoginTicket(LoginTicket loginTicket);
}
