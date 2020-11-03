/**
 * This file contains Junit tests 
 * 
 * Andrew Curry, Project 0
 */
package bankTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import dao.BankDAO;
import dao.BankDAOException;
import dao.TextFileDAO;

public class BankTest {
	
	/**
	 * -----------------------------------------------------------------------
	 * TextFileDAO
	 * -----------------------------------------------------------------------
	 */
	
	// some helper methods/variables
	
	static private final String testFilename = "testfile.bdf"; // 'bank data file'
	static private TextFileDAO tdao;
	static private final String[] FILELINES = {
			"PRF 101 user pass CST 444", "ACC 444 OPN SNG 78923 101", "PRF 103 user2 pass CST 317 515",
			"ACC 317 OPN SNG 7892312 103", "PRF 999 admin admin ADM", "ACC 515 OPN SNG 111111 103"
	};
	
	/**
	 * Sets up a text file for use in tests.
	 * @return true if the file could be set up, false otherwise
	 */
	private boolean prepareTextFile() {
		
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
	
	/**
	 * Sets up the text DAO for use
	 * @return true if successful
	 */
	private boolean prepareTextFileDAO() {
		
		try {
			tdao = new TextFileDAO(testFilename);
		}
		catch (BankDAOException e){
			System.out.println("ALERT: prepareTextFileDAO could not create a TextFileDAO");
			return false;
		}
		
		return true;
	}
	
	
	@Test
	public void testSearchFilePositive() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			String result;
			
			result = tdao.searchFile("ACC 444");
			//System.out.println(result);
			hasPassed = hasPassed && result.equals("ACC 444 OPN SNG 78923 101");
			
			result = tdao.searchFile("PRF 103");
			//System.out.println(result);
			hasPassed = hasPassed && result.equals("PRF 103 user2 pass CST 317 515");
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
		
	}
	
	@Test
	public void testSearchFileNegative() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			String result;
			
			result = tdao.searchFile("ACC 000");
			hasPassed = hasPassed && result.equals("");
			
			result = tdao.searchFile("nonsense");
			hasPassed = hasPassed && result.equals("");
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testSearchFileMultiple() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			List<String> results = tdao.searchFileMultiple("ACC");
			List<String> allLines = List.of(FILELINES);
			List<String> expected = new ArrayList<String>();
			for (String s : allLines) {
				if (s.startsWith("ACC")) {
					expected.add(s);
				}
			}
			
			hasPassed = results.equals(expected);
			
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testSearchFileMultipleNegative() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			List<String> results = tdao.searchFileMultiple("foobar");
			
			hasPassed = results.isEmpty();
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testSearchFileGetAll() {
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		
		try {
			List<String> results = tdao.searchFileMultiple("");
			List<String> allLines = List.of(FILELINES);
			
			hasPassed = results.equals(allLines);
		}
		catch (BankDAOException e) {
			hasPassed = false;
		}
		finally {
			assertTrue(hasPassed);
		}
	}
	
	@Test
	public void testWriteEntry() throws BankDAOException, IOException {
		
		boolean hasPassed = true;
		
		// use a new/blank text document for this one
		String writetest = "writetest.bdf";
		BufferedWriter writer = new BufferedWriter(new FileWriter(writetest));
		tdao = new TextFileDAO(writetest);
		tdao.writeEntry("PRF 101 user pass CST 444");
		
		// now check the file
		BufferedReader reader = new BufferedReader(new FileReader(writetest));
		List<String> fileData = new ArrayList<String>();
		while (reader.ready()) {
			fileData.add(reader.readLine());
		}
		
		hasPassed = hasPassed && 
				((fileData.size() == 1) 
				&& (fileData.get(0).equals("PRF 101 user pass CST 444")));
		
		assertTrue(hasPassed);
	}
	
	@Test
	public void testWriteEntryOverwrite() throws BankDAOException, IOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		boolean hasPassed = true;
		//System.out.println("Check");
		
		// cache file data before changing
		BufferedReader reader = new BufferedReader(new FileReader(testFilename));
		List<String> expected = new ArrayList<String>();
		while (reader.ready()) {
			expected.add(reader.readLine());
		}
		reader.close();
		
		tdao.writeEntry("PRF 101 user newpass CST 444");
		
		// check to make sure it's right
		expected.remove("PRF 101 user pass CST 444");
		expected.add("PRF 101 user newpass CST 444");
		
		List<String> fileData = new ArrayList<String>();
		reader = new BufferedReader(new FileReader(testFilename));
		while (reader.ready()) {
			fileData.add(reader.readLine());
		}
		
		hasPassed = expected.equals(fileData);
		
		/*
		System.out.println("expected:");
		for (String s : expected) {
			System.out.println("    " + s);
		}
		System.out.println("actual:");
		for (String s : fileData) {
			System.out.println("    " + s);
		}
		*/
		
		assertTrue(hasPassed);
	}
	
	@Test
	public void testWriteMultipleEntries() throws BankDAOException, IOException{
		
		prepareTextFile();
		prepareTextFileDAO();
		//System.out.println("Check");
		
		// cache file data before changing
		BufferedReader reader = new BufferedReader(new FileReader(testFilename));
		List<String> expected = new ArrayList<String>();
		while (reader.ready()) {
			expected.add(reader.readLine());
		}
		reader.close();
		
		// generate the entries to be added
		List<String> entriesAdded = new ArrayList<String>();
		entriesAdded.add("PRF 101 user newpass CST 444"); // update
		entriesAdded.add("PRF 989 usertest testpass CST 89003"); // new
		entriesAdded.add("ACC 317 OPN SNG 100 103"); // update
		
		// update the expected list
		expected.remove("PRF 101 user pass CST 444");
		expected.remove("ACC 317 OPN SNG 7892312 103");
		for (String entry : entriesAdded) {
			expected.add(entry);
		}
		
		// do the writing and check the results
		tdao.writeMultipleEntries(entriesAdded);
		
		reader = new BufferedReader(new FileReader(testFilename));
		List<String> actual = new ArrayList<String>();
		while (reader.ready()) {
			actual.add(reader.readLine());
		}
		reader.close();
		
		/*
		System.out.println("expected:");
		for (String s : expected) {
			System.out.println("    " + s);
		}
		System.out.println("actual:");
		for (String s : actual) {
			System.out.println("    " + s);
		}
		*/
		
		// writeMultipleEntries changes the order around, rather than try to
		// duplicate it I've written some code here that shouldn't care about order
		boolean hasPassed = true;
		
		for (String s : expected) {
			if (actual.contains(s)){
				actual.remove(s);
			}
			else {
				hasPassed = false;
			}
		}
		
		
		hasPassed = hasPassed && actual.isEmpty();
		assertTrue(hasPassed);
	}
}
