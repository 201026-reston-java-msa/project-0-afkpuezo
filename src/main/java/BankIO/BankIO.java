/**
 * This interface defines what is needed for input/output for the banking program.
 * 
 * @author Andrew Curry
 */
package BankIO;

import java.awt.DisplayMode;
import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;

import bankSystem.Request;
import bankSystem.Request.RequestType;

public interface BankIO {

	/**
	 * Displays the given text to the user.
	 * @param text
	 */
	public void displayText(String text);
	
	/**
	 * Displays the given text to the user.
	 * @param text
	 * @param frame : if true, frame the text with a box
	 */
	public void displayText(String text, boolean frame);
	
	/**
	 * Displays information about the given user profile to the user.
	 * @param up
	 */
	public void displayUserProfile(UserProfile up);
	
	/**
	 * Displays information about the given bank accounts to the user.
	 * @param accounts
	 */
	public void displayBankAccounts(List<BankAccount> accounts);
	
	/**
	 * Displays information about the given transaction records to the user.
	 * @param accounts
	 */
	public void displayTransactionRecords(List<TransactionRecord> transactions);
	
	/**
	 * Displays the given text to the user, and returns a Request object based on
	 * the user's responding input.
	 * @param text
	 * @param permittedRequestTypes : the user chooses one of these
	 * @return Request
	 */
	public Request prompt(String text, RequestType[] permittedRequestTypes);
}
