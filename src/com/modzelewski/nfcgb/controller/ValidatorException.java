package com.modzelewski.nfcgb.controller;

@SuppressWarnings("serial")
public class ValidatorException extends RuntimeException {

	public ValidatorException(String errorMessage) {
		super(errorMessage);
	}

}
