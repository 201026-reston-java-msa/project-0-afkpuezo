/**
 * This DAO implementation used a Postgres server to maintain data.
 * 
 * @author Andrew Curry
 */
package dao;

import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDAO implements BankDAO {
	
	// class / static variables
	
	// instance variables
	//private String databaseAddress;
	//private String databaseUsername;
	//private String databasePassword;
	
	// constructor
	public PostgresDAO() {

		DatabaseUtil.loadConfiguration();
	}
	
	// util methods ------------------------------------------------------------
	
	
	
	// methods from DAO interface ------------------------------------------------

	@Override
	public String getResourceName() {

		return DatabaseUtil.getAddress();
	}

	@Override
	public BankAccount readBankAccount(int accID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BankAccount> readAllBankAccounts() throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile readUserProfile(int userID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile readUserProfile(String username) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserProfile> readAllUserProfiles() throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionRecord readTransactionRecord(int recID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransactionRecord> readAllTransactionRecords() throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransactionRecord> readTransactionRecordByActingUserId(int actingUserID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransactionRecord> readTransactionRecordByAccountId(int accID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(BankData bd) throws BankDAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(List<BankData> toWrite) throws BankDAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getHighestUserProfileID() throws BankDAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHighestBankAccountID() throws BankDAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHighestTransactionRecordID() throws BankDAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUsernameFree(String username) throws BankDAOException {
		// TODO Auto-generated method stub
		return false;
	}

}
