/**
 * This interface defines what is needed for input/output for the banking program.
 */
package BankIO;

import java.awt.DisplayMode;
import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;

import bankSystem.Request;

public interface BankIO {

	/**
	 * Displays the given text to the user.
	 * @param text
	 */
	public void displayText(String text);
	
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
	 * @return Request
	 */
	public Request prompt(String text);
}
