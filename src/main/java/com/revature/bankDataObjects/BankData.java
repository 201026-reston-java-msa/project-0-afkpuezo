/**
 * the BankData class represents things like accounts, user profiles, and transactions -
 * individual nuggets of data that the program will need to run. Each specific type of
 * data will have its own class which extends this one.
 * 
 * Andrew Curry, Project 0
 */
package com.revature.bankDataObjects;

import java.security.PublicKey;

public abstract class BankData {
	
	public enum BankDataType {
		BANK_ACCOUNT, USER_PROFILE, TRANSACTION_RECORD, NONE
	}
	
	// class variables
	public static final BankDataType DATA_TYPE = BankDataType.NONE;
	
	// instance variables
	private int id;

	
	// getset
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * clumsy way of having each class update its type
	 * @return
	 */
	public abstract BankDataType getBankDataType();
}
