/**
 * This class contains Junit tests for the PostgresDAO class.
 * 
 * @author Andrew Curry
 */
package bankTest;

import static org.junit.Assert.assertEquals;

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
	public void testResetDatabase() {
		DatabaseUtil.resetDatabase();
	}
}
