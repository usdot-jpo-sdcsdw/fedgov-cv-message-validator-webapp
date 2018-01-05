package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.Asn1Type;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.Asn1Types;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.PerXerCodec;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.PerXerCodec.TypeGuessResult;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.exception.CodecException;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.exception.CodecFailedException;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.per.RawPerData;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.xer.RawXerData;

public class SemiValidator
{
    public SemiValidator()
    {
        
    }

    public synchronized String validate( byte[] bytes )
            throws SemiValidatorException
    {
        return validate(bytes, null);
    }

    public synchronized String validate( byte[] bytes, String name )
            throws SemiValidatorException
    {
        	String messageName = null;
        	String decodedMessage = null;
        	try {
        		
    	    	TypeGuessResult<String> decodedData = name != null ? decode( bytes, name ) 
    	    	                                                   : decode( bytes );
        		
    	    	if ( decodedData.isSuccesful() ) {
    	    		decodedMessage = (String) decodedData.getData();
    	    		boolean needsPrefix = name == null || name.startsWith(LEGACY_PREFIX);
    	    		String prefix = needsPrefix ? LEGACY_PREFIX : "";
    	    		messageName = prefix + decodedData.getType().getName();
    	    		return formatResult(messageName, decodedMessage);
    	    	}
    	    	
    	    	throw new SemiValidatorException( name == null
    	    	                                ? NO_AUTO_DETECT_MESSAGE
        				                    : NO_DECODE_MESSAGE);
        	} catch (Exception ex) {
        		throw new SemiValidatorException(formatResult("Unknown", ex.getMessage()));
        	}
    }
    
    public Iterable<String> getMessageTypes()
    {
        	ArrayList<String> messageTypes = new ArrayList<String>();
        	
        	for (Asn1Type type : Asn1Types.getAllTypes()) {
        		messageTypes.add(type.getName());
        	}
        	
        	return messageTypes;
    }
    
    private static final String NO_DECODE_MESSAGE = "Couldn't decode message";

    private static final String NO_AUTO_DETECT_MESSAGE = "Couldn't auto-detect message type. Select intended message type and rerun the validation.";

    private static final String LEGACY_PREFIX = "gov.usdot.asn1.generated.j2735.semi.";
        
    private TypeGuessResult<String> decode( byte[] bytes ) throws CodecException
    {
        	return PerXerCodec.guessPerToXer(Asn1Types.getAllTypes(),
        	                                 bytes,
        	                                 RawPerData.unformatter,
        	                                 RawXerData.formatter);
    }
    
    private TypeGuessResult<String> decode( byte[] bytes, String name )
            throws CodecException
    {
        	Asn1Type type;
        	
        	String nameWithoutPrefix;
        	
        	
        	nameWithoutPrefix = name.startsWith(LEGACY_PREFIX) 
        	                  ? name.replace(LEGACY_PREFIX, "")
        	                  : name;
        	
        	type = Asn1Types.getAsn1TypeByName(nameWithoutPrefix);
        	
        	try {
            	String xer = PerXerCodec.perToXer(type, 
            	                                  bytes, 
            	                                  RawPerData.unformatter, 
            	                                  RawXerData.formatter);
            	
            	return new TypeGuessResult<String>(type, xer);
        	} catch (CodecFailedException ex) {
        	    return new TypeGuessResult<String>();
        	}
    }
    
    private String formatResult(String messageName, String decodedMessage)
    {
    	    JSONObject result = new JSONObject();
		try {
		    String actualMessageName = StringUtils.isBlank(messageName) 
		                             ? "Unknown"
		                             : messageName;
			result.put("messageName", actualMessageName);
			result.put("decodedMessage", decodedMessage);
		} catch (JSONException ignored) {
		}
		return result.toString();
    }
}
