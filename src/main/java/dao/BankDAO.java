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
	 * Name should be set in constructor, and not be changed
	 * @return
	 */
	public String getResourceName();
	
	/**
	 * Fetches the bank account with the given ID number from the data storage.
	 * If no such account exists, the resulting BankAccount object will have status
	 *  NONE and -1 in other fields
	 * @param accID
	 * @return BankAccount object
	 */
	public BankAccount readBankAccount(int accID) throws BankDAOException;
	
	/**
	 * Fetches all bank accounts in the data storage.
	 * @return
	 */
	public List<BankAccount> readAllBankAccounts() throws BankDAOException;
	
	
	/**
	 * Fetches the user profile with the given ID number from the data storage.
	 * If no such account exists, the resulting UserProfile object will have type NONE.
	 * @param userID
	 * @return UserProfile object
	 */
	public UserProfile readUserProfile(int userID) throws BankDAOException;
	
	/**
	 * Fetches all user profiles in the data storage.
	 * @return
	 */
	public List<UserProfile> readAllUserProfiles() throws BankDAOException;
	
	/**
	 * Fetches the TransactionRecord with the given ID number from the data storage.
	 * If no such account exists, the resulting TransactionRecord object will have type NONE.
	 * @param recID
	 * @return TransactionRecord
	 */
	public TransactionRecord readTransactionRecord(int recID) throws BankDAOException;
	
	/**
	 * Fetches all TransactionRecords in the data storage.
	 * @return
	 */
	public List<TransactionRecord> readAllTransactionRecords() throws BankDAOException;
	
	/**
	 * Writes the given BankData object to the data storage. WILL overwrite if matching
	 * data is already present.
	 * @param bd
	 */
	public void write(BankData bd) throws BankDAOException;
	
	/**
	 * Writes each of the BankData objects in the given List to the data storage. 
	 * WILL overwrite if matching data is already present.
	 * @param bd
	 */
	public void write(List<BankData> toWrite) throws BankDAOException;
	
}