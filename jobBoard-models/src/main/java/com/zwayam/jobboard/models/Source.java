package com.zwayam.jobboard.models;

import java.util.Date;
import java.util.HashSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name = "source")
public class Source {

	@XmlJavaTypeAdapter(JaxbDateSerializer.class)
	public Date lastBuildDate;
 
	 @XmlElement(name="publisherurl")
	 public String publisherurl;
	 
	 @XmlElement(name="publisher")
	 public String publisher;
	 
	 @XmlElement(name="job")
	 public HashSet<Job> jobs;
 
}
