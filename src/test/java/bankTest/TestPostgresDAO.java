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
	
	@Test
	public void testResourceName() {
		
		assertEquals(DatabaseUtil.TEST_ADDRESS, pdao.getResourceName());
	}
	
	@Test
	public void testGetConnection() {
		
		Connection conn = DatabaseUtil.getConnection();
		assertNotNull(conn);
	}
	
	@Test
	public void testReadBankAccount() throws BankDAOException{
		
		BankAccount ba = pdao.readBankAccount(1);
		assertEquals(1,  ba.getId());
		assertEquals(BankAccountStatus.OPEN, ba.getStatus());
		assertEquals(BankAccountType.SINGLE, ba.getType());
		assertEquals(123456, ba.getFunds());
		assertEquals(1, ba.getOwners().size());
		assertTrue(3 == ba.getOwners().get(0));
	}
	
	@Test
	public void testReadAllBankAccounts() throws BankDAOException{
		
		List<BankAccount> accounts = pdao.readAllBankAccounts();
		assertEquals(2, accounts.size());

		BankAccount ba = accounts.get(0);
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
		
		UserProfile up = pdao.readUserProfile(1);
		assertEquals(1, up.getId());
		assertEquals("admin", up.getUsername());
		assertEquals("admin", up.getPassword());
		assertEquals(UserProfileType.ADMIN, up.getType());
	}
}
