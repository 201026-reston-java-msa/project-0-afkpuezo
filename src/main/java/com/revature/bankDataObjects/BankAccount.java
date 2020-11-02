/**
 * The BankAccount class is used to encapsulate information about
 * bank accounts in the banking system.
 * 
 * Andrew Curry, Project 0
 */
package com.revature.bankDataObjects;

import java.util.ArrayList;
import java.util.List;

import com.revature.bankDataObjects.BankData.BankDataType;

public class BankAccount extends BankData {
	
	// enums
	enum BankAccountStatus{
		NONE, OPEN, CLOSED, PENDING
	}
	
	// might have a 'AccountType' enum here eventually
	
	
	// class variables
	public static final BankData.BankDataType DATA_TYPE = BankDataType.BANK_ACCOUNT;
	
	
	// instance variables
	// id in super
	List<Integer> owners; // could be a single or joint account
	private BankAccountStatus status;
	private int funds; // could be a special Money class or something
	
	
	// constructor(s)
	public BankAccount() {
		super();
		owners = new ArrayList<Integer>();
		// status = BankAccountStatus.NONE;
		funds = 0;
	}
	
	public BankAccount(int id) {
		super();
		super.setId(id);
		owners = new ArrayList<Integer>();
		// status = BankAccountStatus.NONE;
		funds = 0;
	}
	
	
	// virtual methods
	
	
	@Override
	public BankDataType getBankDataType() {
		return BankAccount.DATA_TYPE;
	}
	
	// util methods
	
	
	@Override
	public String toString() {
		return "ACCOUNT " + super.getId(); 
	}

	
	// getters and setters
	
	
	// technically I'm not sure if the List should have getters/setters or just
	// add/remove methods, for now I'll keep both in.
	public List<Integer> getOwners() {
		return owners;
	}

	
	public void setOwners(List<Integer> owners) {
		this.owners = owners;
	}
	
	
	/**
	 * Prevents repeats
	 * @param ownerID
	 */
	public void addOwner(int ownerID) {
		if (!owners.contains(ownerID)) {
			owners.add(ownerID);
		}
	}
	
	
	public void removeOwner(int ownerID) {
		owners.remove(ownerID);
	}

	
	public BankAccountStatus getStatus() {
		return status;
	}

	
	public void setStatus(BankAccountStatus status) {
		this.status = status;
	}

	public double getFunds() {
		return funds;
	}

	public void setFunds(int funds) {
		this.funds = funds;
	}
}
