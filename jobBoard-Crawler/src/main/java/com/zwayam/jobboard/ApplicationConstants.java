package com.zwayam.jobboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConstants {
	
	public static String URL_LIST;
	public static String OLD_FILE_LIST;
	public static String NEW_FILE_LIST;
	public static String ES_SERVER;
	public static String ES_INDEX;
	public static String PARSER_URL;

	@Value("${uriList}")
	public  void setURL_LIST(String uRL_LIST) {
		URL_LIST = uRL_LIST;
	}
	
	@Value("${oldFilePath}")
	public void setOLD_FILE_LIST(String oLD_FILE_LIST) {
		OLD_FILE_LIST = oLD_FILE_LIST;
	}
	
	@Value("${newFilePath}")
	public void setNEW_FILE_LIST(String nEW_FILE_LIST) {
		NEW_FILE_LIST = nEW_FILE_LIST;
	}

	@Value("${es.server}")
	public void setES_SERVER(String eS_SERVER) {
		ES_SERVER = eS_SERVER;
	}
	
	@Value("${es.index}")
	public void setES_INDEX(String eS_INDEX) {
		ES_INDEX = eS_INDEX;
	}
	
	@Value("${env.com.zwayam.parser.url}")
	public void setPARSER_URL(String pARSER_URL) {
		PARSER_URL = pARSER_URL;
	}
	
}

