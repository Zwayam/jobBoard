package com.zwayam.jobboard.models;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;

@Entity
@Table(name = "jobs")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Job {
	

	@Id
    @JacksonXmlCData
	@XmlElement(name="referencenumber")
	@Column(name = "referencenumber")
    public String referenceNumber;
	
    @JacksonXmlCData
	@XmlElement(name="title")
	@Column(name = "job_title")
    public String jobTitle;
	
	@XmlJavaTypeAdapter(JaxbDateSerializer.class)
	@XmlElement(name="date")
	@Column(name = "created_date")
	public Date createdDate;
	
    @JacksonXmlCData
	@Column(name = "company")
	@XmlElement(name="company")
    public String companyName;

    @JacksonXmlCData
	 @XmlElement(name="url")
	@Column(name = "job_url")
    public String jobUrl;

    @JacksonXmlCData
	 @XmlElement(name="country")
	@Column(name = "country")
    public String country;
	
    @JacksonXmlCData
	 @XmlElement(name="description")
	@Column(name = "description")
    public String longDescription;
    
	@Column(name = "jdSkillsKnown")
    public String jdSkillsKnown="";
	
	@Transient
	public String skillSet = "";
	
	@Column(name = "job_provider")
	public String jobProvider;

	public String domainName;

	@Transient
	public List<String> jdskillsKnownList = new ArrayList<>();
	
	@Transient
	public Integer hashCode;

	@Transient
	public float scoreForRecommendation;
	
	 	@Override
	    public boolean equals(Object obj) {

		 Job job = (Job)obj;

	        if(referenceNumber.equals(job.referenceNumber) && jobProvider.equals(job.jobProvider))
	        {
	            hashCode = job.hashCode;
	            return true;
	        }else{
	            hashCode = super.hashCode();
	            return false;
	        }
	    }
	
}
