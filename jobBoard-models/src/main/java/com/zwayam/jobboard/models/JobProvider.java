package com.zwayam.jobboard.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "job_provider")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class JobProvider {
	
@Id
public String id;

public String jobListUrl;

public String fileContent;

public Date lastBuildDate;

}
