/**
 * This class controls the operation of the banking system. It gets user Requests from the IO, and resolves
 * them by interacting with the DAO.
 * 
 * There is no corresponding interface because the other parts of the system do not need to know anything
 * about the BankSystem (currently, anyway).
 * 
 * @author Andrew Curry
 */
package bankSystem;

import javax.xml.stream.events.StartDocument;

import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.UserProfile.UserProfileType;

import BankIO.BankIO;
import dao.BankDAO;

public class BankSystem {

	// class variables
	private static final String START_MESSAGE = "Welcome to the bank!";
	private static final String NO_USER_PROMPT = "(1) Log in\n(2) Register new user\n(3) Quit";
	private static final String CUSTOMER_PROMPT = "";
	
	// instance variables (fields)
	private BankIO io;
	private BankDAO dao;
	
	private UserProfile currentUser; // who is logged in?
	
	// constructor(s)
	
	/**
	 * Note: the DAO should already be hooked up to the target data store.
	 * @param io
	 * @param dao
	 */
	public BankSystem(BankIO io, BankDAO dao) {
		
		this.io = io;
		this.dao = dao;
		
		currentUser = getEmptyUser();
	}
	
	// 'operation' methods
	
	/**
	 * Called by the driver to start operation of the system.
	 */
	public void start() {
		
		io.displayText(START_MESSAGE, true);
		interactionLoop();
	}
	
	/**
	 * Prompts the user for input, and handles the resulting request.
	 */
	private void interactionLoop() {
		
		boolean running = true;
		String outputText = "";
		Request currentRequest;
		
		while (running) {
			// first, determine what to prompt the user with
			if (currentUser.getType() == UserProfileType.NONE) { // if no one is logged in
				outputText = NO_USER_PROMPT;
			}
			else if (currentUser.getType() == UserProfileType.CUSTOMER) {
				
			}
			else if (currentUser.getType() == UserProfileType.EMPLOYEE) {
				
			}
			else if (currentUser.getType() == UserProfileType.ADMIN) {
				
			}
			
			currentRequest = io.prompt(outputText);
		} // end while (running) loop
	} // end interactionLoop() method

	// util methods

	/**
	 * Creates an 'empty' UserProfile object, representing that no one is logged in.
	 * @return
	 */
	private static UserProfile getEmptyUser() {
		
		UserProfile empty = new UserProfile(-1);
		empty.setType(UserProfileType.NONE);
		return empty;
	}
}
