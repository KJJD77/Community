package com.kjjd.community.community.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.kjjd.community.community.dao.elasticsearch.DiscussPostRepository;
import com.kjjd.community.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchTemplate  elasticsearchTemplate;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    public void saveDiscussPost(DiscussPost discussPost)
    {
        discussPostRepository.save(discussPost);
    }
    public void deleteDiscussPost(int id)
    {
        discussPostRepository.deleteById(id);
    }
    public List<DiscussPost> searchDiscussPost(String keyword,int current,int limit) throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("discusspost") // 替换为你的索引名称
                .query(q -> q.multiMatch(mm -> mm
                        .query(keyword)
                        .fields("title", "content")
                ))
                .sort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                .sort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                .sort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                .from((current-1)*limit)
                .size(limit)
                .highlight(h -> h
                        .preTags("<em>").postTags("</em>")  // 统一设置高亮标签
                        .fields("title", f -> f)  // 高亮 title 字段
                        .fields("content", f -> f)  // 高亮 content 字段
                )
                .build();
        SearchResponse<DiscussPost> search = elasticsearchClient.search(searchRequest, DiscussPost.class);
        List<DiscussPost>list=new ArrayList<>();
        List<Hit<DiscussPost>> hits = search.hits().hits();
        for(Hit<DiscussPost> hit:hits)
        {
            DiscussPost discussPost = hit.source();
            Map<String, List<String>> highlight = hit.highlight();
            if(highlight!=null)
            {
                List<String> titleHighlights = highlight.get("title");
                if (titleHighlights != null && !titleHighlights.isEmpty()) {
                    discussPost.setTitle(titleHighlights.get(0));
                }
                List<String> contentHighlights = highlight.get("content");
                if (contentHighlights != null && !contentHighlights.isEmpty()) {
                    discussPost.setContent(contentHighlights.get(0));
                }
            }
            list.add(discussPost);
        }
        return list;
    }



}
