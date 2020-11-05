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

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.TransactionRecord.TransactionType;
import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.BankAccount.BankAccountStatus;
import com.revature.bankDataObjects.BankAccount.BankAccountType;
import com.revature.bankDataObjects.UserProfile.UserProfileType;

import BankIO.BankIO;
import bankSystem.Request.RequestType;
import dao.BankDAO;
import dao.BankDAOException;

public class BankSystem {

	// class variables
	private static final String START_MESSAGE = "Welcome to the bank!";
	private static final String NO_USER_LOGGED_IN_MESSAGE = "LOGGED IN AS: N/A";
	private static final String USER_LOGGED_IN_PREFIX= "LOGGED IN AS: "; // should append username
	private static final String USERNAME_IN_USE_MESSAGE = "Unable to proceed: That username is already in use.";
	private static final String USER_DOES_NOT_EXIST_MESSAGE = "Unable to proceed: No user profile with that name exist.";
	private static final String GENERIC_DAO_ERROR_MESSAGE = "ALERT: There were issues communicating with the database. Contact your system administrator.";
	private static final String LOGIN_USER_NOT_FOUND_PREFIX = "Unable to proceed: No profile found matching username: ";
	private static final String LOGIN_INVALID_PASSWORD_MESSAGE = "Unable to proceed: Incorrect password.";
	private static final String LOGOUT_MESSAGE = "Logging out.";
	private static final String QUIT_MESSAGE = "Quitting.";
	
	private static final String APPLY_OPEN_ACCOUNT_NOT_CUSTOMER_MESSAGE 
			= "Unable to proceed: Only customers can apply to open accounts";
	private static final String APPLY_OPEN_ACCOUNT_MESSAGE
			= "Account created, pending approval.";
	private static final String APPROVE_OPEN_ACCOUNT_NO_PERMISSION_MESSAGE
			= "Unable to proceed: Only employees and administrators can approve accounts.";
	private static final String APPROVE_OPEN_ACCOUNT_MESSAGE
			= "Account created, pending approval.";
	private static final String BANK_ACCOUNT_DOES_NOT_EXIST_PREFIX
			= "Unable to proceed: No account exists with ID: ";
	private static final String BANK_ACCOUNT_NOT_PENDING_MESSAGE
			= "Unable to proceed: That account is not pending approval.";
	private static final String ACCOUNT_APPROVED_MESSAGE
			= "Account approved.";
	private static final String ACCOUNT_DENIED_MESSAGE
			= "Account denied.";
	
	private static final String CLOSE_ACCOUNT_NO_PERMISSION_MESSAGE
			= "Unable to proceed: Only an administrator can close an account.";
	private static final String CLOSE_ACCOUNT_NOT_OPEN_MESSAGE
			= "Unable to proceed: That account cannot be closed because it is not open.";
	private static final String CLOSE_ACCOUNT_PREFIX
			= "Account closed. All funds withdrawn and returned to account owner(s). Funds amount: $";
	
	// arrays of permitted request types
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
			
			// display a header with the current user
			if (currentUser.getType() == UserProfileType.NONE) {
				io.displayText(NO_USER_LOGGED_IN_MESSAGE);
			}
			else {
				io.displayText(USER_LOGGED_IN_PREFIX + currentUser.getUsername() + " ID: " + currentUser.getId());
			}
			
			//determine what to prompt the user with
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
				case CREATE_EMPLOYEE:
					handleCreateEmployee(currentRequest);
					break;
				case CREATE_ADMIN:
					handleCreateAdmin(currentRequest);
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
	 * Creates a new user profile, if the username is not a duplicate.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleRegisterUser(Request currentRequest) throws ImpossibleActionException {
		
		List<String> params = currentRequest.getParams();
		
		try {
			if (dao.isUsernameFree(params.get(0))) {
				UserProfile user = new UserProfile(dao.getHighestUserProfileID() + 1);
				user.setUsername(params.get(0));
				user.setPassword(params.get(1));
				dao.write(user);
				changeLoggedInUser(user);
				
				TransactionRecord tr = new TransactionRecord();
				tr.setType(TransactionType.USER_REGISTERED);
				tr.setActingUser(user.getId());
				saveTransactionRecord(tr);
			}
			else { // username is taken
				throw new ImpossibleActionException(USERNAME_IN_USE_MESSAGE);
			}			
		}
		catch (BankDAOException e) {
			throw new ImpossibleActionException(GENERIC_DAO_ERROR_MESSAGE);
		}
	}

	/**
	 * Logs in to the user account with the matching username, provided it exists
	 * and the password is valid.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleLogIn(Request currentRequest) throws ImpossibleActionException {
		
		List<String> params = currentRequest.getParams();
		String username = params.get(0);
		String password = params.get(1);
		
		try {
			UserProfile up = dao.readUserProfile(params.get(0));
			
			if (up.getType() == UserProfileType.NONE) { // if no matching account
				throw new ImpossibleActionException(LOGIN_USER_NOT_FOUND_PREFIX + username);
			}
			else { // account found
				if (up.getPassword().equals(password)) {
					changeLoggedInUser(up);
				}
				else { // invalid pass
					throw new ImpossibleActionException(LOGIN_INVALID_PASSWORD_MESSAGE);
				}
			}
			
			// no transaction
		}
		catch(BankDAOException e){
			throw new ImpossibleActionException(GENERIC_DAO_ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * Logs out of the current user.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleLogOut(Request currentRequest) throws ImpossibleActionException {
		
		io.displayText(LOGOUT_MESSAGE);;
		changeLoggedInUser(getEmptyUser());
		// no transaction
	}
	
	/**
	 * Triggers the end of execution.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleQuit(Request currentRequest) throws ImpossibleActionException {
		
		io.displayText(QUIT_MESSAGE);
		stopRunning();
		// no transaction
	}
	
	/**
	 * Allows a customer to apply to open a new account.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleApplyToOpenAccount(Request currentRequest) throws ImpossibleActionException {
		
		// check permissions - only a customer can apply to open an account
		if (currentUser.getType() != UserProfileType.CUSTOMER) {
			throw new ImpossibleActionException(APPLY_OPEN_ACCOUNT_NOT_CUSTOMER_MESSAGE);
		}
		
		try {
			BankAccount ba = new BankAccount(dao.getHighestBankAccountID() + 1);
			ba.setStatus(BankAccountStatus.PENDING);
			ba.setType(BankAccountType.SINGLE);
			ba.setFunds(0);
			ba.addOwner(currentUser.getId());
			dao.write(ba);
			io.displayText(APPLY_OPEN_ACCOUNT_MESSAGE);
			
			TransactionRecord tr = new TransactionRecord();
			tr.setType(TransactionType.ACCOUNT_REGISTERED);
			tr.setActingUser(currentUser.getId());
			tr.setDestinationAccount(ba.getId());
			saveTransactionRecord(tr);
		}
		catch(BankDAOException e) {
			throw new ImpossibleActionException(GENERIC_DAO_ERROR_MESSAGE);
		}
	}
	
	/**
	 * Allows an employee or admin to approve an account that is pending approval.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleApproveOpenAccount(Request currentRequest) throws ImpossibleActionException {
		
		// check permissions - only employees and admins can approve an account
		if (currentUser.getType() != UserProfileType.EMPLOYEE 
				&& currentUser.getType() != UserProfileType.ADMIN) {
			io.displayText(APPROVE_OPEN_ACCOUNT_NO_PERMISSION_MESSAGE);
		}
		
		try {
			List<String> params = currentRequest.getParams();
			int id = Integer.parseInt(params.get(0));
			
			BankAccount ba = dao.readBankAccount(id);
			
			if (ba.getType() == BankAccountType.NONE) {
				throw new ImpossibleActionException(BANK_ACCOUNT_DOES_NOT_EXIST_PREFIX + id);
			}
			if (ba.getStatus() != BankAccountStatus.PENDING) {
				throw new ImpossibleActionException(BANK_ACCOUNT_NOT_PENDING_MESSAGE);
			}
			
			ba.setStatus(BankAccountStatus.OPEN);
			dao.write(ba);
			io.displayText(ACCOUNT_APPROVED_MESSAGE);
			
			TransactionRecord tr = new TransactionRecord();
			tr.setType(TransactionType.ACCOUNT_APPROVED);
			tr.setActingUser(currentUser.getId());
			tr.setDestinationAccount(ba.getId());
			saveTransactionRecord(tr);
		}
		catch(BankDAOException e) {
			throw new ImpossibleActionException(GENERIC_DAO_ERROR_MESSAGE);
		}
	}
	
	/**
	 * Allows an employee or admin to deny an account that is pending approval.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleDenyOpenAccount(Request currentRequest) throws ImpossibleActionException {
		
		// check permissions - only employees and admins can approve an account
		if (currentUser.getType() != UserProfileType.EMPLOYEE 
				&& currentUser.getType() != UserProfileType.ADMIN) {
			io.displayText(APPROVE_OPEN_ACCOUNT_NO_PERMISSION_MESSAGE);
		}
		
		try {
			List<String> params = currentRequest.getParams();
			int id = Integer.parseInt(params.get(0));
			
			BankAccount ba = dao.readBankAccount(id);
			
			if (ba.getType() == BankAccountType.NONE) {
				throw new ImpossibleActionException(BANK_ACCOUNT_DOES_NOT_EXIST_PREFIX + id);
			}
			if (ba.getStatus() != BankAccountStatus.PENDING) {
				throw new ImpossibleActionException(BANK_ACCOUNT_NOT_PENDING_MESSAGE);
			}
			
			ba.setStatus(BankAccountStatus.CLOSED);
			dao.write(ba);
			io.displayText(ACCOUNT_DENIED_MESSAGE);
			
			TransactionRecord tr = new TransactionRecord();
			tr.setType(TransactionType.ACCOUNT_CLOSED);
			tr.setActingUser(currentUser.getId());
			tr.setDestinationAccount(ba.getId());
			saveTransactionRecord(tr);
		}
		catch(BankDAOException e) {
			throw new ImpossibleActionException(GENERIC_DAO_ERROR_MESSAGE);
		}
	}
	
	/**
	 * Allows an admin to close an account.
	 * NOTE: only works on open accounts.
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleCloseAccount(Request currentRequest) throws ImpossibleActionException {
		
		if (currentUser.getType() != UserProfileType.ADMIN) {
			io.displayText(CLOSE_ACCOUNT_NO_PERMISSION_MESSAGE);
		}
		
		try {
			List<String> params = currentRequest.getParams();
			int id = Integer.parseInt(params.get(0));
			
			BankAccount ba = dao.readBankAccount(id);
			
			if (ba.getType() == BankAccountType.NONE) {
				throw new ImpossibleActionException(BANK_ACCOUNT_DOES_NOT_EXIST_PREFIX + id);
			}
			if (ba.getStatus() != BankAccountStatus.OPEN) {
				throw new ImpossibleActionException(CLOSE_ACCOUNT_NOT_OPEN_MESSAGE);
			}
			
			int funds = ba.getFunds();
			ba.setFunds(0);
			ba.setStatus(BankAccountStatus.CLOSED);
			dao.write(ba);
			io.displayText(CLOSE_ACCOUNT_PREFIX + funds);
			
			TransactionRecord tr = new TransactionRecord();
			tr.setType(TransactionType.ACCOUNT_CLOSED);
			tr.setActingUser(currentUser.getId());
			tr.setDestinationAccount(ba.getId());
			saveTransactionRecord(tr);
		}
		catch(BankDAOException e) {
			throw new ImpossibleActionException(GENERIC_DAO_ERROR_MESSAGE);
		}
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
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleCreateEmployee(Request currentRequest) throws ImpossibleActionException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO doc
	 * @param currentRequest
	 * @throws ImpossibleActionException
	 */
	private void handleCreateAdmin(Request currentRequest) throws ImpossibleActionException {
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
	
	/**
	 * Changes the current user to the given user.
	 * @param user
	 */
	private void changeLoggedInUser(UserProfile user) {

		currentUser = user;
	}
	
	/**
	 * Sets the running variable to false, ending the loop.
	 */
	private void stopRunning() {
		
		running = false;
	}
	
	/**
	 * Writes the given TR to the database.
	 * This method will take care of finding the ID and creating the timestamp (eventually)
	 * @param tr
	 */
	private void saveTransactionRecord(TransactionRecord tr) throws BankDAOException{
		
		tr.setId(dao.getHighestTransactionRecordID() + 1);
		tr.setTime("PLACEHOLDER"); // TODO fix this
		dao.write(tr);
	}
	
	
}
