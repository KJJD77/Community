package com.kjjd.community.community.dao.elasticsearch;

import com.kjjd.community.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
