/**
 * This interface lists the methods that a DAO will need to function
 * inside the banking system.
 * 
 * Andrew Curry, Project 0
 */
package dao;

import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;

public interface BankDAO {
	
	
	/**
	 * Can only be set once.
	 * @param name : The name of the storage resource. EG, filename or database address.
	 * @return boolean reflecting whether the name was set correctly
	 */
	public boolean setResourceName(String name);
	
	public String getResourceName();
	
	/**
	 * Fetches the bank account with the given ID number from the data storage.
	 * If no such account exists, the resulting BankAccount object will have type NONE.
	 * @param accID
	 * @return BankAccount object
	 */
	public BankAccount readBankAccount(int accID);
	
	/**
	 * Fetches all bank accounts in the data storage.
	 * @return
	 */
	public List<BankAccount> readAllBankAccounts();
	
	
	/**
	 * Fetches the user profile with the given ID number from the data storage.
	 * If no such account exists, the resulting UserProfile object will have type NONE.
	 * @param userID
	 * @return UserProfile object
	 */
	public UserProfile readUserProfile(int userID);
	
	/**
	 * Fetches all user profiles in the data storage.
	 * @return
	 */
	public List<UserProfile> readAllUserProfiles();
	
	/**
	 * Fetches the TransactionRecord with the given ID number from the data storage.
	 * If no such account exists, the resulting TransactionRecord object will have type NONE.
	 * @param recID
	 * @return TransactionRecord
	 */
	public TransactionRecord readTransactionRecord(int recID);
	
	/**
	 * Fetches all TransactionRecords in the data storage.
	 * @return
	 */
	public List<TransactionRecord> readAllTransactionRecords();
	
	/**
	 * Writes the given BankData object to the data storage. WILL overwrite if matching
	 * data is already present.
	 * @param bd
	 * @return true if write successful, false otherwise.
	 */
	public boolean write(BankData bd);
	
	/**
	 * Writes each of the BankData objects in the given List to the data storage. 
	 * WILL overwrite if matching data is already present.
	 * @param bd
	 * @return true if write successful, false otherwise.
	 */
	public boolean write(List<BankData> data);
	
}
