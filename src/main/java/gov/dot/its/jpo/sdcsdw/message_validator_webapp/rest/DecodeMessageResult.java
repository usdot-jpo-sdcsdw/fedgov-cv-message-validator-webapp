/** LEGACY CODE
 * 
 * This was salvaged in part or in whole from the Legacy System. It will be heavily refactored or removed.
 */
package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DecodeMessageResult
{
	
	public enum Status
	{
		Success, Error;
	}
	
	public Status getStatus() { return this.status; }
	public String getMessage() { return this.message; }
	
	public void setStatus(Status status) { this.status = status; }
	public void setMessage(String message) { this.message = message; }
	
	private Status status;
    private String message;
}