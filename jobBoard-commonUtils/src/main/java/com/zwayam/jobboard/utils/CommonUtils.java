package com.zwayam.jobboard.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class CommonUtils {
	
	static final Logger logger = LogManager.getLogger(CommonUtils.class);

	
	public static String splitAndGetJobUrl(String jobUrl) {
		String[] urlSplit = jobUrl.split("/");
		return urlSplit[urlSplit.length-2];
	}
	
	public static String getDomainName(String jobUrl) {
		URL url;
		try {
			url = new URL(jobUrl);
			return url.getProtocol()+"://"+url.getAuthority();
		} catch (MalformedURLException e) {
			return jobUrl;
		}
	}
	
	public static String removeSpaceSpecialChar(String inStr){
		String strOut = null;
		if(inStr != null && !inStr.isEmpty()){
			strOut = inStr.replaceAll("[^\\w-.]+", "").trim();
		}
		return strOut;
	}
	

	
	public static void MutlipartEntity(File resume, String joburl, String request, HttpPost httpPost) {
		
		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", new FileBody(resume));
			builder.addPart("joburl", new StringBody(joburl));
			builder.addPart("data", new StringBody(request));
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			logger.info("exception while multipart entity file convertion"+e);

		}
	}

}
