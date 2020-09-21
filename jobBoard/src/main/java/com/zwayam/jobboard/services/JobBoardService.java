package com.zwayam.jobboard.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zwayam.ai.parser.model.ParsedResult;
import com.zwayam.jobboard.ApplicationConstants;
import com.zwayam.jobboard.models.Applies;
import com.zwayam.jobboard.models.Job;
import com.zwayam.jobboard.models.JobApplication;
import com.zwayam.jobboard.models.JobMatchesForApplication;
import com.zwayam.jobboard.models.JobRecommendationCriteria;
import com.zwayam.jobboard.models.Status;
import com.zwayam.jobboard.repos.AppliesRepo;
import com.zwayam.jobboard.repos.JobsRepo;
import com.zwayam.jobboard.utils.CommonUtils;



@Service
public class JobBoardService {
	
	@Autowired
	EsService esService;
	
	@Autowired
	JobsRepo jobRepo;
	
	@Autowired
	AppliesRepo appliesRepo;
	
	@Autowired
	CommonUtils commonUtils;
	
	@Autowired
	ObjectMapper objectMapper;
	
	static final Logger logger = LogManager.getLogger(JobBoardService.class);

	public JobMatchesForApplication getJobRecommendation(MultipartFile file, JobRecommendationCriteria data) throws Exception {
		
		logger.info("#" + Thread.currentThread().getId() + "-Entering parseAndRecommendJobs");
		
		if ( file == null ) {
			logger.info("#" + Thread.currentThread().getId()
					+ "-ResumeParsingForAutofill: No Resume get/Multipart file is null. Need to be addressed. \nJobApplication:");
			return new JobMatchesForApplication();
		}
	
		File resume=null;
		JobApplication jobApplication = new JobApplication();
		
		try {
			jobApplication.fileName = CommonUtils.removeSpaceSpecialChar(file.getOriginalFilename());  
			Blob blob = NonContextualLobCreator.INSTANCE.wrap(NonContextualLobCreator.INSTANCE.createBlob(file.getBytes()));
			if(blob==null) {
				return new JobMatchesForApplication(jobApplication);
			}
	
			resume = saveBlobToUniqueFile( blob, jobApplication.fileName);
			ParsedResult parsedResult = parseFile(resume);
	
			if (parsedResult == null) {
				logger.info("Parsing failed, not recommended jobs, returning selected job");
				return new JobMatchesForApplication(jobApplication);
			}
			assignParsedData(jobApplication, parsedResult);
			List<Job> jobs = esService.recommendJobs(parsedResult.profile.directInfoList,data.requiredJobsCount);
			return new JobMatchesForApplication(jobApplication, jobs,parsedResult); 
									
		} catch (Exception e) {
			logger.error("#" + Thread.currentThread().getId() + "-Error occured in resume parsing,", e);
			return new JobMatchesForApplication(jobApplication);
			
		} finally {
			if (resume != null) {
				try {
					resume.delete();
				} catch (Exception e) {
					logger.error("resume delete", e);
				}
			}
		}
	}
	
	


	public ParsedResult parseFile(File profileFile) throws Exception {
		
		ParsedResult parsedResult = parseByPassingFileToParser(profileFile);
		if(parsedResult == null) {
			logger.error("parsedResult is null " + profileFile.getName());
			ParsedResult pResult = new ParsedResult();
			pResult.docContent.errorMessage = "Error";
			return pResult;
		}
		String resultToLog = "parsed result for "+profileFile.getName() + ", ";
		resultToLog += parsedResult.profile.parsedApplicantName == null? "null":parsedResult.profile.parsedApplicantName + ", ";
		resultToLog += parsedResult.profile.email == null? "null":parsedResult.profile.email + ", ";
		resultToLog += parsedResult.profile.phone == null? "null":parsedResult.profile.phone + ", ";

		logger.error(resultToLog);
		return parsedResult;
	}
	
	private void assignParsedData(JobApplication jobApplication, ParsedResult parsedResult) {
		if (parsedResult != null) {
			if (parsedResult.profile.parsedApplicantName != null) {
				if (parsedResult.profile.parsedApplicantName.contains(" ")) {
					int index = parsedResult.profile.parsedApplicantName.lastIndexOf(" ");
					jobApplication.firstName = parsedResult.profile.parsedApplicantName.substring(0, index);
					jobApplication.lastName = parsedResult.profile.parsedApplicantName.substring(index + 1);
				} else {
					jobApplication.firstName=parsedResult.profile.parsedApplicantName;
				}
			}
			if (parsedResult.profile.email != null) {
				jobApplication.emailId = parsedResult.profile.email.toString()
						.replaceAll(ApplicationConstants.REMOVE_SPECIAL_CHARACTERS, "");
			}
			if (parsedResult.profile.phone != null) {
				jobApplication.phoneNo = parsedResult.profile.phone.toString()
						.replaceAll(ApplicationConstants.REMOVE_SPECIAL_CHARACTERS, "");
			}
		}
	}
	
	private ParsedResult parseByPassingFileToParser(File file) throws ClientProtocolException, IOException {

		StringBuffer buffer = new StringBuffer();
		ObjectMapper objectMapper = new ObjectMapper();
		CloseableHttpResponse httpResponse = null;
		BufferedReader bufferedReader = null;
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
			HttpPost httpPost = new HttpPost(
					ApplicationConstants.PARSER_URL + "/resumeParserAPI/parseFileAndReturnParsedResult");
		    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		    builder.addPart("resumeFile", new FileBody(file));
		    httpPost.setHeader("Accept", "application/json");
		    httpPost.setEntity(builder.build());
			logger.error("Calling parseFileAndReturnParsedResult api");
			httpResponse = httpClient.execute(httpPost);
			logger.error("Returned from parseFileAndReturnParsedResult api");
			bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				buffer.append(line);
			}
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper.readValue(buffer.toString(), ParsedResult.class);

		}catch(Exception e) {
			logger.error(e);
			return objectMapper.readValue(buffer.toString(), ParsedResult.class);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	


	public Job getJob(String jobUrl) {
		return jobRepo.findByReferenceNumber(jobUrl);
	}


	public Object addProfile(String jobUrl, MultipartFile file, String requestData) throws SQLException, IOException {
		String url = CommonUtils.splitAndGetJobUrl(jobUrl);
		String fileName = CommonUtils.removeSpaceSpecialChar(file.getOriginalFilename());
		Blob blob = NonContextualLobCreator.INSTANCE.wrap(NonContextualLobCreator.INSTANCE.createBlob(file.getBytes()));
		File resume = saveBlobToUniqueFile( blob, fileName);
		StringBuffer buffer = new StringBuffer();
		ObjectMapper objectMapper = new ObjectMapper();

		CloseableHttpResponse httpResponse = null;
		BufferedReader bufferedReader = null;
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
			if(!ApplicationConstants.IS_LOCAL) {
				ApplicationConstants.CONTROLLER_URL = CommonUtils.getDomainName(jobUrl);
			}
			HttpPost httpPost = new HttpPost(
					ApplicationConstants.CONTROLLER_URL + "/ApplyJobAPI/addProfileWithParsedResultsCareersite/");
			CommonUtils.MutlipartEntity(resume, url, requestData, httpPost);
			logger.error("Calling parseFileAndReturnParsedResult api");
			httpResponse = httpClient.execute(httpPost);
			logger.error("Returned from parseFileAndReturnParsedResult api");
			bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				buffer.append(line);
			}
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper.readValue(buffer.toString(), Status.class);

		}catch(Exception e) {
			logger.error(e);
			return new Status(500,"something when wrong");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}

		}
	}

	public Status saveApplyData(String appliesDetails) {
		try {
			Applies apply = objectMapper.readValue(appliesDetails, Applies.class);
			apply.updatedDate = new Date();
			appliesRepo.save(apply);
			return new Status(200,"save successfully");
		}catch(Exception e) {
			return new Status(500,"exception while saving apply data");
		}
	}


	public File saveBlobToUniqueFile( Blob blob, String fileName) throws  SQLException, IOException {
		UUID uniqueKey = UUID.randomUUID();
		logger.info("File Writing Starting");
		File 	resume = new File(ApplicationConstants.RESUME_FOLDER_PATH + uniqueKey + "-" + fileName);
		FileOutputStream output = new FileOutputStream(resume);
		InputStream input = blob.getBinaryStream();
		byte[] buffer = new byte[input.available()];
		while (input.read(buffer) > 0) {
			output.write(buffer);
		}
		output.close();
		input.close();
		logger.info("file writing ended.");
	
		return resume;
	}



}
