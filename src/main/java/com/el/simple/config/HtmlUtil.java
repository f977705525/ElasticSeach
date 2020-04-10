package com.el.simple.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.el.simple.pojo.Content;


@Component
public class HtmlUtil {
	public List<Content> getjd(String keyword) throws MalformedURLException, IOException {
		//获取请求 https://search.jd.com/Search?keyword=java
		String url ="https://search.jd.com/Search?keyword="+keyword;
		//解析网页
		 Document document = Jsoup.parse(new URL(url),30000);
		 Element element = document.getElementById("J_goodsList");
		 //获取所有li
		 Elements elements = element.getElementsByTag("li");
		 
		 ArrayList<Content> contents = new ArrayList<Content>();
		 //获取元素中的内容
		 for(Element e:elements) {
			 String img = e.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
			 String price = e.getElementsByClass("p-price").eq(0).text();
			 String title = e.getElementsByClass("p-name").eq(0).text();
			 Content content = new Content();
			 content.setImg(img);
			 content.setPrice(price);
			 content.setTitle(title);
			 contents.add(content);
		 }
		 return contents;
	}

}
