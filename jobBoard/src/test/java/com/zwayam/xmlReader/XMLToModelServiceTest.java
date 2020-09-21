package com.zwayam.xmlReader;

import org.junit.Test;

import com.zwayam.jobboard.utils.CommonUtils;

public class XMLToModelServiceTest {
	final  CommonUtils commonUtils = new CommonUtils();

/*	@Test
	public void getXMLFormURLTest(){
		xml.getXMLFormURL();
	}*/
	
	/*@Test
	public void getXMLDocument() {
		xml.collectXMLFiles("http://feeds.zwayam.com/TalentDotcom/consolidated.xml");
	}*/
	
	@Test
	public void getUrl() {
		String s = commonUtils.splitAndGetJobUrl("https://jobs.bjaz.in/#!/job-view/sales-manager-bancasssurance-mysore-2020081411223241/?source=Talent.com");
		System.out.println(s);
	}
	
	@Test
	public void getdomain() {
		String s = commonUtils.getDomainName("https://jobs.bjaz.in/#!/job-view/sales-manager-bancasssurance-mysore-2020081411223241/?source=Talent.com");
		System.out.println(s);
	}
}
