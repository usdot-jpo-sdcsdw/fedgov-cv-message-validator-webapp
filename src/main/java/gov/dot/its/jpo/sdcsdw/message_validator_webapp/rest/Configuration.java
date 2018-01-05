package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Configuration {
	
    public Configuration() throws JSONException
    {
        JSONObject application = new JSONObject();
        application.put(TITLE_KEY, TITLE);
        application.put(SUBTITLE_KEY, SUBTITLE);
        application.put(VERSION_KEY, VERSION);
        application.put(RELEASE_KEY, RELEASE);      
        application.put(COPYRIGHT_KEY, COPYRIGHT);
        configuraton.put(APPLICATION_KEY, application);
        configuraton.put(DEFAULT_INDEX_KEY, 0);
        JSONArray validators = new JSONArray();
        configuraton.put(VALIDATORS_KEY, validators);
    }
    
    @Override
    public String toString()
    {
        return configuraton.toString();
    }
    
    public String toString(int indentFactor) throws JSONException
    {
        return configuraton.toString(indentFactor);
    }
    
	static final String VALIDATORS_KEY = "validators";
    static final String DEFAULT_INDEX_KEY = "defaultIndex";
    static final String APPLICATION_KEY = "application";
    static final String COPYRIGHT_KEY = "copyright";
    static final String RELEASE_KEY = "release";
    static final String VERSION_KEY = "version";
    static final String SUBTITLE_KEY = "subtitle";
    static final String TITLE_KEY = "title";
    static final String TITLE = "Connected Vehicle Message Validator";
	static final String SUBTITLE = "Unified Model Architecture Using J2735 3/2016";
	static final String VERSION = "1.4";
	static final String RELEASE = "March 11, 2017, 10am EST";	
	static final String COPYRIGHT = "2014-2017";
	
	void setDefaultIndex(int index) throws JSONException
	{
		if ( index >= 0 ) {
			configuraton.put(DEFAULT_INDEX_KEY, index);
		}
	}
	
	void addConfiguration(String version, List<String> messages)
	        throws JSONException
	{
		JSONObject validator = new JSONObject();
		validator.put(VERSION_KEY, version);
		validator.put("messages", messages);
		JSONArray validators = configuraton.getJSONArray(VALIDATORS_KEY);
		validators.put(validator);
	}
	
	private JSONObject configuraton = new JSONObject();
}
