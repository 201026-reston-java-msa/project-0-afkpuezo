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
import java.util.ArrayList;
import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;

public class TextFileDAO implements BankDAO {
	
	// class/static variables
	private final String USER_PROFILE_PREFIX = "PRF";
	private final String BANK_ACCOUNT_PREFIX = "ACC";
	private final String TRANSACTION_RECORD_PREFIX = "TRR";
	
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
		finally {
			closeFile(reader);
		}
	}
	
	// methods from BankDAO interfacea
	
	@Override
	public String getResourceName() {
		return filename;
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
	public boolean write(BankData bd) throws BankDAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write(List<BankData> data) throws BankDAOException {
		// TODO Auto-generated method stub
		return false;
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
	private BufferedWriter openFileWriter() throws BankDAOException {
		
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
	private void closeFile(Closeable closeMe) throws BankDAOException {
		
		try {
			closeMe.close();			
		}
		catch (IOException e){
			throw (new BankDAOException("Problem closing file: " + filename));
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
	 * returns a list of strings, where each string is a representation of one of the
	 * entries that matches the given tag (EG, pass "PRF" to get all profiles)
	 * @param tags
	 * @return
	 * @throws BankDAOException
	 */
	public List<String> searchFileMultiple(String tag) throws BankDAOException{
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

}
