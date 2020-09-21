package com.zwayam.jobboard.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;
 
public class JaxbDateSerializer extends XmlAdapter<String, Date>{
 
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    @Override
    public String marshal(Date date) throws Exception {
        return dateFormat.format(date);
    }
 
    @Override
    public Date unmarshal(String date) throws Exception {
    	try {
            return dateFormat.parse(date.split(",")[1].trim());
    	}catch(Exception e) {
            return dateFormat1.parse(date.split(",")[1].trim());
    	}
    }
}