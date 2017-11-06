package gov.usdot.cv.service.rest;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.util.HexTool;
import com.sun.jersey.test.framework.JerseyTest;

public class SemiValidatorTest extends JerseyTest {
	
	private static SemiValidator v23validator;
	private static Map<DecodeMessageResource.EncodeVersion, SemiValidator> validators;
	
	public SemiValidatorTest() throws Exception {
		super("gov.usdot.cv.service.rest");
	}

	@Test
	public void testDirect() throws MalformedURLException, FileNotFoundException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, DecodeFailedException, DecodeNotSupportedException, SemiValidatorException {
	
		v23validator = new SemiValidator("fedgov-cv-asn1-1.0.0-SNAPSHOT.jar");
		validators = new HashMap<DecodeMessageResource.EncodeVersion, SemiValidator>();
		validators.put(DecodeMessageResource.EncodeVersion.v23, v23validator);
		
		byte[] bytes = HexTool.parseHex("118000000008004f8580", false);
		
		v23validator.validate(bytes);
		validators.get(DecodeMessageResource.EncodeVersion.v23).validate(bytes);
		
		// Only one version, don't need negative
/*		try{
			validators.get(DecodeMessageResource.EncodeVersion.v21).validate("30138002009c810107820400000000830420013e16");
			validators.get(DecodeMessageResource.EncodeVersion.v22).validate("300d8002009c810107820420013e16");
			assertTrue(false);
		} catch ( SemiValidatorException ex ) {
			System.out.println(ex.getMessage());
			assertTrue(true);
		}*/
	}
}
