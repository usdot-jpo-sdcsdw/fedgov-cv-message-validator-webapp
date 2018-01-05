package gov.dot.its.jpo.sdcsdw.message_validator_webapp.rest;

public class DecodeMessageException extends Exception
{

	private static final long serialVersionUID = 7571113740158537819L;
	
	public DecodeMessageException(String message)
	{
		super(message);
	}
	
	public DecodeMessageException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}