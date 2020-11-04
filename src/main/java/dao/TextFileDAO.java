/**
 * This is a textfile-based DAO class that I will use to test other features
 * until the final, database version is written.
 * 
 * Andrew Curry
 */
package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.LambdaConversionException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.UserProfile.UserProfileType;
import com.revature.bankDataObjects.BankAccount.BankAccountStatus;
import com.revature.bankDataObjects.BankAccount.BankAccountType;
import com.revature.bankDataObjects.TransactionRecord.TransactionType;

public class TextFileDAO implements BankDAO {
	
	// class/static variables
	private static final String USER_PROFILE_PREFIX = "PRF";
	private static final String BANK_ACCOUNT_PREFIX = "ACC";
	private static final String TRANSACTION_RECORD_PREFIX = "TRR";
	
	private static final String ACCOUNT_STATUS_OPEN = "OPN";
	private static final String ACCOUNT_STATUS_CLOSED = "CLS";
	private static final String ACCOUNT_STATUS_PENDING = "PND";
	private static final String ACCOUNT_STATUS_NONE = "NON"; // shouldn't be used?
	
	private static final String ACCOUNT_TYPE_NONE = "NON"; // shouldn't be used?
	private static final String ACCOUNT_TYPE_SINGLE = "SNG";
	private static final String ACCOUNT_TYPE_JOINT = "JNT";
	
	private static final String PROFILE_TYPE_NONE = "NON";
	private static final String PROFILE_TYPE_CUSTOMER = "CST";
	private static final String PROFILE_TYPE_EMPLOYEE = "EMP";
	private static final String PROFILE_TYPE_ADMIN = "ADM";
	
	private static final String TRANSACTION_TYPE_ACCOUNT_REGISTERED = "ACR";
	private static final String TRANSACTION_TYPE_ACCOUNT_APPROVED = "ACA";
	private static final String TRANSACTION_TYPE_ACCOUNT_CLOSED = "ACC";
	private static final String TRANSACTION_TYPE_ACCOUNT_OWNER_ADDED = "AOA";
	private static final String TRANSACTION_TYPE_ACCOUNT_OWNER_REMOVED = "AOR";
	private static final String TRANSACTION_TYPE_FUNDS_TRANSFERED = "FTR";
	private static final String TRANSACTION_TYPE_FUNDS_DEPOSITED = "FDP";
	private static final String TRANSACTION_TYPE_FUNDS_WITHDRAWN = "FWD";
	private static final String TRANSACTION_TYPE_USER_REGISTERED = "URG";
	private static final String TRANSACTION_TYPE_NONE = "NON";
	
	// instance variables
	private String filename;
	private BufferedReader reader;
	private BufferedWriter writer;
	
	// constructor(s)
	public TextFileDAO(String filename) throws BankDAOException {
		this.filename = filename;
		
		// make sure the filename is valid
		try {
			reader = openFileReader();
		}
		catch (BankDAOException e) {
			throw e;
		}
		
		closeFile(reader);
	}
	
	// methods from BankDAO interface
	
	@Override
	public String getResourceName() {
		return filename;
	}

	@Override
	/**
	 * Fetches the bank account with the given ID number from the data storage.
	 * If no such account exists, the resulting BankAccount object will have status
	 *  NONE and id of -1
	 * @param accID
	 * @return BankAccount object
	 */
	public BankAccount readBankAccount(int accID) throws BankDAOException {
		
		String entry = searchFile(BANK_ACCOUNT_PREFIX + " " + accID);
		return buildAccountFromEntry(entry);
	}

	/**
	 * Fetches all bank accounts in the data storage.
	 * @return
	 */
	@Override
	public List<BankAccount> readAllBankAccounts() throws BankDAOException {
		
		List<BankAccount> accounts = new ArrayList<>();
		List<String> entries = searchFileMultiple(BANK_ACCOUNT_PREFIX);
		
		for (String e : entries) {
			accounts.add(buildAccountFromEntry(e));
		}
		
		return accounts;
	}

	@Override
	public UserProfile readUserProfile(int userID) throws BankDAOException {
		
		String entry = searchFile(USER_PROFILE_PREFIX + " " + userID);
		return buildUserProfileFromEntry(entry);
	}

	@Override
	public List<UserProfile> readAllUserProfiles() throws BankDAOException {
		
		List<UserProfile> profiles = new ArrayList<>();
		List<String> entries = searchFileMultiple(USER_PROFILE_PREFIX);
		
		for (String e : entries) {
			profiles.add(buildUserProfileFromEntry(e));
		}
		
		return profiles;
	}

	@Override
	public TransactionRecord readTransactionRecord(int recID) throws BankDAOException {
		
		String entry = searchFile(TRANSACTION_RECORD_PREFIX + " " + recID);
		return buildTransactionRecordFromEntry(entry);
	}

	@Override
	public List<TransactionRecord> readAllTransactionRecords() throws BankDAOException {
		
		List<TransactionRecord> transactions = new ArrayList<>();
		List<String> entries = searchFileMultiple(TRANSACTION_RECORD_PREFIX);
		
		for (String e : entries) {
			transactions.add(buildTransactionRecordFromEntry(e));
		}
		
		return transactions;
	}

	/**
	 * Writes the given BankData object to the data storage. WILL overwrite if matching
	 * data is already present.
	 * @param bd
	 */
	@Override
	public void write(BankData bd) throws BankDAOException {
		List<BankData> toWrite = new ArrayList<BankData>();
		toWrite.add(bd);
		write(toWrite); // just use the list method
	}

	/**
	 * Writes each of the BankData objects in the given List to the data storage. 
	 * WILL overwrite if matching data is already present.
	 * @param bd
	 */
	@Override
	public void write(List<BankData> toWrite) throws BankDAOException {
		
		// get all of the data first so that we can look for duplicate/outdated entries
		List<String> data = searchFileMultiple("");
		
		for (BankData bd : toWrite) {
			
			if (bd.getClass() == UserProfile.class) {
				saveUserProfile((UserProfile)bd, data);
			}
			else if (bd.getClass() == BankAccount.class) {
				saveBankAccount((BankAccount)bd, data);
			}
			else if (bd.getClass() == TransactionRecord.class) {
				saveTransactionRecord((TransactionRecord)bd, data);
			}
			else {
				throw new BankDAOException("BankData subclass not supported in write: " + bd.getClass());
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			for (String line : data) {
				writer.write(line);
			}
			
			writer.close();			
		}
		catch (IOException e){
			throw new BankDAOException("IOException when writing to file");
		}
	}
	
	// helper / util methods for file IO
	
	/**
	 * Opens the file with a BufferedReader, handles the try/catch
	 * @return a reference to a new BufferedReader
	 */
	private BufferedReader openFileReader() throws BankDAOException {
		
		try {
			BufferedReader temp = new BufferedReader(new FileReader(filename));
			return temp;
		}
		catch (FileNotFoundException e) {
			throw (new BankDAOException("File not found: " + filename));
		}
	}
	
	/**
	 * Opens the file with a BufferedWriter, handles the try/catch
	 * @return a reference to a new BufferedReader
	 */
	private static BufferedWriter openFileWriter(String filename) throws BankDAOException {
		
		try {
			BufferedWriter temp = new BufferedWriter(new FileWriter(filename));
			return temp;
		}
		catch (IOException e) {
			throw (new BankDAOException("Could not open file for writing: " + filename));
		}
	}
	
	/**
	 * Closes the given BufferedReader or Writer 
	 * (should only be this.reader or this.writer)
	 * @param closeMe
	 * @throws BankDAOException
	 */
	private static void closeFile(Closeable closeMe) throws BankDAOException {
		
		try {
			closeMe.close();			
		}
		catch (IOException e){
			throw (new BankDAOException("Problem closing file in TextFileDAO."));
		}
	}
	
	// generic methods to search the file, used by BankDAO methods
	
	/**
	 * generic method to search the file for a given single data entry.
	 * If there is no matching entry, returns the empty string.
	 * @param tag : the type tag + ' ' + the ID, eg "PRF 101"
	 * @return a string containing all of the data in the entry matching the tag
	 */
	public String searchFile(String tag) throws BankDAOException {
		reader = openFileReader();
		String result = "";
		
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.startsWith(tag)){
					result = line;
					break;
				}
			}
		}
		catch (IOException e) {
			throw (new BankDAOException("Problem searching file: " + filename));
		}
		finally {
			closeFile(reader);
		}
		
		return result;
	}
	
	/**
	 * Returns a list of strings, where each string is a representation of one of the
	 * entries that matches the given tag (EG, pass "PRF" to get all profiles)
	 * Returns an empty list if no such entries are found.
	 * Returns all entries if passed the empty string.
	 * @param tags
	 * @return
	 * @throws BankDAOException
	 */
	public List<String> searchFileMultiple(String tag) throws BankDAOException {
		reader = openFileReader();
		List<String> results = new ArrayList<String>();
		
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.startsWith(tag)){
					results.add(line);
				}
			}
		}
		catch (IOException e) {
			throw (new BankDAOException("Problem searching file: " + filename));
		}
		finally {
			closeFile(reader);
		}
		
		return results;
	}
	
	/**
	 * Writes the given string into the data file. The string should represent a BankData object.
	 * If a matching entry (same type and id) already exists, it will be overwritten.
	 * @param entry
	 * @throws BankDAOException
	 */
	public void writeEntry(String entry) throws BankDAOException {
		
		// parse the entry for relevant data
		String[] tokens = entry.split(" ", 3);
		String tag = tokens[0] + " " + tokens[1];
		
		// get all of the data so that we can verify if the entry already exists
		List<String> fileData = searchFileMultiple("");
		
		for (String s : fileData) {
			if (s.startsWith(tag)) {
				fileData.remove(s); // safe to do this while iterating?
				break;
			}
		}
		
		// now add the entry and write the data back
		fileData.add(entry);
		
		writer = openFileWriter(filename);
		
		try {
			for (String s : fileData) {
				writer.write(s);
				writer.write("\n");
			}			
		}
		catch (IOException e) {
			throw (new BankDAOException("ALERT: writeEntry failed to write to file: " + filename));
		}
		finally {
			closeFile(writer);
		}
	}
	
	/**
	 * Writes each string in the given list into the data file. Each string should represent a BankData 
	 * object. If a matching entry (same type and id) already exists, it will be overwritten.
	 * @param entries
	 * @throws BankDAOException
	 */
	public void writeMultipleEntries(List<String> entries) throws BankDAOException {
		
		// get all of the data so that we can verify if entries already exist
		List<String> fileData = searchFileMultiple("");
		List<String> outputData = new ArrayList<>(entries); // copy the list
		
		// rather than just calling WriteEntry for each entry, going to optimize things a little
		// (actually is this even better?)
		for (String s : fileData) {
			String[] tokens = s.split(" ", 3);
			String tag = tokens[0] + " " + tokens[1];
			boolean was_found = false;
			
			for (String entry : outputData) {
				if (entry.startsWith(tag)) {
					was_found = true;
					break; // don't add duplicates/outdated entries
				}
			}
			
			if (!was_found) {
				outputData.add(s);
			}
		}
		
		writer = openFileWriter(filename);
		
		try {
			for (String s : outputData) {
				writer.write(s);
				writer.write("\n");
			}			
		}
		catch (IOException e) {
			throw (new BankDAOException("ALERT: writeMultipleEntries failed to write to file: " + filename));
		}
		finally {
			closeFile(writer);
		}
		
	} // end writeMultipleEntries method
	
	/**
	 * Returns a BankAccount object based on the given entry. If the entry is the empty string,
	 * an account with type NONE and id -1 will be returned.
	 * @param entry
	 * @return
	 */
	private BankAccount buildAccountFromEntry(String entry) {
		
		BankAccount ba = new BankAccount();
		
		if (entry.equals("")){ // if not found
			ba.setId(-1);
			ba.setType(BankAccountType.NONE);
		}
		else { // if found
			// sample entry for format: "ACC 444 OPN SNG 78923 101"
			String[] tokens = entry.split(" ");
			ba.setId(Integer.parseInt(tokens[1]));
			
			
			switch(tokens[2]) { // set the status
				case ACCOUNT_STATUS_OPEN:
					ba.setStatus(BankAccountStatus.OPEN);
					break;
				case ACCOUNT_STATUS_CLOSED:
					ba.setStatus(BankAccountStatus.CLOSED);
					break;
				case ACCOUNT_STATUS_PENDING:
					ba.setStatus(BankAccountStatus.PENDING);
					break;
				case ACCOUNT_STATUS_NONE:
					ba.setStatus(BankAccountStatus.NONE);
					break;
			}
			
			switch(tokens[3]) { // set the account type
				case ACCOUNT_TYPE_JOINT:
					ba.setType(BankAccountType.JOINT);
					break;
				case ACCOUNT_TYPE_SINGLE:
					ba.setType(BankAccountType.SINGLE);
					break;
				case ACCOUNT_TYPE_NONE:
					ba.setType(BankAccountType.NONE);
					break;
			}
			
			ba.setFunds(Integer.parseInt(tokens[4]));
			
			// the rest of the tokens are the ID numbers of the owner(s) of this account
			List<Integer> owners = new ArrayList<>();
			
			for (int i = 5; i < tokens.length; i++) {
				owners.add(Integer.parseInt(tokens[i]));
			}
			
			ba.setOwners(owners);
		}
		
		return ba;
	}
	
	/**
	 * Returns a UserProfile object based on the given entry. If the entry is the empty string,
	 * returns a UserProfile with ID -1 and type NONE.
	 * @param entry
	 * @return
	 */
	private UserProfile buildUserProfileFromEntry(String entry) {
		
		UserProfile up = new UserProfile();
		
		if (entry.equals("")){ // if not found
			up.setId(-1);
			up.setType(UserProfileType.NONE);
		}
		else {
			// sample entry for format "PRF 101 user pass CST 444"
			String[] tokens = entry.split(" ");
			up.setId(Integer.parseInt(tokens[1]));
			up.setUsername(tokens[2]);
			up.setPassword(tokens[3]);
			
			switch(tokens[4]) { // set the type
			case PROFILE_TYPE_ADMIN:
				up.setType(UserProfileType.ADMIN);
				break;
			case PROFILE_TYPE_CUSTOMER:
				up.setType(UserProfileType.CUSTOMER);
				break;
			case PROFILE_TYPE_EMPLOYEE:
				up.setType(UserProfileType.EMPLOYEE);
				break;
			case PROFILE_TYPE_NONE:
				up.setType(UserProfileType.NONE);
				break;
			}
			
			// the rest of the tokens are ID numbers corresponding to owned accounts
			List<Integer> ownedAccounts = new ArrayList<>();
			
			for (int i = 5; i < tokens.length; i++) {
				ownedAccounts.add(Integer.parseInt(tokens[i]));
			}
			up.setOwnedAccounts(ownedAccounts);
		}
		
		return up;
	}
	
	/**
	 * Returns a TransactionRecord object based on the given entry. If the entry is the empty string,
	 * returns a TransactionRecord with ID -1 and type NONE.
	 * @param entry
	 * @return
	 */
	private TransactionRecord buildTransactionRecordFromEntry(String entry) {
		
		TransactionRecord tr = new TransactionRecord();
		
		if (entry.equals("")) { // if not found
			tr.setId(-1);
			tr.setType(TransactionType.NONE);
		}
		else { // if found
			// sample entry for format: "TRR 123 3:00 FDD 101 -1 444 87654"
			String[] tokens = entry.split(" ");
			
			tr.setId(Integer.parseInt(tokens[1]));
			tr.setTime(tokens[2]);
			
			switch(tokens[3]) { // type
				case TRANSACTION_TYPE_ACCOUNT_REGISTERED:
					tr.setType(TransactionType.ACCOUNT_REGISTERED);
					break;
				case TRANSACTION_TYPE_ACCOUNT_APPROVED:
					tr.setType(TransactionType.ACCOUNT_APPROVED);
					break;
				case TRANSACTION_TYPE_ACCOUNT_CLOSED:
					tr.setType(TransactionType.ACCOUNT_CLOSED);
					break;
				case TRANSACTION_TYPE_FUNDS_DEPOSITED:
					tr.setType(TransactionType.FUNDS_DEPOSITED);
					break;
				case TRANSACTION_TYPE_FUNDS_WITHDRAWN:
					tr.setType(TransactionType.FUNDS_WITHDRAWN);
					break;
				case TRANSACTION_TYPE_FUNDS_TRANSFERED:
					tr.setType(TransactionType.FUNDS_TRANSFERED);
					break;
				case TRANSACTION_TYPE_USER_REGISTERED:
					tr.setType(TransactionType.USER_REGISTERED);
					break;
				case TRANSACTION_TYPE_NONE:
					tr.setType(TransactionType.NONE);
					break;
			}
			
			tr.setActingUser(Integer.parseInt(tokens[4]));
			tr.setSourceAccount(Integer.parseInt(tokens[5]));
			tr.setDestinationAccount(Integer.parseInt(tokens[6]));
			tr.setMoneyAmount(Integer.parseInt(tokens[7]));
		}
		
		return tr;
	}

	/**
	 * Properly updates the given data with the given user profile. Does NOT write to file
	 * @param up
	 * @param data
	 */
	private void saveUserProfile(UserProfile up, List<String> data) {
		
		String entry = USER_PROFILE_PREFIX + " " + up.getId();
		removeStartingWith(entry, data);
		
		// example entry for format: "PRF 101 user pass CST 444"
		entry = entry + " " + up.getUsername() + " " + up.getPassword();
		
		switch(up.getType()) {
			case NONE:
				entry = entry + " " + PROFILE_TYPE_NONE;
				break;
			case CUSTOMER:
				entry = entry + " " + PROFILE_TYPE_CUSTOMER;
				break;
			case EMPLOYEE:
				entry = entry + " " + PROFILE_TYPE_CUSTOMER;
				break;
			case ADMIN:
				entry = entry + " " + PROFILE_TYPE_ADMIN;
				break;
		}
		
		// now do added accounts
		for (Integer accID : up.getOwnedAccounts()) {
			entry = entry + " " + accID;
		}
		
		// we're done
		data.add(entry);
	}
	
	/**
	 * Properly updates the given data with the given bank account. Does NOT write to file
	 * @param up
	 * @param data
	 */
	private void saveBankAccount(BankAccount ba, List<String> data) {
		
		String entry = BANK_ACCOUNT_PREFIX + " " + ba.getId();
		removeStartingWith(entry, data);
		
		// example entry for format: "ACC 444 OPN SNG 78923 101"
		switch (ba.getStatus()) {
			case NONE:
				entry = entry + " " + ACCOUNT_STATUS_NONE;
				break;
			case OPEN:
				entry = entry + " " + ACCOUNT_STATUS_OPEN;
				break;
			case CLOSED:
				entry = entry + " " + ACCOUNT_STATUS_CLOSED;
				break;
			case PENDING:
				entry = entry + " " + ACCOUNT_STATUS_PENDING;
				break;
		}
		
		switch (ba.getType()) {
			case NONE:
				entry = entry + " " + ACCOUNT_TYPE_NONE;
				break;
			case SINGLE:
				entry = entry + " " + ACCOUNT_TYPE_SINGLE;
				break;
			case JOINT:
				entry = entry + " " + ACCOUNT_TYPE_JOINT;
				break;
		}
		
		entry = entry + " " + ba.getFunds();
		
		for (Integer ownerID : ba.getOwners()) {
			entry = entry + " " + ownerID;
		}
		
		data.add(entry);
	}
	
	/**
	 * Properly updates the given data with the given transaction record. Does NOT write to file
	 * @param up
	 * @param data
	 */
	private void saveTransactionRecord(TransactionRecord tr, List<String> data) {
		
		String entry = TRANSACTION_RECORD_PREFIX + " " + tr.getId();
		removeStartingWith(entry, data);
		
		// example entry for format: "TRR 123 3:00 FDP 101 -1 444 87654"
		entry = entry + " " + tr.getTime();
		
		switch (tr.getType()) { // big switch case
			case ACCOUNT_REGISTERED:
				entry = entry + " " + TRANSACTION_TYPE_ACCOUNT_REGISTERED;
				break;
			case ACCOUNT_APPROVED:
				entry = entry + " " + TRANSACTION_TYPE_ACCOUNT_APPROVED;
				break;
			case ACCOUNT_CLOSED:
				entry = entry + " " + TRANSACTION_TYPE_ACCOUNT_CLOSED;
				break;
			case FUNDS_TRANSFERED:
				entry = entry + " " + TRANSACTION_TYPE_FUNDS_TRANSFERED;
				break;
			case FUNDS_DEPOSITED:
				entry = entry + " " + TRANSACTION_TYPE_FUNDS_DEPOSITED;
				break;
			case FUNDS_WITHDRAWN:
				entry = entry + " " + TRANSACTION_TYPE_FUNDS_WITHDRAWN;
				break;
			case USER_REGISTERED:
				entry = entry + " " + TRANSACTION_TYPE_USER_REGISTERED;
				break;
			case ACCOUNT_OWNER_ADDED:
				entry = entry + " " + TRANSACTION_TYPE_ACCOUNT_OWNER_ADDED;
				break;
			case ACCOUNT_OWNER_REMOVED:
				entry = entry + " " + TRANSACTION_TYPE_ACCOUNT_OWNER_REMOVED;
				break;
			case NONE:
				entry = entry + " " + TRANSACTION_TYPE_NONE;
				break;
		}
		
		entry = entry + " " + tr.getActingUser() 
				+ " " + tr.getSourceAccount() 
				+ " " + tr.getDestinationAccount() 
				+ " " + tr.getMoneyAmount();
		
		data.add(entry);
	}
	
	/**
	 * (another) Helper method that searches the given list and removes the first entry discovered
	 * that starts with the given tag string. (There should never be repeats, so it assumes there
	 * is at most one old entry to be found)
	 * If no matching entry is found, does nothing
	 * @param tag
	 * @param data
	 */
	private void removeStartingWith(String tag, List<String> data) {
		
		for (String s : data) {
			if (s.startsWith(tag)) {
				data.remove(s);
				return;
			}
		}
	}
}