package gov.usdot.cv.service.rest;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import gov.usdot.cv.service.util.CustomClassLoader;

public class SemiValidator {

	private CustomClassLoader loader;
	private Object coder;
	private StringWriter debugWriter;
	private Class<?> uperCoder, j2735Util, ConnectedVehicleMessageLookup;
	private Method  decodeMethod, getMessageTypesMethod, decodeWithTypeMethod, enableAutomaticDecoding;
	
    public SemiValidator(String jarName) throws MalformedURLException, FileNotFoundException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        loader = new CustomClassLoader( jarName );
        initialize();
    }
    
    private synchronized void initialize() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		// call static method initialize: gov.usdot.asn1.generated.j2735.J2735.initialize();
    	Class<?> j2735 = loader.loadClass("gov.usdot.asn1.generated.j2735.J2735");
		Method initializeMethod = j2735.getMethod("initialize");
		initializeMethod.invoke(null);
		
		// load classes
		uperCoder = loader.loadClass("com.oss.asn1.Coder");
		j2735Util = loader.loadClass("gov.usdot.asn1.j2735.J2735Util");
    	ConnectedVehicleMessageLookup = loader.loadClass("gov.usdot.asn1.j2735.msg.ids.ConnectedVehicleMessageLookup");
    	
    	// load methods
    	getMessageTypesMethod = ConnectedVehicleMessageLookup.getMethod("getMessageList");
    	decodeMethod = j2735Util.getMethod("decode", uperCoder, byte[].class);
    	decodeWithTypeMethod = j2735Util.getMethod("decode", uperCoder, byte[].class, String.class);
    	enableAutomaticDecoding = uperCoder.getMethod("enableAutomaticDecoding");
		
		// call static method initialize: gov.usdot.asn1.generated.j2735.J2735.getPERUnalignedCoder();
		Method getPERUnalignedCoderMethod = j2735.getMethod("getPERUnalignedCoder");
		coder = getPERUnalignedCoderMethod.invoke(null);
		
		// Turn on automatic decoding of OpenTypes. The coder will automatically decode the OpenType with a component relation constraint or a type constraint applied.
		enableAutomaticDecoding.invoke(coder);
		
		// initialize the coder
		initCoder(coder);
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
    		
	    	Object decodedData = name != null ? decode( bytes, name ) : decode( bytes );
    		
	    	if ( decodedData != null ) {
	    		decodedMessage = (String) decodedData.toString();
	    		messageName = decodedData.getClass().getName();	    		
	    		return formatResult(messageName, decodedMessage);
	    	}
	    	throw new SemiValidatorException(name == null ?
    				"Couldn't auto-detect message type. Select intended message type and rerun the validation." :
    				"Couldn't decode message");
    	} catch (InvocationTargetException ex ) {
    		throw formatException(messageName, ex.getCause());
    	} catch (Exception ex) {
    		throw new SemiValidatorException(formatResult("Unknown", ex.getMessage()));
    	}
    }
    
    public Object getMessageTypes() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	return getMessageTypesMethod.invoke(coder);
    }
    
    private Object decode( byte[] bytes ) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DecodeFailedException, DecodeNotSupportedException {
    	return decodeMethod.invoke(coder, coder, bytes);
    }
    
    private Object decode( byte[] bytes, String name ) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DecodeFailedException, DecodeNotSupportedException {
    	return decodeWithTypeMethod.invoke(coder, coder, bytes, name);
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
