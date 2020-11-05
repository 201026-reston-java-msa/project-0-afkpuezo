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

import java.io.LineNumberInputStream;
import java.util.List;

import javax.xml.stream.events.StartDocument;

import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.UserProfile.UserProfileType;

import BankIO.BankIO;
import bankSystem.Request.RequestType;
import dao.BankDAO;

public class BankSystem {

	// class variables
	private static final String START_MESSAGE = "Welcome to the bank!";
	//private static final String NO_USER_PROMPT = "(1) Log in\n(2) Register new user\n(3) Quit";
	//private static final String CUSTOMER_PROMPT = "";
	
	private static final RequestType[] NO_USER_CHOICES = 
			{RequestType.LOG_IN, RequestType.REGISTER_USER, RequestType.QUIT};
	
	private static final RequestType[] CUSTOMER_CHOICES_NO_ACCOUNTS = 
			{RequestType.APPLY_OPEN_ACCOUNT, RequestType.LOG_OUT, RequestType.QUIT};
	private static final RequestType[] CUSTOMER_CHOICES_HAS_ACCOUNT =
			{RequestType.VIEW_ACCOUNTS, RequestType.DEPOSIT, RequestType.WITHDRAW, RequestType.TRANSFER, 
			RequestType.VIEW_TRANSACTIONS, RequestType.ADD_ACCOUNT_OWNER, RequestType.REMOVE_ACCOUNT_OWNER,
			RequestType.APPLY_OPEN_ACCOUNT, RequestType.LOG_OUT, RequestType.QUIT};
	
	private static final RequestType[] EMPLOYEE_CHOICES = 
			{RequestType.VIEW_ACCOUNTS, RequestType.VIEW_USERS, RequestType.APPROVE_OPEN_ACCOUNT, 
			RequestType.DENY_OPEN_ACCOUNT, RequestType.LOG_OUT, RequestType.QUIT};
	
	private static final RequestType[] ADMIN_CHOICES =
			{RequestType.VIEW_ACCOUNTS, RequestType.VIEW_USERS, RequestType.APPROVE_OPEN_ACCOUNT,
			RequestType.DENY_OPEN_ACCOUNT, RequestType.WITHDRAW, RequestType.DEPOSIT, RequestType.TRANSFER,
			RequestType.CLOSE_ACCOUNT, RequestType.LOG_OUT, RequestType.QUIT};
	
	// instance variables (fields)
	private BankIO io;
	private BankDAO dao;
	
	private UserProfile currentUser; // who is logged in?
	private boolean running; // controls interaction loop
	
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
		running = false;
	}
	
	// 'operation' methods
	
	/**
	 * Called by the driver to start operation of the system.
	 */
	public void start() {
		
		io.displayText(START_MESSAGE, true);
		running = true;
		interactionLoop();
	}
	
	/**
	 * Prompts the user for input, and handles the resulting request.
	 */
	private void interactionLoop() {
		
		//boolean running = true;
		String outputText = "";
		RequestType[] permittedRequestTypes; // = new RequestType[0]; // should get replaced in loop
		Request currentRequest;
		
		while (running) {
			// first, determine what to prompt the user with
			if (currentUser.getType() == UserProfileType.NONE) { // if no one is logged in
				permittedRequestTypes = NO_USER_CHOICES;
			}
			else if (currentUser.getType() == UserProfileType.CUSTOMER) {
				if( currentUser.getOwnedAccounts().isEmpty()) {
					permittedRequestTypes = CUSTOMER_CHOICES_NO_ACCOUNTS;
				}
				else {
					permittedRequestTypes = CUSTOMER_CHOICES_HAS_ACCOUNT;
				}
			}
			else if (currentUser.getType() == UserProfileType.EMPLOYEE) {
				permittedRequestTypes = EMPLOYEE_CHOICES;
			}
			else { //if (currentUser.getType() == UserProfileType.ADMIN) // assume admin
				permittedRequestTypes = ADMIN_CHOICES;
			}
			
			currentRequest = io.prompt(permittedRequestTypes);
			
			// now handle the request
			try {
				switch(currentRequest.getType()) {
				
				case REGISTER_USER:
					handleRegisterUser(currentRequest);
					break;
				case LOG_IN:
					handleLogIn(currentRequest);
					break;
				case LOG_OUT:
					handleLogOut(currentRequest);
					break;
				case QUIT:
					handleQuit(currentRequest);
					break;
				case APPLY_OPEN_ACCOUNT:
					handleApplyToOpenAccount(currentRequest);
					break;
				case APPROVE_OPEN_ACCOUNT:
					handleApproveOpenAccount(currentRequest);
					break;
				case DENY_OPEN_ACCOUNT:
					handleDenyOpenAccount(currentRequest);
					break;
				case CLOSE_ACCOUNT:
					handleCloseAccount(currentRequest);
					break;
				case ADD_ACCOUNT_OWNER:
					handleAddAccountOwner(currentRequest);
					break;
				case REMOVE_ACCOUNT_OWNER:
					handleRemoveAccountOwner(currentRequest);
					break;
				case DEPOSIT:
					handleDeposit(currentRequest);
					break;
				case WITHDRAW:
					handleWithdraw(currentRequest);
					break;
				case TRANSFER:
					handleTransfer(currentRequest);
					break;
				case VIEW_ACCOUNTS:
					handleViewAccounts(currentRequest);
					break;
				case VIEW_USERS:
					handleViewUsers(currentRequest);
					break;
				case VIEW_TRANSACTIONS:
					handleViewTransactions(currentRequest);
					break;
				}				
			}
			catch (ImpossibleActionException e) {
				io.displayText(e.getMessage());
			}
			
		} // end while (running) loop
	} // end interactionLoop() method

	
	// methods for handling specific request types --------------
	

	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleRegisterUser(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleLogIn(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleLogOut(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleQuit(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleApplyToOpenAccount(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleApproveOpenAccount(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleDenyOpenAccount(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleCloseAccount(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleAddAccountOwner(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleRemoveAccountOwner(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleDeposit(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleWithdraw(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleTransfer(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleViewAccounts(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleViewUsers(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleViewTransactions(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}

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
