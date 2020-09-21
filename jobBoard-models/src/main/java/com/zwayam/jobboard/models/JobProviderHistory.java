package com.zwayam.jobboard.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "job_provider_history")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class JobProviderHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public String id;

	public String jobProvider;

	public String jobListUrl;

	public String fileContent;

	public Date contentUpdatedDate;
	
	public Date backUpCreatedDate = new Date();
	
	public JobProviderHistory(JobProvider jobProvider){
		this.jobProvider = jobProvider.id;
		this.jobListUrl = jobProvider.jobListUrl;
		this.fileContent = jobProvider.fileContent;
		this.contentUpdatedDate = jobProvider.lastBuildDate;
	}
}
