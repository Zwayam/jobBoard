package com.zwayam.jobboard.crawler.services;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zwayam.EsClient.Client;
import com.zwayam.jobboard.ApplicationConstants;
import com.zwayam.jobboard.models.Job;


@Service
public class EsService {

	@Autowired
	private ObjectMapper objMapper;
	
	static final Logger logger = LogManager.getLogger(EsService.class);
	
	public void bulkUpdateJobs( Set<Job> jobs) {
		BulkRequest bulkRequest = new BulkRequest(); 
		try {
			RestHighLevelClient client  = Client.getInstance(ApplicationConstants.ES_SERVER).restHighLevelClient;
			deleteAllJobByQuery();

			jobs.forEach(job -> {
	            IndexRequest indexRequest = new IndexRequest("careersite","job",job.jobProvider+'-'+job.referenceNumber).
	                    source(objMapper.convertValue(job, Map.class));
	            
	            bulkRequest.add(indexRequest);
	        });
			logger.error("Before indexing all the Jobs ");
			BulkResponse res = client.bulk(bulkRequest);
			logger.error("Successfully indexed all jobs in millis",res.getTookInMillis());
		} catch (Exception e) {
			logger.error("Exception while Indexing all jobs", e);
			if(bulkRequest.payloads() == null) {
				logger.error("Request payload is empty. No jobs found to be indexed.");
			}
		}		
		
	}
	
	public void deleteAllJobByQuery() {
		try {
			String queryBody = "{" + 
					"    \"query\": {" + 
					"        \"match_all\": {}" + 
					"    }" + 
					"}";
			
			String url = "http://" + ApplicationConstants.ES_SERVER + ":9200"+"/" + ApplicationConstants.ES_INDEX + "/job/_delete_by_query";	
			
			@SuppressWarnings({ "deprecation", "resource" })
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(queryBody));
			client.execute(request);
			logger.info("Successfully deleted jobs ");
		} catch (Exception e) {
			logger.error("Exception while deleting jobs ", e);
		}
	}
	
}
