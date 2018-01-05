package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.Asn1Type;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.Asn1Types;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.PerXerCodec;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.PerXerCodec.TypeGuessResult;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.exception.CodecException;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.per.RawPerData;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.xer.RawXerData;

public class SemiValidator {

	private static final String LEGACY_PREFIX = "gov.usdot.asn1.generated.j2735.semi.";
	
	private ClassLoader loader;
	private Object coder;
	private StringWriter debugWriter;
	private Class<?> uperCoder, j2735Util, ConnectedVehicleMessageLookup;
	private Method  decodeMethod, getMessageTypesMethod, decodeWithTypeMethod, enableAutomaticDecoding;
	
    public SemiValidator() throws MalformedURLException, FileNotFoundException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    	loader = this.getClass().getClassLoader();
        initialize();
    }
    
    private synchronized void initialize() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	
    }
    
    private void initCoder( Object coder ) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		// call instance method enableEncoderDebugging
		Method enableEncoderDebuggingMethod = uperCoder.getMethod("enableEncoderDebugging");
		enableEncoderDebuggingMethod.invoke(coder);
		
		// call instance method enableDecoderDebugging
		Method enableDecoderDebuggingMethod = uperCoder.getMethod("enableDecoderDebugging");
		enableDecoderDebuggingMethod.invoke(coder);
		
		// call instance method enableRichDecoderExceptions
		Method enableRichDecoderExceptionsMethod = uperCoder.getMethod("enableRichDecoderExceptions");
		enableRichDecoderExceptionsMethod.invoke(coder);
		
		// call instance method with parameter: com.oss.asn1.Coder.setDecoderDebugOut(PrintWriter arg0)
		Method setDecoderDebugOutMethod = uperCoder.getMethod("setDecoderDebugOut", PrintWriter.class);
		debugWriter = new StringWriter();
		setDecoderDebugOutMethod.invoke(coder, new PrintWriter(debugWriter));
    }
    
    public synchronized String validate( byte[] bytes ) throws SemiValidatorException {
    	return validate(bytes, null);
    }

    public synchronized String validate( byte[] bytes, String name ) throws SemiValidatorException {
    	String messageName = null;
    	String decodedMessage = null;
    	try {
    		
	    	TypeGuessResult<String> decodedData = name != null ? decode( bytes, name ) : decode( bytes );
    		
	    	if ( decodedData.isSuccesful() ) {
	    		decodedMessage = (String) decodedData.getData();
	    		messageName = (name == null || name.startsWith(LEGACY_PREFIX) ? LEGACY_PREFIX : "") + decodedData.getType().getName();    		
	    		return formatResult(messageName, decodedMessage);
	    	}
	    	throw new SemiValidatorException(name == null ?
    				"Couldn't auto-detect message type. Select intended message type and rerun the validation." :
    				"Couldn't decode message");
    	} catch (Exception ex) {
    		throw new SemiValidatorException(formatResult("Unknown", ex.getMessage()));
    	}
    }
    
    public Iterable<String> getMessageTypes() {
    	ArrayList<String> messageTypes = new ArrayList<String>();
    	
    	for (Asn1Type type : Asn1Types.getAllTypes()) {
    		messageTypes.add(type.getName());
    	}
    	
    	return messageTypes;
    }
    
    private TypeGuessResult<String> decode( byte[] bytes ) throws CodecException {
    	return PerXerCodec.guessPerToXer(Asn1Types.getAllTypes(), bytes, RawPerData.unformatter, RawXerData.formatter);
    }
    
    private TypeGuessResult<String> decode( byte[] bytes, String name ) throws CodecException {
    	Asn1Type type;
    	
    	if (name.startsWith(LEGACY_PREFIX)) {
    		type = Asn1Types.getAsn1TypeByName(name.replace(LEGACY_PREFIX, "")); 
    	} else {
    		type = Asn1Types.getAsn1TypeByName(name);
    	}
    	String xer = PerXerCodec.perToXer(type, bytes, RawPerData.unformatter, RawXerData.formatter);
    	
    	return new TypeGuessResult<String>(type, xer);
    }
    
    private static final String lineSeparator =  System.getProperty("line.separator");
    
    private String formatResult(String messageName, String decodedMessage) {
    	JSONObject result = new JSONObject();
		try {
			result.put("messageName", !StringUtils.isBlank(messageName) ? messageName : "Unknown" );
			result.put("decodedMessage", decodedMessage);
		} catch (JSONException ignored) {
		}
		return result.toString();
    }
    
    private static String appendString(String base, String next) {
    	if ( StringUtils.isBlank(next) )
    		return base;
    	if ( StringUtils.isBlank(base) )
    		return next;
    	return base + lineSeparator + next;
    }
    
    private SemiValidatorException formatException(String messageName, Throwable throwable) {
		try {
			String errorText = throwable.getMessage().trim();
			
			Class<?> rdeClass = loader.loadClass("com.oss.coders.RichDecoderException");
			
			Method getDecodedData = rdeClass.getMethod("getDecodedData");
			Object decodedData = getDecodedData.invoke(throwable);
			String decodedText = null;
			if ( decodedData != null ) {
				decodedText = decodedData.toString().trim();
			}
			
			Method getContextTrace = rdeClass.getMethod("getContextTrace");
			Object contextTrace = getContextTrace.invoke(throwable);
			String contextText = null;
			if ( contextTrace != null && StringUtils.isNotBlank((String)contextTrace) )
				contextText = contextTrace.toString().trim();
			
			String debugText = null;
			if ( debugWriter.getBuffer().length() > 0 )
				debugText = debugWriter.toString();

			if ( StringUtils.isBlank(messageName) )
				messageName = contextText;
			
			if ( StringUtils.isBlank(messageName) )
				messageName = "UNKNOWN";
			
			String decodedMessage = appendString(errorText, contextText);
			decodedMessage = appendString(decodedMessage, decodedText);
			decodedMessage = appendString(decodedMessage, debugText);
			return new SemiValidatorException(formatResult(messageName, decodedMessage));
		} catch (Exception ex) {
			return new SemiValidatorException(formatResult("Unknown", ex.getMessage()));
		}
    }
    
}
