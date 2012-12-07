package com.modzelewski.nfcgb;

@SuppressWarnings("serial")
public class ValidatorException extends RuntimeException {

	public ValidatorException(String errorMessage) {
		super(errorMessage);
	}

}
