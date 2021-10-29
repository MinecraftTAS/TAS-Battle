package de.pfannekuchen.bedwars.exceptions;

/**
 * A parse exception is being thrown, when a configuration entry could not be parsed.
 * @author Pancake
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParseException(String reason, Throwable exactCause) {
		super(reason, exactCause);
	}
	
	public ParseException(Throwable exactCause) {
		super(exactCause);
	}
	
}
