/**
 * This DAO implementation used a Postgres server to maintain data.
 * 
 * @author Andrew Curry
 */
package dao;

import java.util.ArrayList;
import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.BankAccount.BankAccountStatus;
import com.revature.bankDataObjects.BankAccount.BankAccountType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresDAO implements BankDAO {
	
	// constants
	private static final String ACCOUNT_STATUS_OPEN = "OPEN";
	private static final String ACCOUNT_STATUS_CLOSED = "CLOSED";
	private static final String ACCOUNT_STATUS_PENDING = "PENDING";
	private static final String ACCOUNT_STATUS_NONE = "NONE"; // shouldn't be used?
	
	private static final String ACCOUNT_TYPE_NONE = "NONE"; // shouldn't be used?
	private static final String ACCOUNT_TYPE_SINGLE = "SINGLE";
	private static final String ACCOUNT_TYPE_JOINT = "JOINT";
	
	private static final String PROFILE_TYPE_NONE = "NONE";
	private static final String PROFILE_TYPE_CUSTOMER = "CUSTOMER";
	private static final String PROFILE_TYPE_EMPLOYEE = "EMPLOYEE";
	private static final String PROFILE_TYPE_ADMIN = "ADMIN";
	
	private static final String TRANSACTION_TYPE_ACCOUNT_REGISTERED = "ACCOUNT_REGISTERED";
	private static final String TRANSACTION_TYPE_ACCOUNT_APPROVED = "ACCOUNT_APPROVED";
	private static final String TRANSACTION_TYPE_ACCOUNT_CLOSED = "ACCOUNT_CLOSED";
	private static final String TRANSACTION_TYPE_ACCOUNT_OWNER_ADDED = "ACCOUNT_OWNER_ADDED";
	private static final String TRANSACTION_TYPE_ACCOUNT_OWNER_REMOVED = "ACCOUNT_OWNER_REMOVED";
	private static final String TRANSACTION_TYPE_FUNDS_TRANSFERED = "FUNDS_TRANSFERED";
	private static final String TRANSACTION_TYPE_FUNDS_DEPOSITED = "FUNDS_DEPOSITED";
	private static final String TRANSACTION_TYPE_FUNDS_WITHDRAWN = "FUNDS_WITHDRAWN";
	private static final String TRANSACTION_TYPE_USER_REGISTERED = "USER_REGISTERED";
	private static final String TRANSACTION_TYPE_NONE = "NONE";
	
	private static final String GENERIC_SQL_EXCEPTION_MESSAGE
			= "ALERT: There was a problem communicating with the database.";
	private static final String NULL_CONNECTION_MESSAGE
			= "ALERT: Unable to make connection with database.";
	private static final String RESULT_SET_ERROR_MESSAGE
			= "ALERT: There was a problem processing results from the database.";
	
	// class / static variables
	
	// instance variables
	//private String databaseAddress;
	//private String databaseUsername;
	//private String databasePassword;
	
	// constructor
	public PostgresDAO() {

		DatabaseUtil.loadConfiguration();
	}
	
	// methods from DAO interface ------------------------------------------------

	/**
	 * Returns address of the database
	 */
	@Override
	public String getResourceName() {

		return DatabaseUtil.getAddress();
	}

	/**
	 * Fetches the bank account with the given ID number from the data storage.
	 * If no such account exists, the resulting BankAccount object will have the
	 * status NONE, and -1 in other fields
	 * @param accID
	 * @return BankAccount object
	 */
	@Override
	public BankAccount readBankAccount(int accID) throws BankDAOException {

		
		try (Connection conn = DatabaseUtil.getConnection()){
			
			if (conn == null) {
				throw new BankDAOException(NULL_CONNECTION_MESSAGE);
			}
			
			String sql = "SELECT * FROM bank_account WHERE account_id = ?;";
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setInt(1, accID);
			ResultSet accSet = pstm.executeQuery();
			
			BankAccount ba = new BankAccount();
			while (accSet.next()) { // should only be one result
				ba.setId(accSet.getInt("account_id"));
				ba.setStatus(stringToBankAccountStatus(accSet.getString("status")));
				ba.setType(stringToBankAccountType(accSet.getString("type")));
				ba.setFunds(accSet.getInt("funds"));
			}
			
			ba.setOwners(getAccountOwnerList(conn, accID));
			
			return ba;
			//return buildBankAccountFromResults(accSet, ownerSet);
		}
		catch(SQLException e) {
			throw new BankDAOException(GENERIC_SQL_EXCEPTION_MESSAGE);
		}
	}

	/**
	 * Fetches all bank accounts in the data storage.
	 * @return
	 */
	@Override
	public List<BankAccount> readAllBankAccounts() throws BankDAOException {
		
		try (Connection conn = DatabaseUtil.getConnection()){
			
			if (conn == null) {
				throw new BankDAOException(NULL_CONNECTION_MESSAGE);
			}
			
			String sql = "SELECT * FROM bank_account";
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet accSet = pstm.executeQuery();
			
			List<BankAccount> accounts = new ArrayList<>();
			
			while (accSet.next()) { // should only be one result
				BankAccount ba = new BankAccount();
				int accID = accSet.getInt("account_id");
				ba.setId(accID);
				ba.setStatus(stringToBankAccountStatus(accSet.getString("status")));
				ba.setType(stringToBankAccountType(accSet.getString("type")));
				ba.setFunds(accSet.getInt("funds"));
				ba.setOwners(getAccountOwnerList(conn, accID));
				accounts.add(ba);
			}
			
			return accounts;
		}
		catch(SQLException e) {
			throw new BankDAOException(GENERIC_SQL_EXCEPTION_MESSAGE);
		}
	}

	/**
	 * Fetches the user profile with the given ID number from the data storage.
	 * If no such account exists, the resulting UserProfile object will have type NONE.
	 * @param userID
	 * @return UserProfile object
	 */
	@Override
	public UserProfile readUserProfile(int userID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Fetches the user profile with the given username from the data storage.
	 * If no such account exists, the resulting UserProfile object will have type NONE.
	 * @param userID
	 * @return UserProfile object
	 */
	@Override
	public UserProfile readUserProfile(String username) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Fetches all user profiles in the data storage.
	 * @return
	 */
	@Override
	public List<UserProfile> readAllUserProfiles() throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Fetches the TransactionRecord with the given ID number from the data storage.
	 * If no such account exists, the resulting TransactionRecord object will have type NONE.
	 * @param recID
	 * @return TransactionRecord
	 */
	@Override
	public TransactionRecord readTransactionRecord(int recID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Fetches all TransactionRecords in the data storage.
	 * @return
	 */
	@Override
	public List<TransactionRecord> readAllTransactionRecords() throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Fetches all TransactionRecords that were carried out by the given user.
	 * Returns an empty list if there are no matches.
	 * @param actingUserId
	 * @return
	 * @throws BankDAOException
	 */
	@Override
	public List<TransactionRecord> readTransactionRecordByActingUserId(int actingUserID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Fetches all TransactionRecords that involved the given account (as source or destination)
	 * Returns an empty list if there are no matches.
	 * @param accID
	 * @return
	 * @throws BankDAOException
	 */
	@Override
	public List<TransactionRecord> readTransactionRecordByAccountId(int accID) throws BankDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Writes the given BankData object to the data storage. WILL overwrite if matching
	 * data is already present.
	 * @param bd
	 */
	@Override
	public void write(BankData bd) throws BankDAOException {
		// TODO Auto-generated method stub

	}

	/**
	 * Writes each of the BankData objects in the given List to the data storage. 
	 * WILL overwrite if matching data is already present.
	 * @param bd
	 */
	@Override
	public void write(List<BankData> toWrite) throws BankDAOException {
		// TODO Auto-generated method stub

	}

	/** 
	 * @return the highest ID currently assigned to a user profile
	 */
	@Override
	public int getHighestUserProfileID() throws BankDAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * @return the highest ID currently assigned to a bank account
	 */
	@Override
	public int getHighestBankAccountID() throws BankDAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * @return the highest ID currently assigned to a transaction record
	 */
	@Override
	public int getHighestTransactionRecordID() throws BankDAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Determines whether or not the given username is free to use. Used during registration, to make sure that usernames are unique.
	 * @param username
	 * @return
	 */
	@Override
	public boolean isUsernameFree(String username) throws BankDAOException {
		// TODO Auto-generated method stub
		return false;
	}

	// helper methods -------------------------------------------------------------
	
	/**
	 * Gets the list of owning user IDs for the indicated account
	 * @param conn : an already open connection
	 * @param accID
	 * @return
	 * @throws BankDAOException
	 */
	private List<Integer> getAccountOwnerList(Connection conn, int accID) throws BankDAOException{
		
		try {
			String sql = "SELECT user_id FROM account_ownership WHERE account_id = ?";
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setInt(1, accID);
			ResultSet ownerSet = pstm.executeQuery();
			
			List<Integer> owners = new ArrayList<>();
			
			while (ownerSet.next()) { // should be AT LEAST one
				//System.out.println("DEBUG: in ownerSet loop");
				int ownerID = ownerSet.getInt("user_id");
				owners.add(ownerID);
			}
			return owners;
		}
		catch (SQLException e) {
			throw new BankDAOException(RESULT_SET_ERROR_MESSAGE);
		}
	}
	
	// util methods ------------------------------------------------------------
	
		/**
		 * String -> enum
		 * @param s
		 * @return
		 */
		private BankAccountType stringToBankAccountType(String s) {

			switch (s) { // set the account type
				case ACCOUNT_TYPE_JOINT:
					return BankAccountType.JOINT;
				case ACCOUNT_TYPE_SINGLE:
					return (BankAccountType.SINGLE);
				default:
					return BankAccountType.NONE;
			}
		}
		
		/**
		 * String -> enum
		 * @param s
		 * @return
		 */
		private BankAccountStatus stringToBankAccountStatus(String s) {
			
			switch(s) { // set the status
				case ACCOUNT_STATUS_OPEN:
					return BankAccountStatus.OPEN;
				case ACCOUNT_STATUS_CLOSED:
					return BankAccountStatus.CLOSED;
				case ACCOUNT_STATUS_PENDING:
					return BankAccountStatus.PENDING;
				default:
					return BankAccountStatus.NONE;
			}
		}
}
