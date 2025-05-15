package com.kjjd.community.community;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.kjjd.community.community.dao.DiscussPostMapper;
import com.kjjd.community.community.dao.elasticsearch.DiscussPostRepository;
import com.kjjd.community.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.Aggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTests {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    DiscussPostRepository discussPostRepository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SearchOperations searchOperations;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Test
    public void InsertTests()
    {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(222));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(223));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(224));
    }
    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }
    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人,使劲灌水.");
        discussPostRepository.save(post);
    }
//    @Test
//    public void testSearchByRepository()
//    {
//        SearchRequest searchRequest = SearchRequest.of(s -> s
//                .index("discusspost") // Specify the index name
//                .query(q -> q.multiMatch(m -> m)) // Add a query, for example, match_all
//                .sort(sort -> sort
//                        .field(f -> f
//                                .field("type")
//                                .order(SortOrder.Desc)
//                        )
//                        .field(f -> f
//                                .field("score")
//                                .order(SortOrder.Desc)
//                        )
//                        .field(f -> f
//                                .field("createTime")
//                                .order(SortOrder.Desc)
//                        )
//                )
//                .size(10) // Pagination size
//                .from(0)  // Pagination start position
//                .highlight(h -> h
//                        .fields("title", f -> f.preTags("<em>").postTags("</em>"))
//                        .fields("content", f -> f.preTags("<em>").postTags("</em>"))
//                )
//        );
//
//
//    }
    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("discusspost") // 替换为你的索引名称
                .query(q -> q.multiMatch(mm -> mm
                        .query("寒冬")
                        .fields("title", "content")
                ))
                .sort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                .sort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                .sort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                .from(10)
                .size(10)
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
        System.out.println("Total hits: " + search.hits().total().value());
        list.forEach(System.out::println);
    }
}
