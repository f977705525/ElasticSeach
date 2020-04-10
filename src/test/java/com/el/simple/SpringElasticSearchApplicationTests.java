package com.el.simple;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSON;

@SpringBootTest
class SpringElasticSearchApplicationTests {

	@Autowired
	private RestHighLevelClient client;
	
	@Test
	void contextLoads() throws IOException {
		CreateIndexRequest request = new CreateIndexRequest("fl_index");
		CreateIndexResponse response = client.indices().create(request,RequestOptions.DEFAULT);
		System.out.println(response);
	}
	
	//获取索引
	@Test
	void getindex() throws IOException {
		GetIndexRequest getIndexRequest = new GetIndexRequest("fl_index");
		boolean exist = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
		System.out.println(exist);
	}
	
	//删除索引
	@Test
	void delindex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("fl_index");
		AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println(response);
	}
	
	//添加文档
	@Test 
	void adddocument() throws IOException {
//		Users user = new Users("fl",24);
//		IndexRequest request = new IndexRequest("fl_index1");
//		//规则
//		request.id("1");
//		request.timeout(TimeValue.timeValueSeconds(1));
//		request.timeout("1s");
//		request.source(JSON.toJSONString(user), XContentType.JSON);
//		IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//		System.out.println(response);
	}
	
	//获取文档	
	@Test
	void testget() throws IOException {
		GetRequest getRequest = new GetRequest("fl_index1","1");
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		
		System.out.println(client.exists(getRequest, RequestOptions.DEFAULT));
	}
	
	//查询
	void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest("fl_index1");
		//构造搜索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		//精确匹配
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "fl");
		sourceBuilder.query(termQueryBuilder);
		//分页
//		sourceBuilder.from();
//		sourceBuilder.size();
			
		//设置查询时间
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest.source(sourceBuilder);
		//获取查询结果
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		for(SearchHit documentFields : searchResponse.getHits().getHits()) {
			System.out.println(documentFields.getSourceAsMap());
		}
 	}

	
}
