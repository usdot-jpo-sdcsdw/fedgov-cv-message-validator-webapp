/** LEGACY CODE
 * 
 * This was salvaged in part or in whole from the Legacy System. It will be heavily refactored or removed.
 */
package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.Configuration;
import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.SemiValidator;
import gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest.DecodeMessageResource.EncodeVersion;

public class ConfigurationTest
{

	@Test
	public void test() throws JSONException
	{
		Configuration configuration = new Configuration();
		SemiValidator validator = new SemiValidator();
		List<String> messages = (List<String>)validator.getMessageTypes();
		configuration.addConfiguration(EncodeVersion.vMVP.getValue(), messages);
		Collections.sort(messages, Collections.reverseOrder());
		configuration.addConfiguration("MVP", messages);
		configuration.setDefaultIndex(1);
		configuration.setDefaultIndex(-1);
		int defaultIndex = new JSONObject(configuration.toString()).getInt(Configuration.DEFAULT_INDEX_KEY);
		assertEquals(1, defaultIndex);
		System.out.println(configuration.toString(3));
	}

}
