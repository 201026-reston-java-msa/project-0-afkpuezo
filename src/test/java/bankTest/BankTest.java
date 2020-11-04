/**
 * This file contains Junit tests 
 * 
 * Andrew Curry, Project 0
 */
package bankTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.BankAccount.BankAccountStatus;
import com.revature.bankDataObjects.BankAccount.BankAccountType;
import com.revature.bankDataObjects.TransactionRecord;

import dao.BankDAO;
import dao.BankDAOException;
import dao.TextFileDAO;

public class BankTest {
	
	/**
	 * -----------------------------------------------------------------------
	 * TextFileDAO
	 * -----------------------------------------------------------------------
	 */
	
	// some helper methods/variables
	
	static private final String testFilename = "testfile.bdf"; // 'bank data file'
	static private TextFileDAO tdao;
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
	
	
	@Test
	public void testSearchFilePositive() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			String result;
			
			result = tdao.searchFile("ACC 444");
			//System.out.println(result);
			hasPassed = hasPassed && result.equals("ACC 444 OPN SNG 78923 101");
			
			result = tdao.searchFile("PRF 103");
			//System.out.println(result);
			hasPassed = hasPassed && result.equals("PRF 103 user2 pass CST 317 515");
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
		
	}
	
	@Test
	public void testSearchFileNegative() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			String result;
			
			result = tdao.searchFile("ACC 000");
			hasPassed = hasPassed && result.equals("");
			
			result = tdao.searchFile("nonsense");
			hasPassed = hasPassed && result.equals("");
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testSearchFileMultiple() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			List<String> results = tdao.searchFileMultiple("ACC");
			List<String> allLines = List.of(FILELINES);
			List<String> expected = new ArrayList<String>();
			for (String s : allLines) {
				if (s.startsWith("ACC")) {
					expected.add(s);
				}
			}
			
			hasPassed = results.equals(expected);
			
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testSearchFileMultipleNegative() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			List<String> results = tdao.searchFileMultiple("foobar");
			
			hasPassed = results.isEmpty();
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testSearchFileGetAll() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			List<String> results = tdao.searchFileMultiple("");
			List<String> allLines = List.of(FILELINES);
			
			hasPassed = results.equals(allLines);
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testReadBankAccount() throws BankDAOException {
		
		prepareTextFile();
		prepareTextFileDAO();
		
		// this is the entry we're looking for "ACC 444 OPN SNG 78923 101"
		// this was clumsy, should have tested fields directly
		BankAccount expected = new BankAccount(); 
		expected.setId(444);
		expected.setStatus(BankAccountStatus.OPEN);
		expected.setType(BankAccountType.SINGLE);
		expected.setFunds(78923);
		List<Integer> owners = new ArrayList<>();
		owners.add(101);
		expected.setOwners(owners);
		
		BankAccount actual = tdao.readBankAccount(444);
		
		boolean hasPassed = true;
		hasPassed = hasPassed && actual.getId() == expected.getId();
		hasPassed = hasPassed && actual.getStatus() == expected.getStatus();
		hasPassed = hasPassed && actual.getType() == expected.getType();
		hasPassed = hasPassed && actual.getFunds() == expected.getFunds();
		hasPassed = hasPassed && actual.getOwners().equals(expected.getOwners());
		
		assertTrue(hasPassed);
		
	}
	
	@Test
	public void testReadBankAccountNotFound() throws BankDAOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		
		BankAccount actual = tdao.readBankAccount(534843943);
		
		assertEquals(-1, actual.getId());
	}
	
	/**
	 * Assumes each bank account is correct
	 * @throws BankDAOException
	 */
	@Test
	public void testReadAllBankAccounts() throws BankDAOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		
		List<BankAccount> found = tdao.readAllBankAccounts();
		
		assertEquals(3, found.size()); // could change if I update the test file
	}
	
	@Test
	public void testReadUserProfile() throws BankDAOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		
		// heres the entry: "PRF 101 user pass CST 444"
		UserProfile actual = tdao.readUserProfile(101);
		
		// try multiple asserts - I have a feeling this is how Junit is meant to work
		assertEquals(101, actual.getId());
		assertEquals("user", actual.getUsername());
		assertEquals("pass", actual.getPassword());
		assertEquals(UserProfile.UserProfileType.CUSTOMER, actual.getType());
		List<Integer> ownedAccounts = new ArrayList<>();
		ownedAccounts.add(444);
		assertEquals(ownedAccounts, actual.getOwnedAccounts());
		
	}
	
	@Test
	public void testReadUserProfileNotFound() throws BankDAOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		
		UserProfile actual = tdao.readUserProfile(534843943);
		
		assertEquals(-1, actual.getId());
	}
	
	/**
	 * Assumes each user profile is correct
	 * @throws BankDAOException
	 */
	@Test
	public void testReadAllUserProfiles() throws BankDAOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		
		List<UserProfile> found = tdao.readAllUserProfiles();
		
		assertEquals(3, found.size()); // could change if I update the test file
	}
	
	@Test
	public void testReadTransactionHistory() throws BankDAOException {
		
		prepareTextFile();
		prepareTextFileDAO();
		
		TransactionRecord actual = tdao.readTransactionRecord(123);
		
		// here's the entry: "TRR 123 3:00 FDP 101 -1 444 87654"
		assertEquals(123,  actual.getId());
		assertEquals("3:00", actual.getTime());
		assertEquals(TransactionRecord.TransactionType.FUNDS_DEPOSITED, actual.getType());
		assertEquals(101, actual.getActingUser());
		assertEquals(-1, actual.getSourceAccount());
		assertEquals(444, actual.getDestinationAccount());
		assertEquals(87654, actual.getMoneyAmount());
	}
	
	@Test
	public void testReadTransactionRecordNotFound() throws BankDAOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		
		TransactionRecord actual = tdao.readTransactionRecord(534843943);
		
		assertEquals(-1, actual.getId());
	}
	
	/**
	 * Assumes each TransactionRecord is correct
	 * @throws BankDAOException
	 */
	@Test
	public void testReadAllTransactionRecords() throws BankDAOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		
		List<TransactionRecord> found = tdao.readAllTransactionRecords();
		
		assertEquals(1, found.size()); // could change if I update the test file
	}
}
