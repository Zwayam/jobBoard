package com.zwayam.jobboard.crawler.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zwayam.jobboard.crawler.services.CrawlerService;
import com.zwayam.jobboard.models.Status;

@RestController
@RequestMapping("/crawlerController")
public class CrawlerController {

	@Autowired
	public CrawlerService crawlerService;
	
	public static final Logger logger = LogManager.getLogger(CrawlerController.class);
	
	@RequestMapping(value="/collectJobs", method = RequestMethod.POST)
	public @ResponseBody Status collectJobs(){
		return crawlerService.collectJobs();
	}
}
