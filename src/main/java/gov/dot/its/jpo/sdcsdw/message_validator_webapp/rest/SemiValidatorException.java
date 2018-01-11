/** LEGACY CODE
 * 
 * This was salvaged in part or in whole from the Legacy System. It will be heavily refactored or removed.
 */
package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

public class SemiValidatorException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public SemiValidatorException(String message)
	{
		super(message);
	}
	
	public SemiValidatorException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public SemiValidatorException(Throwable cause)
	{
		super(cause);
	}
}
