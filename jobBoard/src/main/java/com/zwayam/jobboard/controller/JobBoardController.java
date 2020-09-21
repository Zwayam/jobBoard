package com.zwayam.jobboard.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.zwayam.jobboard.models.Job;
import com.zwayam.jobboard.models.JobMatchesForApplication;
import com.zwayam.jobboard.models.JobRecommendationCriteria;
import com.zwayam.jobboard.models.Status;
import com.zwayam.jobboard.services.JobBoardService;

@RestController
@RequestMapping("/JobBoardController")
public class JobBoardController {

	@Autowired
	public JobBoardService jobBoardService;
	
	public static final Logger logger = LogManager.getLogger(JobBoardController.class);
	
	@RequestMapping(value = "/parseAndRecommendJobs", method = RequestMethod.POST)
	public @ResponseBody JobMatchesForApplication parseAndRecommendJobs(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "data") Object data) throws Exception {
		JobRecommendationCriteria reportsFilter = new Gson().fromJson(data.toString(),JobRecommendationCriteria.class);
		return jobBoardService.getJobRecommendation(file,reportsFilter);
	}
	
	@RequestMapping(value = "/getJobForUrl", method = RequestMethod.POST)
	public @ResponseBody Job getJobForUrl(@RequestParam(value ="jobUrl") String jobUrl) {
		try {
			return jobBoardService.getJob(jobUrl);
		} catch (Exception e) {
			logger.error("Exception while getJobForUrl details", e);
			return new Job();
		}
	}
	
	@RequestMapping(value = "/appliesDetails", method = RequestMethod.POST)
	public @ResponseBody  Status appliesDetails(@RequestParam(value ="appliesDetails") String appliesDetails) {
			return jobBoardService.saveApplyData(appliesDetails);
	}
	
	@RequestMapping(value = "/addProfileWithParsedResultsCareersite", method = RequestMethod.POST)
	public @ResponseBody Object addProfileWithParsedResults(
			@RequestParam(value = "jobUrl") String jobUrl,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "data") String requestData, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			return jobBoardService.addProfile(jobUrl, file, requestData);
		} catch (Exception e) {
			logger.error("Exception while addProfile With ParsedResults from Careersite", e);
			return null;
		}
		
	}
	
}
