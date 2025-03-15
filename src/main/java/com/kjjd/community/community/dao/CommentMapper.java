package com.kjjd.community.community.dao;

import com.kjjd.community.community.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("select count(id) from comment where entity_type=#{entityType} and entity_id=#{entityId}")
    int selectCountByEntity(int entityType,int entityId);

    @Select("select id,user_id,entity_id,entity_type,target_id,content,status,create_time from comment where entity_id=#{entityId} and entity_type=#{entityType} order by create_time limit #{offset},#{limit}")
    List<Comment> selectCommentByEntity(int entityId,int entityType,int offset,int limit);

    @Insert("insert into comment (user_id, entity_type, entity_id, target_id, content, status, create_time) values(#{userId},#{entityType},#{entityId},#{targetId},#{content},#{status},#{createTime})")
    int insertComment(Comment comment);
}
