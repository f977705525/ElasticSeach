package com.el.simple.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.el.simple.Service.ContentService;

@RestController
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@GetMapping("/parse/{keyword}")
	public boolean getJfinfo(@PathVariable(name = "keyword")String keyword) throws MalformedURLException, IOException {
		return contentService.parseJd(keyword);
	}
	
	@GetMapping("/search/{keyword}/{pageno}/{pagesize}")
	public List<Map<String, Object>> search(@PathVariable("keyword")String keyword,@PathVariable("pageno")int pageno,@PathVariable("pagesize")int pagesize) throws IOException{
		return contentService.searchpage(keyword, pageno, pagesize);
	}
	 
}
