/**
 * An implementation of the BankIO interface that uses the Command Line.
 * 
 * @author Andrew Curry
 */
package BankIO;

import java.util.List;
import java.util.Scanner;

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
	
	private static final String BAD_MONEY_FORMAT_ONLY_TWO_DECIMAL_PLACES_MESSAGE
			= "Input has more than 2 characters after the decimal point.";
	private static final String BAD_MONEY_FORMAT_ONLY_ONE_DOT_MESSAGE
			= "Input has a second decimal point.";
	private static final String BAD_MONEY_FORMAT_DOLLAR_SIGN_WRONG_MESSAGE
			= "'$' character is only valid as the first character.";
	private static final String BAD_MONEY_FORMAT_GENERIC_PREFIX
			= "Input contains an invalid character: ";
	
	// instance variables (fields)
	private Scanner input;
	
	// constructor
	public CommandLineIO() {

		input = new Scanner(System.in);
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
				}
			}
			
			System.out.println(line);
		}
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
			line = line + " |Funds: " + intToMoneyString(ba.getFunds());
		}
	}

	/**
	 * Returns a Request object based on the user's responding input.
	 * @param permittedRequestTypes : the user chooses one of these
	 * @return Request
	 */
	@Override
	public void displayTransactionRecords(List<TransactionRecord> transactions) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns a Request object based on the user's responding input.
	 * @param permittedRequestTypes : the user chooses one of these
	 * @return Request
	 */
	@Override
	public Request prompt(RequestType[] permittedRequestTypes) {
		// TODO Auto-generated method stub
		return null;
	}
}
