package com.wonkglorg.database.exception;

public class DatabaseDriverNotFoundException extends RuntimeException{
	public DatabaseDriverNotFoundException() {
	}
	
	public DatabaseDriverNotFoundException(String message) {
		super(message);
	}
	
	public DatabaseDriverNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DatabaseDriverNotFoundException(Throwable cause) {
		super(cause);
	}
}
