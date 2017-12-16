package com.db.awmd.challenge.exception;

public class OperationFailedException extends RuntimeException {
	public OperationFailedException(String message){
		super(message);
	}
}
