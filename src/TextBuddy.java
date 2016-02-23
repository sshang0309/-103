
/* 
* @author Shang Shuqi
* @date 2016/1/31 7:07:58 a.m.
* @assumptions about program behavior:
* 	1. If the path for the file is not correct or the file is not a text file, print an error message and exit the program
* 	2. If the file doesn't exist, create a new file
*   3. If the command that the user input is invalid, output the COMMAND_ERROR_MESSAGE
*   4. If the item to be deleted is not found, output the NUMBER_NOT_FOUND_MESSAGE
*   5. Add command will assign the number(the existing biggest number plus one) to the input text
*   6. After deleting an item, the number assigned to other items will not be changed
* 	7. The file will be saved to the disk after each user operation
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TextBuddy {
	
	//error message
	private static final String ERROR_MESSAGE_INVALID_FILE = "The file cannot be accessed";
	private static final String ERROR_MESSAGE_INVALID_COMMANDS = "Invalid commands";
	
	//feedback message
	private static final String MESSAGE_INPUT = "Command: ";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. " + "%s" + " is ready for use\n";
	private static final String MESSAGE_FILE_EMPTY = "%s" + " is empty";
	private static final String MESSAGE_CLEAR_DONE = "all content deleted from " + "%s";
	private static final String MESSAGE_DELETE_DONE = "deleted from " + "%s" + ": \"" + "%s" + "\"";
	private static final String MESSAGE_ADD_DONE = "added to " + "%s" + ": \"" + "%s" + "\"";
	private static final String MESSAGE_SORT_DONE = "all lines are sorted in alphabetical order";
	private static final String MESSAGE_SEARCH_DONE = "search done";
	
	//these are the correct input size for each kind of command
	private static final int INPUT_SIZE_DISPLAY_CLEAR_EXIT_SORT = 1;
	private static final int INPUT_SIZE_DELETE = 2;
	private static final int LEAST_INPUT_SIZE_ADD_SEARCH = 2;
	
	//the start position of string for each item in List<String> contents
	private static final int TEXT_START_POSITION = 3;
	
	private static String fileName = new String();
	
	//this list is used to store all the content in the text file by line
	private static List<String> contents = new ArrayList<String>();
	
	//this variable is used to store the number assigned to the last line in the text file
	private static int lastNumber;
	
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		checkValidFile(args);
		createFile(args);
		welcomeMessage();
		execution();
	}

	//print the welcome message
	private static void welcomeMessage() {
		System.out.printf(MESSAGE_WELCOME, fileName);
	}

	//execute the command that the user input until exit
	private static void execution() throws IOException {
		while(true){
			System.out.printf(MESSAGE_INPUT);
			String input = scanner.nextLine();
			System.out.println(executeCommand(input));
		}
	}

	static String executeCommand(String input) throws IOException {
		String[] command = input.split(" ");
		
		if(command.length == INPUT_SIZE_DISPLAY_CLEAR_EXIT_SORT){
			switch(command[0]){
			case "display":
				return display(contents);
			case "clear":
				return clear();
			case "sort":
				return sort();
			case "exit":
				System.exit(0);
			default:
				return ERROR_MESSAGE_INVALID_COMMANDS;
			}
		}
		
		else if(command.length == INPUT_SIZE_DELETE && command[0].equals("delete")){
			return delete(command);
		}
		
		else if(command.length >= LEAST_INPUT_SIZE_ADD_SEARCH){
			switch(command[0]){
			case "add":
				return add(input);
			case "search":
				return search(input);
			default:
				return ERROR_MESSAGE_INVALID_COMMANDS;
			}
		}
		
		else{
			return ERROR_MESSAGE_INVALID_COMMANDS;
		}
	}
	
	//display the contents of the text file
	private static String display(List<String> list){
		if(list.isEmpty()){
			return String.format(MESSAGE_FILE_EMPTY, fileName);
		}
		
		else{
			String output = "";
			for(String content: list){
				output += content + '\n';
			}
			return output;
		}
	}
	
	//clear all the contents of the text file
	private static String clear() throws IOException {
		contents.clear();
		lastNumber = 0;
		saveFile();
		return String.format(MESSAGE_CLEAR_DONE, fileName);
	}
	
	//search for the input word and return the lines containing that word
	private static String search(String input) throws IOException {
		String text = getItemContent(input);
		List<String> searchResult = searchKeyword(text);
		return String.format(display(searchResult) + MESSAGE_SEARCH_DONE);
	}

	//extract the lines containing the keyword
	private static List<String> searchKeyword(String text) {
		List<String> searchResult = new ArrayList<String>();
		for(String content : contents){
			if(content.contains(text))
				searchResult.add(content);
		}
		return searchResult;
	}
	
	//sort lines alphabetically
	private static String sort() throws IOException{
		List<String> texts = extractText();
		Collections.sort(texts);
		writeContent(texts);
		lastNumber = texts.size();
		saveFile();
		return String.format(MESSAGE_SORT_DONE);
	}

	//extract the text without the serial number before it
	private static List<String> extractText() {
		List<String> texts = new ArrayList<String>();
		for (String content : contents) {
			String text = content.substring(TEXT_START_POSITION);
			texts.add(text);
		}
		return texts;
	}
	
	//write the sorted lines of text back to list of contents
	private static void writeContent(List<String> texts) {
		contents.clear();
		for (int i = 0; i < texts.size(); i++) {
			String content = (i+1) + ". " + texts.get(i);
			contents.add(content);
		}
	}
	
	//delete the items according to the number that the user input
	private static String delete(String[] command) throws IOException {
		for(int i = 0; i < contents.size(); i++){
			if(contents.get(i).substring(0, 1).equals(command[1])){
				String item = contents.get(i).substring(TEXT_START_POSITION);
				deleteIndex(i);
				contents.remove(i);
				saveFile();
				return String.format(MESSAGE_DELETE_DONE, fileName, item);
			}
		}
		
		//if the number is not found in List<String> contents, print an error message
		return String.format(ERROR_MESSAGE_INVALID_COMMANDS);
	}

	//update the lastNumber if the item to be deleted is the last item
	private static void deleteIndex(int index) {
		if(index == contents.size() - 1){
			if(index == 0)
				lastNumber = 0;
			else
				lastNumber = Integer.parseInt(contents.get(index-1).substring(0, 1));
		}
	}
	
	//add the input string to the text file
	private static String add(String input) throws IOException {
		lastNumber++;
		
		String text = getItemContent(input);
		String item = lastNumber + ". " + text;
		
		contents.add(item);
		saveFile();
		return String.format(MESSAGE_ADD_DONE, fileName, text);
	}
	
	//get the item after the "add" or "search" argument
	private static String getItemContent(String input) {
		int position = input.indexOf(" ");
		String ItemContent = input.substring(position + 1);
		return ItemContent;
	}

	//check whether the file is valid
	private static void checkValidFile(String[] args) {
		
		int last_four_characters = args[0].length() - 4;
		boolean is_txt = (args[0].substring(last_four_characters)).equals(".txt");
		
		if(args.length != 1 || (!is_txt)){
			System.out.println(ERROR_MESSAGE_INVALID_FILE);
			System.exit(0);
		}
	}

	//create the file if the file doesn't exist, read the file if the file already exists
	private static void createFile(String[] args) throws IOException {
		fileName = args[0];
		
		File file = new File(args[0]);
		if (!file.exists())
			file.createNewFile();
		else
			readFile();
	}

	//find the number assigned to the last line of the text file
	private static int findLastNumber() {
		int lastNum;
		
		if (contents.isEmpty()) {
			lastNum = 0;
		} 
		
		else {
			int lastLineNumber = contents.size() - 1;
			String[] lastItem = contents.get(lastLineNumber).split(". ");
			lastNum = Integer.parseInt(lastItem[0]);
		}
		
		return lastNum;
	}
	
	//read the content of the file and store it to the List<String> contents
	static int readFile() throws FileNotFoundException, IOException {
		int lineCount = 0;
		contents.clear();
		
		FileInputStream fis = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String line = br.readLine();
		while (line != null) {
			lineCount++;
			contents.add(line);
			line = br.readLine();
		}
		
		lastNumber = findLastNumber();
		
		br.close();
		return lineCount;
	}
	
	//save the file after each user operation(write the contents of the List<String> contents to the file)
	private static void saveFile() throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		for (String content : contents) {
			bw.write(content);
			bw.newLine();
		}
		
		bw.flush();
		
		fos.close();
		bw.close();
	}
}
