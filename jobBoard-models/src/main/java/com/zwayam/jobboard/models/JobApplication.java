package com.zwayam.jobboard.models;

import java.io.Serializable;


public class JobApplication extends Applies implements Serializable{
		
	public static final long serialVersionUID = 1L;

	public String candidateId;
	
	public String salutation;
				
	public String referrerEmailId;
		
	public String jobId;
		
	public String jobTitle;
		
	public int jobCode;
				
	public String countryCode;
	
	public String temporaryFileName;
	
	public Integer modifiedByUserId;
	
	public java.util.Date applicationModifiedDate;
	
	public Integer companyId;
	
	public Character status;
	
	public String currentStatus = "Pending";
	
	public String currentStatusCopy;
	
	public String currentStatusMigrated;

	public String nextStatus;
	
	public String location;
		
	public String experience;
	
	public String dateOfBirth;
	
	public String applicantWorkflowId;
	
	public Boolean isBlocked = true;
	
	public Integer isResumePresent;
	
	public String picture;

	public Boolean isTalentPoolBlocked = false;
	
	public String fileName;
	
	public Integer isPdfResumeExists = 0 ;
	
	public String jobUrl;
	
	public String source;
	
	public String subSource;
	
	public String otherSource;
		
	public String currentStage;


	
	public String getName() {

		if (this.lastName != null) {
			return (this.firstName + " " + this.lastName).trim();
		} else {
			return this.firstName;
		}
	}
}
