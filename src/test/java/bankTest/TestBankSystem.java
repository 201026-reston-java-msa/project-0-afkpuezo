/**
 * This file contains Junit tests for the BankSystem class.
 * It uses the MockIO and TextFileDAO classes to accomplish this.
 */
package bankTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.UserProfile.UserProfileType;

import BankIO.MockIO;
import bankSystem.BankSystem;
import bankSystem.Request;
import bankSystem.Request.RequestType;

import com.revature.bankDataObjects.BankAccount.BankAccountStatus;
import com.revature.bankDataObjects.BankAccount.BankAccountType;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.TransactionRecord.TransactionType;

import dao.BankDAO;
import dao.BankDAOException;
import dao.TextFileDAO;

public class TestBankSystem {

	// static variables for use in each test
	private static BankSystem bank;
	private static TextFileDAO tdao;
	private static MockIO mio;
	
	static private final String testFilename = "testfile.bdf"; // 'bank data file'
	static private final String[] FILELINES = {
			"PRF 101 user pass CST 444", "ACC 444 OPN SNG 78923 101", "PRF 103 user2 pass CST 317 515",
			"ACC 317 OPN SNG 7892312 103", "PRF 999 admin admin ADM", "ACC 515 OPN SNG 111111 103",
			"TRR 123 3:00 FDP 101 -1 444 87654"
	};
	
	/**
	 * Sets up a text file for use in tests.
	 * @return true if the file could be set up, false otherwise
	 */
	private boolean prepareTextFile() {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(testFilename));
			
			for (String line : FILELINES){
				writer.write(line);
				writer.write("\n");
			}
			
			writer.close();
		}
		catch (IOException e) {
			System.out.println("ALERT: prepareTextFile could not complete writing the text file.");
			return false;
		}
		
		return true; // only reached if successful
	}
	
	/**
	 * Sets up the text DAO for use
	 * @return true if successful
	 */
	private boolean prepareTextFileDAO() {
		
		try {
			tdao = new TextFileDAO(testFilename);
		}
		catch (BankDAOException e){
			System.out.println("ALERT: prepareTextFileDAO could not create a TextFileDAO");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Prepares the bank system into a default state
	 */
	@Before
	public void setup() {
		
		prepareTextFile();
		prepareTextFileDAO();
		mio = new MockIO();
		bank = new BankSystem(mio, tdao);
	}
	
	/**
	 * For my own sanity, makes sure that the @Before method setup works
	 */
	@Test
	public void testSetup() {
		
		assertNotEquals(null, bank);
		assertNotEquals(null, tdao);
		assertNotEquals(null, mio);
		assertEquals(testFilename, tdao.getResourceName());
	}
	
	@Test
	public void testRegisterUser() throws BankDAOException{
		
		List<String> params = new ArrayList<String>();
		params.add("newuser");
		params.add("newpass");
		Request register = new Request(
				RequestType.REGISTER_USER, 
				params);
		mio.setNextRequest(register);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(output.get(1), BankSystem.USER_REGISTERED_MESSAGED);
		
		UserProfile up = tdao.readUserProfile("newuser");
		assertEquals(UserProfileType.CUSTOMER, up.getType());
		assertEquals("newpass", up.getPassword());
	}
}
