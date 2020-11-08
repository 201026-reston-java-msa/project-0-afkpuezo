/**
 * Contains the main method for the project. Prepares and starts the BankSystem
 * and attendant classes.
 */
package driver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import BankIO.BankIO;
import BankIO.CommandLineIO;
import bankSystem.BankSystem;
import dao.BankDAO;
import dao.BankDAOException;
import dao.TextFileDAO;

public class Driver {

	// class / static vars
	static private final String testFilename = "testfile.bdf"; // 'bank data file'
	static private final String[] FILELINES = {
			"PRF 101 user pass CST 444", "ACC 444 OPN SNG 78923 101", 
			"PRF 103 user2 pass CST 317 515", "ACC 317 OPN SNG 7892312 103", 
			"PRF 999 admin admin ADM", "ACC 515 OPN SNG 111111 103",
			"TRR 1 3:00 FDP 101 -1 444 87654", "TRR 2 3:00 FDP 103 -1 444 225", 
			"TRR 3 4:00 FDP 999 -1 515 12345"
	};
	
	public static void main(String[] args) throws BankDAOException {
		
		BankIO io = new CommandLineIO();
		prepareTextFile();
		BankDAO dao = new TextFileDAO(testFilename);
		BankSystem bank = new BankSystem(io, dao);
		bank.start();
	}
	
	/**
	 * Sets up a text file for use in tests.
	 * @return true if the file could be set up, false otherwise
	 */
	private static boolean prepareTextFile() {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(testFilename));
			
			for (String line : FILELINES){
				writer.write(line);
				writer.write("\n");
			}
			
			writer.close();
		}
		catch (IOException e) {
			System.out.println("ALERT: prepareTextFile could not complete writing the text file.");
			return false;
		}
		
		return true; // only reached if successful
	}
}
