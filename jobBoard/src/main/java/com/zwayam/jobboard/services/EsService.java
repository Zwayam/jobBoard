package com.zwayam.jobboard.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zwayam.EsClient.Client;
import com.zwayam.jobboard.ApplicationConstants;
import com.zwayam.jobboard.models.Job;

@Service
public class EsService {
	
	static final Logger logger = LogManager.getLogger(EsService.class);
	
	public  List<Job> recommendJobs( HashMap<String, Set<String>> directInfoList, Integer requiredJobsCount) {
		RestHighLevelClient client = null;
		List<Job> jobList = new ArrayList<>();
		try {
		client = Client.getInstance(ApplicationConstants.ES_SERVER).restHighLevelClient;
		BoolQueryBuilder boolForSkills = new BoolQueryBuilder();
		if(directInfoList.get("skill").size()>0) {
			GenericQueryFormationWildCard(directInfoList.get("skill"), boolForSkills,"jdskillsKnownList");
		}else {
			return new ArrayList<Job>(); 
		}
		BoolQueryBuilder jobRecoQuery = new BoolQueryBuilder();
		jobRecoQuery.must(boolForSkills);
		logger.error("first Query job Reco :"+jobRecoQuery);
		jobList = queryExecuteForJob(client, jobRecoQuery,requiredJobsCount);
		logger.error("first Query result job Reco :"+jobList.toString());
		} catch (Exception e) {
			logger.error("Exception while executing elastic job Recommendatin Query : "+e);
		}
		return jobList;
	}

	
	public List<Job> queryExecuteForJob(RestHighLevelClient client, BoolQueryBuilder boolQuery,int size)
			throws IOException {
		QueryBuilder query = QueryBuilders.boolQuery().must(boolQuery);
		SearchRequest searchRequestForJob = new SearchRequest(ApplicationConstants.ES_INDEX);
		searchRequestForJob.types("job");
		SearchSourceBuilder sourceBuilderForJob = new SearchSourceBuilder();
		sourceBuilderForJob.size(size);
		sourceBuilderForJob.query(query);
		searchRequestForJob.source(sourceBuilderForJob);
		logger.error("Query for career site API :: {}", searchRequestForJob);
		SearchResponse responseForJob = client.search(searchRequestForJob);
		logger.error("Response for career site API :: {}", responseForJob);
		JSONArray jobArray = new JSONObject(responseForJob.toString()).getJSONObject("hits").getJSONArray("hits");
		logger.error("Finished executing for career site API");
		return convertJobModel(jobArray);
	}
	
	private void GenericQueryFormationWildCard(Set<String> skillsAndRating, BoolQueryBuilder jobRecoSkills,
			String fieldName) {
		jobRecoSkills.should(QueryBuilders.termsQuery(fieldName, skillsAndRating)).minimumShouldMatch(1);
	}
	
	private List<Job> convertJobModel(JSONArray jobsList) {
		List<Job> jobs = new ArrayList<Job>();
		ObjectMapper obj = new ObjectMapper();
		for(int size = 0;size<jobsList.length();size++) {
			JSONObject elasticJobObject = new JSONObject(jobsList.get(size).toString());			
			try {
				Job job = obj.readValue(elasticJobObject.get("_source").toString(), Job.class);
				job.scoreForRecommendation = Float.parseFloat((elasticJobObject.get("_score").toString()));
				jobs.add(job);
			} catch (Exception e) {
				logger.error("Exception while converting ES response for careersite job reco", e);
			} 
			
		}
	return jobs;
	}
}
