/**
 * This class contains Junit tests for the PostgresDAO class.
 * NOTE: Several tests could break if details of the resetDatabase method are changed.
 * 
 * @author Andrew Curry
 */
package bankTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.validator.PublicClassValidator;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.TransactionRecord.TransactionType;
import com.revature.bankDataObjects.BankAccount.BankAccountStatus;
import com.revature.bankDataObjects.BankAccount.BankAccountType;
import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.UserProfile.UserProfileType;

import dao.BankDAOException;
import dao.DatabaseUtil;
import dao.PostgresDAO;

public class TestPostgresDAO {
	
	// class variables / constants
	//private static final String TEST_ADDRESS = "jdbc:postgresql://localhost:5432/";
	//private static final String TEST_USERNAME = "postgres";
	//private static final String TEST_PASSWORD = "password";

	// instance variables ----------------------------------------------------------
	private PostgresDAO pdao;
	
	// junit util methods ----------------------------------------------------------
	@Before
	public void setupPDAO() {
		
		pdao = new PostgresDAO();
		DatabaseUtil.resetDatabase();
	}
	
	// test methods ----------------------------------------------------------------
	
	/*
	@Test
	public void testResourceName() {
		
		assertEquals(DatabaseUtil.TEST_ADDRESS, pdao.getResourceName());
	}
	
	@Test
	public void testGetConnection() {
		
		Connection conn = DatabaseUtil.getConnection();
		assertNotNull(conn);
	}
	*/
	
	@Test
	public void testReadBankAccount() throws BankDAOException{
		
		BankAccount ba = pdao.readBankAccount(1);
		assertEquals(1,  ba.getId());
		assertEquals(BankAccountStatus.OPEN, ba.getStatus());
		assertEquals(BankAccountType.SINGLE, ba.getType());
		assertEquals(123456, ba.getFunds());
		assertEquals(1, ba.getOwners().size());
		assertTrue(3 == ba.getOwners().get(0));
		
		ba = pdao.readBankAccount(1001); // not found
		assertEquals(1001, ba.getId());
		assertEquals(BankAccountType.NONE, ba.getType());

		// test readAll
		
		List<BankAccount> accounts = pdao.readAllBankAccounts();
		assertEquals(2, accounts.size());

		ba = accounts.get(0);
		assertEquals(1,  ba.getId());
		assertEquals(BankAccountStatus.OPEN, ba.getStatus());
		assertEquals(BankAccountType.SINGLE, ba.getType());
		assertEquals(123456, ba.getFunds());
		assertEquals(1, ba.getOwners().size());
		assertTrue(3 == ba.getOwners().get(0));
		
		ba = accounts.get(1);
		assertEquals(2,  ba.getId());
		assertEquals(BankAccountStatus.CLOSED, ba.getStatus());
		assertEquals(BankAccountType.SINGLE, ba.getType());
		assertEquals(0, ba.getFunds());
		assertEquals(1, ba.getOwners().size());
		assertTrue(4 == ba.getOwners().get(0));
	}
	
	@Test
	public void testReadUserProfile() throws BankDAOException{
		
		UserProfile up = pdao.readUserProfile(1); // by ID
		assertEquals(1, up.getId());
		assertEquals("admin", up.getUsername());
		assertEquals("admin", up.getPassword());
		assertEquals(UserProfileType.ADMIN, up.getType());
		assertTrue(up.getOwnedAccounts().isEmpty());
		
		up = pdao.readUserProfile("cust"); // by username
		assertEquals(3, up.getId());
		assertEquals("cust", up.getUsername());
		assertEquals("pass", up.getPassword());
		assertEquals(UserProfileType.CUSTOMER, up.getType());
		assertEquals(1, up.getOwnedAccounts().size());
		assertTrue(1 == up.getOwnedAccounts().get(0));
		
		up = pdao.readUserProfile(1001); // not found
		assertEquals(1001, up.getId());
		assertEquals(UserProfileType.NONE, up.getType());
		
		// test read all
		List<UserProfile> users = pdao.readAllUserProfiles();
		assertEquals(4, users.size());
		
		up = users.get(0); //
		assertEquals(1, up.getId());
		assertEquals("admin", up.getUsername());
		assertEquals("admin", up.getPassword());
		assertEquals(UserProfileType.ADMIN, up.getType());
		assertTrue(up.getOwnedAccounts().isEmpty());
		
		up = users.get(2);
		assertEquals(3, up.getId());
		assertEquals("cust", up.getUsername());
		assertEquals("pass", up.getPassword());
		assertEquals(UserProfileType.CUSTOMER, up.getType());
		assertEquals(1, up.getOwnedAccounts().size());
		assertTrue(1 == up.getOwnedAccounts().get(0));
	}
	
	@Test
	public void testReadTransactionRecord() throws BankDAOException{
		
		TransactionRecord tr = pdao.readTransactionRecord(1);
		assertEquals(1, tr.getId());
		assertEquals(TransactionType.FUNDS_DEPOSITED, tr.getType());
		assertEquals(3, tr.getActingUser());
		assertEquals(1, tr.getDestinationAccount());
		assertEquals(123456, tr.getMoneyAmount());
		
		tr = pdao.readTransactionRecord(1001); // not found
		assertEquals(1001, tr.getId());
		assertEquals(TransactionType.NONE, tr.getType());
		
		List<TransactionRecord> transactions = pdao.readAllTransactionRecords();
		assertEquals(1, transactions.size());
		tr = transactions.get(0);
		assertEquals(1, tr.getId());
		assertEquals(TransactionType.FUNDS_DEPOSITED, tr.getType());
		assertEquals(3, tr.getActingUser());
		assertEquals(1, tr.getDestinationAccount());
		assertEquals(123456, tr.getMoneyAmount());
		
		transactions = pdao.readTransactionRecordByActingUserId(3);
		assertEquals(1, transactions.size());
		tr = transactions.get(0);
		assertEquals(1, tr.getId());
		assertEquals(TransactionType.FUNDS_DEPOSITED, tr.getType());
		assertEquals(3, tr.getActingUser());
		assertEquals(1, tr.getDestinationAccount());
		assertEquals(123456, tr.getMoneyAmount());
		
		transactions = pdao.readTransactionRecordByActingUserId(1001); // should find nothing
		assertEquals(0, transactions.size());
		
		transactions = pdao.readTransactionRecordByAccountId(1);
		assertEquals(1, transactions.size());
		tr = transactions.get(0);
		assertEquals(1, tr.getId());
		assertEquals(TransactionType.FUNDS_DEPOSITED, tr.getType());
		assertEquals(3, tr.getActingUser());
		assertEquals(1, tr.getDestinationAccount());
		assertEquals(123456, tr.getMoneyAmount());
	}
	
	@Test
	public void testWriteUserProfile() throws BankDAOException{
		
		UserProfile up;
		
		up = pdao.readUserProfile(111); // should not be found yet
		assertEquals(UserProfileType.NONE, up.getType());
		
		up = new UserProfile(111);
		up.setUsername("new_cust");
		up.setPassword("new_cust");
		up.setType(UserProfileType.CUSTOMER);
		pdao.write(up);
		
		// assume reading works
		up = pdao.readUserProfile(111);
		assertEquals(111, up.getId());
		assertEquals("new_cust", up.getUsername());
		assertEquals("new_cust", up.getPassword());
		assertEquals(UserProfileType.CUSTOMER, up.getType());
	}
}
