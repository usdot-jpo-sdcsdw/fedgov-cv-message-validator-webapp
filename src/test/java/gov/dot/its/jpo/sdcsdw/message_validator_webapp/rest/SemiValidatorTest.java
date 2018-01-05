package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sun.jersey.test.framework.JerseyTest;

import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.exception.CodecException;
import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.per.HexPerData;
import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.DecodeMessageResource;
import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.SemiValidator;
import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.SemiValidatorException;

public class SemiValidatorTest extends JerseyTest {
	
	private static SemiValidator vMVPvalidator;
	private static Map<DecodeMessageResource.EncodeVersion, SemiValidator> validators;
	
	public SemiValidatorTest() throws Exception {
		super("gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest");
	}

	@Test
	public void testDirect() throws MalformedURLException, FileNotFoundException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, CodecException, SemiValidatorException {
	
		vMVPvalidator = new SemiValidator();
		validators = new HashMap<DecodeMessageResource.EncodeVersion, SemiValidator>();
		validators.put(DecodeMessageResource.EncodeVersion.vMVP, vMVPvalidator);
		
		byte[] bytes = new HexPerData("118000000008004f8580").getPerData();
		
		vMVPvalidator.validate(bytes);
		validators.get(DecodeMessageResource.EncodeVersion.vMVP).validate(bytes);
		
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
