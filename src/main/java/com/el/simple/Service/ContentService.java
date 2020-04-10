package com.el.simple.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.ScrollableHitSource.Hit;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Text;

import com.alibaba.fastjson.JSON;
import com.el.simple.config.HtmlUtil;
import com.el.simple.pojo.Content;


@Service
public class ContentService {
	
	@Autowired
	private RestHighLevelClient client;
	
	//解析数据放入索引
	public boolean parseJd(String keyword) throws MalformedURLException, IOException {
		List<Content> contents =  new HtmlUtil().getjd(keyword);
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("2m");
		for (int i = 0; i < contents.size(); i++) {
			bulkRequest.add(new IndexRequest("jd_list")
					.source(JSON.toJSONString(contents.get(i)),XContentType.JSON));
			
		}
		BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		return response.hasFailures();
	}
	//获取数据
	public List<Map<String, Object>> searchpage(String keyword,int pageno,int pagesize) throws IOException{
		if(pageno<=1) {
			pageno = 1;
		}
		SearchRequest searchRequest = new SearchRequest("jd_list");
		SearchSourceBuilder builder = new SearchSourceBuilder();
		builder.from(pageno);
		builder.size(pagesize);
		//精准匹配
		TermQueryBuilder title = QueryBuilders.termQuery("title", keyword);
		builder.query(title);
		builder.timeout(new TimeValue(60,TimeUnit.SECONDS));
		//代码高亮
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("title");
		highlightBuilder.preTags("<span style='color:red;'>");
		highlightBuilder.postTags("</span>");
		builder.highlighter(highlightBuilder);
		searchRequest.source(builder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(SearchHit documentFileds:searchResponse.getHits().getHits()) {
			Map<String, HighlightField> hightfield = documentFileds.getHighlightFields();
			HighlightField titleh = hightfield.get("title");			
			Map<String, Object> sourceAsMap = documentFileds.getSourceAsMap();
			if(title!=null) {
				org.elasticsearch.common.text.Text[] framents = titleh.fragments();
				String n_string = "";
				for(org.elasticsearch.common.text.Text text:framents) {
					n_string+= text;
				}
				sourceAsMap.put("title", n_string);
			}
			list.add(sourceAsMap);
		}
		return list;
	}

}
