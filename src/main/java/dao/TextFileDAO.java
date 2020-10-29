/**
 * This is a textfile-based DAO class that I will use to test other features
 * until the final, database version is written.
 * 
 * Andrew Curry
 */
package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;

public class TextFileDAO implements BankDAO {
	
	// instance variables
	private String filename;
	private BufferedReader reader;
	private BufferedWriter writer;
	
	// constructor(s)
	public TextFileDAO(String filename) throws BankDAOException {
		this.filename = filename;
		
		// make sure the filename is valid
		if (openFileReader()) {
			closeFileReader();
		}
		else {
			 throw (new BankDAOException("Resource name not valid"));
		}
	}
	
	// helper / util methods for file IO
	
	/**
	 * Opens the file with a BufferedReader, handles the try/catch
	 * @return true if the file could be opened
	 */
	private boolean openFileReader() {
		
		boolean opened = false;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			opened = true;
		}
		catch (FileNotFoundException e) {
			
		}
		
		return opened;
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

}
