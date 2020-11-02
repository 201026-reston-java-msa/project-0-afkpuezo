/**
 * The TransactionRecord class is used to encapsulate information about
 * transactions made with the banking system.
 */
package com.revature.bankDataObjects;

import java.sql.Timestamp;

import com.revature.bankDataObjects.BankData.BankDataType;

public class TransactionRecord extends BankData {
	
	
	// enums
	enum TransactionType {
		ACCOUNT_REGISTERED, ACCOUNT_APPROVED, ACCOUNT_CLOSED, FUNDS_TRANSFERED,
		FUNDS_DEPOSITED, FUNDS_WITHDRAWN, USER_REGISTERED, ACCOUNT_OWNER_ADDED, 
		ACCOUNT_OWNER_REMOVED, NONE
	}
	
	
	// class variables
	public static final BankData.BankDataType DATA_TYPE = BankDataType.TRANSACTION_RECORD;
	
	// instance variables
	// id in super
	private Timestamp time;
	private TransactionType type;
	private int actingUser; // who triggered it
	private int sourceAccount; // might not be used in all transaction types
	private int destinationAccount; // might not be used in all transaction types
	private int moneyAmount; // might not be used in all transaction types
	
	
	// constructors
	public TransactionRecord() {
		super();
	}
	
	
	public TransactionRecord(int id) {
		super();
		super.setId(id);
	}
	
	
	// virtual methods
	
	
	@Override
	public BankDataType getBankDataType() {
		return TransactionRecord.DATA_TYPE;
	}
	
	// util methods
	
	
	@Override
	public String toString() {
		return "TRANSACTION " + super.getId();
	}


	// getters and setters
	public Timestamp getTime() {
		return time;
	}


	public void setTime(Timestamp time) {
		this.time = time;
	}


	public TransactionType getType() {
		return type;
	}


	public void setType(TransactionType type) {
		this.type = type;
	}


	public int getActingUser() {
		return actingUser;
	}


	public void setActingUser(int actingUser) {
		this.actingUser = actingUser;
	}


	public int getSourceAccount() {
		return sourceAccount;
	}


	public void setSourceAccount(int sourceAccount) {
		this.sourceAccount = sourceAccount;
	}


	public int getDestinationAccount() {
		return destinationAccount;
	}


	public void setDestinationAccount(int destinationAccount) {
		this.destinationAccount = destinationAccount;
	}


	public int getMoneyAmount() {
		return moneyAmount;
	}


	public void setMoneyAmount(int moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
	
	
}
