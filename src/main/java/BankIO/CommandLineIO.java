/**
 * An implementation of the BankIO interface that uses the Command Line.
 * 
 * @author Andrew Curry
 */
package BankIO;

import java.security.Identity;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.stream.events.EndDocument;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;
import com.revature.bankDataObjects.UserProfile.UserProfileType;

import bankSystem.Request;
import bankSystem.Request.RequestType;

public class CommandLineIO implements BankIO {

	// static / class variables / constants
	private static final String FRAME_LINE = "-----------------------------------";
	private static final String DISPLAY_PROFILES_HEADER = "Showing user profiles...";
	private static final String DISPLAY_ACCOUNTS_HEADER = "Showing accounts...";
	private static final String DISPLAY_TRANSACTIONS_HEADER = "Showing transactions...";
	private static final String CHOICES_HEADER 
			= "Type the number matching one of the following choices:";
	private static final String CHOICES_PROMPT 
	= "Enter your choice here:";
	private static final String DISPLAY_FIELD_EMPTY = "---";
	
	private static final String PARSE_INT_INVALID_INPUT_MESSAGE
			= "Invalid input. Please enter a number.";
	private static final String PARSE_INT_CHOICE_OUT_OF_BOUNDS_MESSAGE
			= "Invalid input. Please choose one of the available options.";
	
	private static final String PARSE_STRING_WHITESPACE_INVALID
			= "Invalid input. No whitespace characters are allowed"; 
	private static final String PARSE_STRING_EMPTY_STRING_INVALID
	= "Invalid input - no input detected.";
	
	private static final String BAD_MONEY_FORMAT_ONLY_TWO_DECIMAL_PLACES_MESSAGE
			= "Input has more than 2 characters after the decimal point.";
	private static final String BAD_MONEY_FORMAT_ONLY_ONE_DOT_MESSAGE
			= "Input has a second decimal point.";
	private static final String BAD_MONEY_FORMAT_DOLLAR_SIGN_WRONG_MESSAGE
			= "'$' character is only valid as the first character.";
	private static final String BAD_MONEY_FORMAT_GENERIC_PREFIX
			= "Input contains an invalid character: ";
	
	private static final String USERNAME_PROMPT = "Enter username: ";
	private static final String PASSWORD_PROMPT = "Enter password: ";
	private static final String USER_ID_PROMPT = "Enter user ID: ";
	private static final String ACCOUNT_ID_PROMPT = "Enter account ID: ";
	
	private static final String REGISTER_HEADER = "Registering new user...";
	
	private static final String LOG_IN_HEADER = "Loggin in...";
	
	private static final String LOG_OUT_HEADER = "Loggin out...";
	
	private static final String QUIT_HEADER = "Quitting...";
	
	private static final String APPLY_HEADER = "Applying to open an account...";
	
	private static final String APPROVE_HEADER = "Approving an account...";
	
	private static final String DENY_HEADER = "Denying an account...";
	
	private static final String CLOSE_HEADER = "Closing an account...";
	
	private static final String ADD_OWNER_HEADER 
			= "Adding a new owner to an account...";
	
	private static final String REMOVE_OWNER_HEADER
			= "Removing an owner from an account...";
	
	// instance variables (fields)
	private Scanner scan;
	
	// constructor
	public CommandLineIO() {

		scan = new Scanner(System.in);
	}
	
	// helper methods --------------------
	
	/**
	 * Converts an integer amount of money to a user-friendly string representation.
	 * Eg, 12345 -> "$123.45"
	 * @param funds
	 * @return
	 */
	private String intToMoneyString(int funds) {
		
		String temp = "" + funds;
		
		return "$" + temp.substring(0, temp.length() - 2) + "." + temp.substring(temp.length() - 2);
	}
	
	/**
	 * Converts a user-friendly string representation of money to an int.
	 * EG, "$123.45" -> 12345
	 * "123.45" -> 12345
	 * "12345" -> 12345 (as in, 12345.00)
	 * @param funds
	 * @return
	 */
	private int moneyStringToInt(String funds) throws BadMoneyFormatException{
		
		int startingIndex = 0;
		if (funds.charAt(0) == '$') {
			startingIndex += 1;
		}
		
		String clean = "";
		boolean dotFound = false;
		int dotFoundIndex = -1;
		for (int i = startingIndex; i < funds.length(); i++) {
			
			if (dotFound && (i - dotFoundIndex) > 2) { // only 2 decimal places
				throw new BadMoneyFormatException(
						BAD_MONEY_FORMAT_ONLY_TWO_DECIMAL_PLACES_MESSAGE);
			}
			
			char c = clean.charAt(i);
			if (Character.isDigit(c)) { // numerical digit
				clean = clean + c;
			}
			else if (c == '.') { // only valid once
				if (dotFound) { // this is the 2nd dot, which is invalid
					throw new BadMoneyFormatException(
							BAD_MONEY_FORMAT_ONLY_ONE_DOT_MESSAGE);
				}
				else {
					dotFound = true;
					dotFoundIndex = i;
				}
			}
			else if (c == '$') { // only valid at the start
				throw new BadMoneyFormatException(
						BAD_MONEY_FORMAT_DOLLAR_SIGN_WRONG_MESSAGE);
			}
			else { // other invalid
				throw new BadMoneyFormatException(
						BAD_MONEY_FORMAT_GENERIC_PREFIX + c);
			}
		}
		
		return Integer.parseInt(clean);
	}
	
	
	// methods from IO interface ----------
	
	/**
	 * Displays the given text to the user.
	 * @param text
	 */
	@Override
	public void displayText(String text) {
		
		System.out.println(text);
	}

	/**
	 * Displays information about the given user profile(s) to the user.
	 * @param up
	 */
	@Override
	public void displayText(String text, boolean frame) {
		
		if (frame) {
			System.out.println(FRAME_LINE);
			displayText(text);
			System.out.println(FRAME_LINE);
		}
		else {
			displayText(text);
		}
	}

	/**
	 * Displays information about the given bank accounts to the user.
	 * @param accounts
	 */
	@Override
	public void displayUserProfiles(List<UserProfile> users) {
		
		System.out.println(FRAME_LINE);
		System.out.println(DISPLAY_PROFILES_HEADER);
		System.out.println(FRAME_LINE);
		
		for (UserProfile up : users) {
			String line = "|ID: " + up.getId();
			line = line + " |Username: " + up.getUsername();
			line = line + " |Type: " + up.getType();
			
			if (up.getType() == UserProfileType.CUSTOMER) {
				line = line + " |Owned Accounts:";
				for (int accID : up.getOwnedAccounts()) {
					line = line + " " + accID;
				} // end inner for loop
			}
			
			System.out.println(line);
		} // end outer for loop
	}

	/**
	 * Displays information about the given transaction records to the user.
	 * @param accounts
	 */
	@Override
	public void displayBankAccounts(List<BankAccount> accounts) {
		
		System.out.println(FRAME_LINE);
		System.out.println(DISPLAY_ACCOUNTS_HEADER);
		System.out.println(FRAME_LINE);
		
		for (BankAccount ba : accounts) {
			String line = "|ID: " + ba.getId();
			line = " |Type: " + ba.getType();
			line = " |Status: " + ba.getStatus();
			line = line + " |Funds: " + intToMoneyString(ba.getFunds());
			line = line + " |Owner(s):"; // assume not empty
			
			for (int ownerID : ba.getOwners()) {
				line = line + " " + ownerID;
			} // end inner for loop
			
			System.out.println(line);
		} // end outer for loop
	}

	/**
	 * Returns a Request object based on the user's responding input.
	 * @param permittedRequestTypes : the user chooses one of these
	 * @return Request
	 */
	@Override
	public void displayTransactionRecords(List<TransactionRecord> transactions) {
		
		System.out.println(FRAME_LINE);
		System.out.println(DISPLAY_ACCOUNTS_HEADER);
		System.out.println(FRAME_LINE);
		
		for (TransactionRecord tr : transactions) {
			String line = "|ID: " + tr.getId();
			line = " |Type: " + tr.getType();
			line = " |Time: " + tr.getTime();
			line = " |Acting User: " + tr.getActingUser();
			
			line = " |Source Account: ";
			if (tr.getSourceAccount() == -1) {
				line = line + DISPLAY_FIELD_EMPTY;
			}
			else {
				line = line + tr.getSourceAccount();
			}
			
			line = " |Destination Account: ";
			if (tr.getDestinationAccount() == -1) {
				line = line + DISPLAY_FIELD_EMPTY;
			}
			else {
				line = line + tr.getDestinationAccount();
			}
			
			line = " |Money amount: ";
			if (tr.getMoneyAmount() == -1) {
				line = line + DISPLAY_FIELD_EMPTY;
			}
			else {
				line = line + tr.getMoneyAmount();
			}
			
			System.out.println(line);
		} // end outer for loop
	}

	/**
	 * Returns a Request object based on the user's responding input.
	 * @param permittedRequestTypes : the user chooses one of these
	 * @return Request
	 */
	@Override
	public Request prompt(RequestType[] permittedRequestTypes) {
		
		// Figure out what kind of request the user wants to make
		// Then, handle getting the parameters from the user
		// then, return the request
		RequestType rtype = permittedRequestTypes[chooseRequestType(permittedRequestTypes)];
		Request req = null; // filled in later
		
		switch(rtype) {
		
			case REGISTER_USER:
				req = buildRegisterUser();
				break;
			case LOG_IN:
				req = buildLogIn();
				break;
			case LOG_OUT:
				req = buildLogOut();
				break;
			case QUIT:
				req = buildQuit();
				break;
			case APPLY_OPEN_ACCOUNT:
				req = buildApplyToOpenAccount();
				break;
			case APPROVE_OPEN_ACCOUNT:
				req = buildApproveOpenAccount();
				break;
			case DENY_OPEN_ACCOUNT:
				req = buildDenyOpenAccount();
				break;
			case CLOSE_ACCOUNT:
				req = buildCloseAccount();
				break;
			case ADD_ACCOUNT_OWNER:
				req = buildAddAccountOwner();
				break;
			case REMOVE_ACCOUNT_OWNER:
				req = buildRemoveAccountOwner();
				break;
			case DEPOSIT:
				//req = buildDeposit();
				break;
			case WITHDRAW:
				//req = buildWithdraw();
				break;
			case TRANSFER:
				//req = buildTransfer();
				break;
			case VIEW_ACCOUNTS:
				//req = buildViewAccounts();
				break;
			case VIEW_SELF_PROFILE:
				//req = buildViewSelfProfile();
				break;
			case VIEW_USERS:
				//req = buildViewUsers();
				break;
			case VIEW_TRANSACTIONS:
				//req = buildViewTransactions();
				break;
			case CREATE_EMPLOYEE:
				//req = buildCreateEmployee();
				break;
			case CREATE_ADMIN:
				//req = buildCreateAdmin();
				break;
		}
		
		return req;
	}
	
	/**
	 * Gets the account and user IDs
	 * @return
	 */
	private Request buildRemoveAccountOwner() {
		
		System.out.println(FRAME_LINE);
		System.out.println(REMOVE_OWNER_HEADER);
		System.out.println(FRAME_LINE);
		
		List<String> params = new ArrayList<>();
		params.add("" + parseInt(ACCOUNT_ID_PROMPT, 0, Integer.MAX_VALUE));
		params.add("" + parseInt(USER_ID_PROMPT, 0, Integer.MAX_VALUE));
	
		return new Request(
				RequestType.REMOVE_ACCOUNT_OWNER,
				params);
	}

	/**
	 * Gets the account and user IDs
	 * @return
	 */
	private Request buildAddAccountOwner() {
		
		System.out.println(FRAME_LINE);
		System.out.println(ADD_OWNER_HEADER);
		System.out.println(FRAME_LINE);
		
		List<String> params = new ArrayList<>();
		params.add("" + parseInt(ACCOUNT_ID_PROMPT, 0, Integer.MAX_VALUE));
		params.add("" + parseInt(USER_ID_PROMPT, 0, Integer.MAX_VALUE));
	
		return new Request(
				RequestType.ADD_ACCOUNT_OWNER,
				params);
	}

	/**
	 * Gets the ID of the account to close.
	 * @return
	 */
	private Request buildCloseAccount() {
		
		System.out.println(FRAME_LINE);
		System.out.println(CLOSE_HEADER);
		System.out.println(FRAME_LINE);
		
		List<String> params = new ArrayList<>();
		params.add("" + parseInt(ACCOUNT_ID_PROMPT, 0, Integer.MAX_VALUE));
		
		return new Request(
				RequestType.CLOSE_ACCOUNT,
				params);
	}

	/**
	 * Gets the ID of the account to deny.
	 * @return
	 */
	private Request buildDenyOpenAccount() {
		
		System.out.println(FRAME_LINE);
		System.out.println(DENY_HEADER);
		System.out.println(FRAME_LINE);
		
		List<String> params = new ArrayList<>();
		params.add("" + parseInt(ACCOUNT_ID_PROMPT, 0, Integer.MAX_VALUE));
		
		return new Request(
				RequestType.DENY_OPEN_ACCOUNT,
				params);
	}

	/**
	 * Gets the ID of the account to approve.
	 * @return
	 */
	private Request buildApproveOpenAccount() {
		
		System.out.println(FRAME_LINE);
		System.out.println(APPROVE_HEADER);
		System.out.println(FRAME_LINE);
		
		List<String> params = new ArrayList<>();
		params.add("" + parseInt(ACCOUNT_ID_PROMPT, 0, Integer.MAX_VALUE));
		
		return new Request(
				RequestType.APPLY_OPEN_ACCOUNT,
				params);
	}

	/**
	 * Creates a request to open an account
	 * @return
	 */
	private Request buildApplyToOpenAccount() {
		
		System.out.println(FRAME_LINE);
		System.out.println(APPLY_HEADER);
		System.out.println(FRAME_LINE);
		
		return new Request(RequestType.APPLY_OPEN_ACCOUNT);
	}

	/**
	 * Creates a quit request.
	 * @return
	 */
	private Request buildQuit() {
		
		System.out.println(FRAME_LINE);
		System.out.println(QUIT_HEADER);
		System.out.println(FRAME_LINE);
		
		return new Request(RequestType.QUIT);
	}

	/**
	 * Creates a log out request.
	 * @return
	 */
	private Request buildLogOut() {
		
		System.out.println(FRAME_LINE);
		System.out.println(LOG_OUT_HEADER);
		System.out.println(FRAME_LINE);
		
		return new Request(RequestType.LOG_OUT);
	}

	/**
	 * Gets the username and password.
	 * @return
	 */
	private Request buildLogIn() {
		
		System.out.println(FRAME_LINE);
		System.out.println(LOG_IN_HEADER);
		System.out.println(FRAME_LINE);
		
		List<String> params = new ArrayList<>();
		params.add(parseString(USERNAME_PROMPT));
		params.add(parseString(PASSWORD_PROMPT));
		return new Request(
				RequestType.LOG_IN,
				params);
	}

	/**
	 * Gets the desired username and password.
	 * @return
	 */
	private Request buildRegisterUser() {
		
		System.out.println(FRAME_LINE);
		System.out.println(REGISTER_HEADER);
		System.out.println(FRAME_LINE);
		
		List<String> params = new ArrayList<>();
		params.add(parseString(USERNAME_PROMPT));
		params.add(parseString(PASSWORD_PROMPT));
		return new Request(
				RequestType.REGISTER_USER,
				params);
	}

	/**
	 * Helper method that asks the user which of the provided choices they want.
	 * @param permittedRequestTypes
	 * @return
	 */
	private int chooseRequestType(RequestType[] permittedRequestTypes) {
		
		System.out.println(FRAME_LINE);
		System.out.println(CHOICES_HEADER);
		System.out.println(FRAME_LINE);
		for (int i = 0; i < permittedRequestTypes.length; i++) {
			String line = "(" + i + ") " + permittedRequestTypes[i];
			System.out.println(line);
		}
		
		return parseInt(CHOICES_PROMPT, 0, permittedRequestTypes.length);
	}
	
	/**
	 * Helper method that prompts the user for an int (non-money) value.
	 * Will loop until they give valid input.
	 * @param promptText
	 * @param min : minimum choice value allowed (inclusive)
	 * @param max : minimum choice value allowed (NOT inclusive)
	 * @return
	 */
	private int parseInt(String promptText, int min, int max) {
		
		
		int choice = 0;
		boolean isValid = false;
		do {
			System.out.print(promptText);
			String input = scan.next();
			try {
				choice = Integer.parseInt(input);
				// it's an int, is it a valid int?
				if (min <= choice && choice < max) {
					isValid = true;					
				}
				else {
					System.out.println(PARSE_INT_CHOICE_OUT_OF_BOUNDS_MESSAGE);
				}
			}
			catch (NumberFormatException e) {
				System.out.println(PARSE_INT_INVALID_INPUT_MESSAGE);
			}
		} while(!isValid);
		
		return choice;
	}
	
	/**
	 * Helper method that prompts the user for a string.
	 * Currently, only whitespace characters or the empty string are invalid.
	 * @param promptText
	 * @return
	 */
	private String parseString(String promptText) {
		
		boolean isValid = false;
		String input = ""; // will be filled in
		do {
			System.out.print(promptText);
			input = scan.nextLine();
			
			if (input.equals("")) {
				System.out.println(PARSE_STRING_EMPTY_STRING_INVALID);
			}
			else { // not empty string
				boolean foundWhite = false;
				for (char c : input.toCharArray()) {
					if (Character.isWhitespace(c)){
						foundWhite = true;
						System.out.println(PARSE_STRING_WHITESPACE_INVALID);
						break;
					}
				}
				
				if (!foundWhite) {
					isValid = true;
				}
			} // end else (if not empty string)
		} while(!isValid);
		
		return input;
	}
}
