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
import org.junit.validator.PublicClassValidator;

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
	
	// utility methods ----------
	
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
	 * Logs into the given user. Used to avoid repitition.
	 * @param user
	 * @param pass
	 */
	public void logInHelp(String user, String pass) {
		List<String> params = new ArrayList<String>();
		params.add(user);
		params.add(pass);
		Request request = new Request(
				RequestType.LOG_IN, 
				params);
		mio.setNextRequest(request);
		bank.testLoop();
	}
	
	// tests -------------------------
	
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
		Request request = new Request(
				RequestType.REGISTER_USER, 
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(output.get(1), BankSystem.USER_REGISTERED_MESSAGED);
		
		UserProfile up = tdao.readUserProfile("newuser");
		assertEquals(UserProfileType.CUSTOMER, up.getType());
		assertEquals("newpass", up.getPassword());
	}
	
	@Test
	public void testRegisterUserWithUsernameTaken() throws BankDAOException{
		
		List<String> params = new ArrayList<String>();
		params.add("user");
		params.add("newpass");
		Request request = new Request(
				RequestType.REGISTER_USER, 
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(output.get(1), BankSystem.USERNAME_IN_USE_MESSAGE);
		
		List<UserProfile> users = tdao.readAllUserProfiles();
		// any new users? could change if i change the test file
		assertEquals(3, users.size()); 
	}
	
	@Test
	public void testLogIn(){
		
		logInHelp("user", "pass");
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(output.get(1), BankSystem.USER_LOGGED_IN_PREFIX + "user");
	}
	
	@Test
	public void testLogInBadPass(){
		
		logInHelp("user", "badpass");
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(output.get(1), BankSystem.LOGIN_INVALID_PASSWORD_MESSAGE);
	}
	
	@Test
	public void testLogInUserNotFound(){
		
		logInHelp("baduser", "badpass");
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(
				output.get(1), 
				BankSystem.LOGIN_USER_NOT_FOUND_PREFIX + "baduser");
	}
	
	/**
	 * Tests the generic catch-all permissions check before the big switch case
	 */
	@Test
	public void testActOutsidePermissions() {
		
		Request request = new Request(
				RequestType.LOG_OUT, // can't log out if you aren't logged in
				new ArrayList<>());
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(
				output.get(1), 
				BankSystem.GENERIC_NO_PERMISSION_MESSAGE);
	}
	
	@Test
	public void testLogOut() {
		
		logInHelp("user", "pass");
		
		Request request = new Request(
				RequestType.LOG_OUT,
				new ArrayList<>());
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(4, output.size());
		assertEquals(
				output.get(3), 
				BankSystem.LOGOUT_MESSAGE);
		
		mio.setNextRequest(request);
		bank.testLoop();
		
		output = mio.getCachedOutput();
		assertEquals(6, output.size());
		assertEquals(
				output.get(5), 
				BankSystem.GENERIC_NO_PERMISSION_MESSAGE);
	}
	
	@Test
	public void testQuit() {
		
		Request request = new Request(
				RequestType.QUIT,
				new ArrayList<>());
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(2, output.size());
		assertEquals(
				output.get(1), 
				BankSystem.QUIT_MESSAGE);
	}
	
	@Test
	public void handleApply() throws BankDAOException{
		
		logInHelp("user", "pass");
		
		Request request = new Request(RequestType.APPLY_OPEN_ACCOUNT);
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(4, output.size());
		assertEquals(
				BankSystem.APPLY_OPEN_ACCOUNT_MESSAGE,
				output.get(3));
		
		UserProfile up = tdao.readUserProfile("user");
		assertEquals(2, up.getOwnedAccounts().size());
		
		int accID = up.getOwnedAccounts().get(1); // new one should be last
		BankAccount ba = tdao.readBankAccount(accID);
		assertTrue(ba.getOwners().contains(up.getId()));
		assertEquals(BankAccount.BankAccountStatus.PENDING, ba.getStatus());
	}
	
	@Test
	public void handleApproveAccount() throws BankDAOException{
		
		logInHelp("user", "pass");
		
		Request request = new Request(RequestType.APPLY_OPEN_ACCOUNT);
		mio.setNextRequest(request);
		bank.testLoop();
		
		request = new Request(
				RequestType.LOG_OUT,
				new ArrayList<>());
		mio.setNextRequest(request);
		bank.testLoop();
		
		UserProfile up = tdao.readUserProfile("user");
		int accID = up.getOwnedAccounts().get(1); // new one should be last
		
		logInHelp("admin", "admin");
		List<String> params = new ArrayList<String>();
		params.add("" + accID);
		request = new Request(
				RequestType.APPROVE_OPEN_ACCOUNT,
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		// is the account actually approved?
		List<Object> output = mio.getCachedOutput();
		assertEquals(
				BankSystem.ACCOUNT_APPROVED_MESSAGE, 
				output.get(output.size() - 1));
		BankAccount ba = tdao.readBankAccount(accID);
		assertEquals(BankAccountStatus.OPEN, ba.getStatus());
	}
	
	@Test
	public void handleApproveBadAccount() throws BankDAOException {
		
		logInHelp("admin", "admin");
		List<String> params = new ArrayList<String>();
		params.add("3234259");
		Request request = new Request(
				RequestType.APPROVE_OPEN_ACCOUNT,
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(
				BankSystem.BANK_ACCOUNT_DOES_NOT_EXIST_PREFIX + "3234259", 
				output.get(output.size() - 1));
		
		params = new ArrayList<String>();
		params.add("444");
		request = new Request(
				RequestType.APPROVE_OPEN_ACCOUNT,
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		output = mio.getCachedOutput();
		assertEquals(
				BankSystem.BANK_ACCOUNT_NOT_PENDING_MESSAGE, 
				output.get(output.size() - 1));
	}
	
	@Test
	public void handleDenyAccount() throws BankDAOException {
		
		logInHelp("user", "pass");
		
		Request request = new Request(RequestType.APPLY_OPEN_ACCOUNT);
		mio.setNextRequest(request);
		bank.testLoop();
		
		request = new Request(
				RequestType.LOG_OUT,
				new ArrayList<>());
		mio.setNextRequest(request);
		bank.testLoop();
		
		UserProfile up = tdao.readUserProfile("user");
		int accID = up.getOwnedAccounts().get(1); // new one should be last
		
		logInHelp("admin", "admin");
		List<String> params = new ArrayList<String>();
		params.add("" + accID);
		request = new Request(
				RequestType.DENY_OPEN_ACCOUNT,
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		// is the account actually approved?
		List<Object> output = mio.getCachedOutput();
		assertEquals(
				BankSystem.ACCOUNT_DENIED_MESSAGE, 
				output.get(output.size() - 1));
		BankAccount ba = tdao.readBankAccount(accID);
		assertEquals(BankAccountStatus.CLOSED, ba.getStatus());
	}
	
	@Test
	public void handleDenyBadAccount() throws BankDAOException {
		
		logInHelp("admin", "admin");
		List<String> params = new ArrayList<String>();
		params.add("3234259");
		Request request = new Request(
				RequestType.DENY_OPEN_ACCOUNT,
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		List<Object> output = mio.getCachedOutput();
		assertEquals(
				BankSystem.BANK_ACCOUNT_DOES_NOT_EXIST_PREFIX + "3234259", 
				output.get(output.size() - 1));
		
		params = new ArrayList<String>();
		params.add("444");
		request = new Request(
				RequestType.DENY_OPEN_ACCOUNT,
				params);
		mio.setNextRequest(request);
		bank.testLoop();
		
		output = mio.getCachedOutput();
		assertEquals(
				BankSystem.BANK_ACCOUNT_NOT_PENDING_MESSAGE, 
				output.get(output.size() - 1));
	}
}