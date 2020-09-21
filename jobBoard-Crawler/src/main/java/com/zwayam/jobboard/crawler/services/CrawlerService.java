package com.zwayam.jobboard.crawler.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.zwayam.jobboard.ApplicationConstants;
import com.zwayam.jobboard.models.Job;
import com.zwayam.jobboard.models.JobProvider;
import com.zwayam.jobboard.models.JobProviderHistory;
import com.zwayam.jobboard.models.Source;
import com.zwayam.jobboard.models.Status;
import com.zwayam.jobboard.repos.JobProviderHistoryRepo;
import com.zwayam.jobboard.repos.JobProviderRepo;
import com.zwayam.jobboard.repos.JobsRepo;
import com.zwayam.ner.utils.files.HttpUtility;



@Service
public class CrawlerService {

	@Autowired
	public Gson gson;

	@Autowired
	public JobsRepo jobsRepository;
	
	@Autowired
	public EsService esService;
	
	@Autowired
	public JobProviderRepo jobProviderRepo;
	
	@Autowired
	public JobProviderHistoryRepo jobProviderHistoryRepo;
	
	static final Logger logger = LogManager.getLogger(CrawlerService.class);

	public Status collectJobs() {
		try {
			List<JobProvider> collectJobProviders = jobProviderRepo.findAll();
			generateContentFormURL(collectJobProviders);
			jobProviderRepo.save(collectJobProviders);
			saveToDB(collectJobProviders);
			return new Status(200,"jobs collected successfully");
		}catch (Exception e) {
			return new Status(500,"something went wrong "+e);
		}
		
	}

	private void saveToDB(List<JobProvider> jobProviders) {
		for(JobProvider jobProvider:jobProviders) {
			HashSet<Job> jobs = convertXMLtoModel(jobProvider.fileContent);
			try {
				setPostConvertionDetailsAndSaveToDB(jobs,jobProvider.id);
				esService.bulkUpdateJobs(jobs);
				logger.error("jobs updated to mysql and elastic successfully");
			}catch(Exception e) {
				logger.error("Exception while updating jobs from mysql and elastic "+e);
			}
		}
		
	}

	private Set<Job> setPostConvertionDetailsAndSaveToDB(Set<Job> jobs, String jobProviderId) {
		for(Job job:jobs) {
			try {
				job.jobProvider = jobProviderId;
				job.domainName = new URL(job.jobUrl).getAuthority();
				setExpressionForJob(job);
				jobsRepository.save(job);
			}catch(Exception e) {
				logger.error("Exception while updating jobs to mysql "+e);
			}
		}
		return jobs;
	}

	public void setExpressionForJob(Job job) {
		try {
			StringEntity stringEntity = HttpUtility.getUtf8HttpEntityFoJsonPost(job);
			String url = ApplicationConstants.PARSER_URL + "/expressionAPI/findJobExpressionAgainstJob";
			CloseableHttpResponse closeableHttpResponse = HttpUtility.executeHttpPost(url,stringEntity);
			String responseString = HttpUtility.getResponseData(closeableHttpResponse);
			logger.info(url + " response : " + closeableHttpResponse.getStatusLine().getStatusCode());
			com.zwayam.ai.parser.model.Job jobNew = new ObjectMapper()
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
					.readValue(responseString, new TypeReference<com.zwayam.ai.parser.model.Job>() {});
			job.jdSkillsKnown = jobNew.jdSkillsKnown;	
			String jdSkillsNew = jobNew.jdSkillsNew;
			List<String> jdskillsNewList = gson.fromJson(jdSkillsNew, ArrayList.class);
			job.jdskillsKnownList = gson.fromJson(job.jdSkillsKnown, ArrayList.class);	
			if(job.jdskillsKnownList!=null && !job.jdskillsKnownList.isEmpty()) {
				job.jdskillsKnownList.addAll(jdskillsNewList);
			}
		} catch (Exception e) {
			logger.error("Exception while finding expression", e);
		} 
	}
	

	private HashSet<Job> convertXMLtoModel(String content) {
		try {
			Source source = getModelFromContent(content);
			return  source.jobs;
		}catch(Exception e) {
			logger.error("Exception while getting job from XML", e);
		}
		return new HashSet<Job>();

	}

	public Source getModelFromContent(String content) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Source.class);              
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			TransformerFactory tf = TransformerFactory.newInstance();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
			DocumentBuilder db = dbf.newDocumentBuilder();  
			Document doc = db.parse(new InputSource(new StringReader(content)));  
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			Source source = (Source) jaxbUnmarshaller.unmarshal(new StringReader(output));
			return source;
		}catch (Exception e) {
			logger.error("Exception while getting job from XML", e);
			return null;
		}
	}

	public void generateContentFormURL(List<JobProvider> jobProviders) {
		try {
			for(JobProvider jobProvider : jobProviders) {
				if(jobProvider.fileContent == null || jobProvider.fileContent.isEmpty()) {
					jobProvider.fileContent = collectXMLFiles(jobProvider);
					Source source = getModelFromContent(jobProvider.fileContent);
					jobProvider.lastBuildDate = source.lastBuildDate;
				}else {
					compareAndCreateBackUp(jobProvider);
				}
			}
		}catch(Exception e) {
			logger.error("Exception while creating backup", e);
		}
		
	}

	public void compareAndCreateBackUp(JobProvider jobProvider) {
		try {
			Source oldDataOfProvider = getModelFromContent(jobProvider.fileContent);
			String content = collectXMLFiles(jobProvider);
			Source newDataOfProvider = getModelFromContent(content);
			if(!oldDataOfProvider.lastBuildDate.equals(newDataOfProvider.lastBuildDate) ) {
				saveJobProviderBackUp(jobProvider);
				jobProvider.fileContent = content;
				jobProvider.lastBuildDate = newDataOfProvider.lastBuildDate;
			}
		}catch(Exception e) {
			logger.error("Exception while creating backup", e);
		}
		
	}
	
	private void saveJobProviderBackUp(JobProvider jobProvider) {
		try {
			JobProviderHistory jobProviderHistory = new JobProviderHistory(jobProvider);
			jobProviderHistoryRepo.save(jobProviderHistory);
		}catch(Exception e) {
			logger.error("Exception while creating backup", e);
		}

	}

	
	public String collectXMLFiles(JobProvider jobProvider) {
		try {
     		URL url = new URL(jobProvider.jobListUrl);
			String content = IOUtils.toString( url, StandardCharsets.UTF_8);
			return content;
		}catch(Exception e) {
			logger.error("exception while collecting content for "+jobProvider.id,e);
			return null;
		}
	}
	
}
