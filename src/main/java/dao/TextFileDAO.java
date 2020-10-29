package dao;

import java.util.List;

import com.revature.bankDataObjects.BankAccount;
import com.revature.bankDataObjects.BankData;
import com.revature.bankDataObjects.TransactionRecord;
import com.revature.bankDataObjects.UserProfile;

public class TextFileDAO implements BankDAO {

	@Override
	public boolean setResourceName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getResourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BankAccount readBankAccount(int accID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BankAccount> readAllBankAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfile readUserProfile(int userID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserProfile> readAllUserProfiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionRecord readTransactionRecord(int recID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransactionRecord> readAllTransactionRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean write(BankData bd) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write(List<BankData> data) {
		// TODO Auto-generated method stub
		return false;
	}

}
