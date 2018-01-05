package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sun.jersey.test.framework.JerseyTest;

import gov.dot.its.jpo.sdcsdw.asn1.perxercodec.per.HexPerData;
import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.DecodeMessageResource;
import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.SemiValidator;

public class SemiValidatorTest extends JerseyTest
{	
	public SemiValidatorTest() throws Exception
	{
		super(PACKAGE_NAME);
	}

	@Test
	public void testDirect() throws Exception
	{
	    SemiValidator vMVPvalidator = new SemiValidator();
	    Map<DecodeMessageResource.EncodeVersion, SemiValidator> validators =
	            new HashMap<DecodeMessageResource.EncodeVersion, SemiValidator>();
	    
		validators.put(DecodeMessageResource.EncodeVersion.vMVP, vMVPvalidator);
		
		byte[] bytes = new HexPerData(TEST_PER_HEX).getPerData();
		
		vMVPvalidator.validate(bytes);
		validators.get(DecodeMessageResource.EncodeVersion.vMVP).validate(bytes);
	}
	
	private static final String PACKAGE_NAME = DecodeMessageServiceTest.class
	                                                                   .getPackage()
	                                                                   .getName();
	
	private static final String TEST_PER_HEX = "118000000008004f8580";
}
