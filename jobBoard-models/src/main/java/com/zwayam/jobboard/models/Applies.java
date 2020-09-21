package com.zwayam.jobboard.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@DynamicUpdate(value=true)
@Table(name = "applies")
public class Applies {
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;
	
	public String firstName;
	
	public String lastName;
		
	public String emailId;
	
	public String jobReferenceNumber;
	
	public String jobProvider;

	public Date updatedDate;
	
	public Date dateOfBirth;
		
	public String phoneNo;
	
	public String applyType;
	

}
