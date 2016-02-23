import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TextBuddyTest {

	@Before
	public void setUp() throws Exception {
		String[] args = {"myfile.txt"};
		TextBuddy.main(args);
		TextBuddy.executeCommand("clear");
	}
	
	@Test
	public void testAdd() throws IOException {
		testClear();
		// check if the ¡°add¡± command returns the right status message
		assertEquals("added to myfile.txt: \"first item\"", TextBuddy.executeCommand("add first item"));
		// check if the item was actually added to the file
		assertEquals(1, TextBuddy.readFile());
		// check if the ¡°add¡± command returns the right status message
		assertEquals("added to myfile.txt: \"second item\"", TextBuddy.executeCommand("add second item"));
		// check if the item was actually added to the file
		assertEquals(2, TextBuddy.readFile());
	}
	
	@Test
	public void testDelete() throws IOException {
		testAdd();
		// check if the item was actually added into the file
		assertEquals(2, TextBuddy.readFile());
		// check if the ¡°delete¡± command returns the right status message
		assertEquals("deleted from myfile.txt: \"second item\"", TextBuddy.executeCommand("delete 2"));
		// check if the item was actually deleted from the file
		assertEquals(1, TextBuddy.readFile());
		// check if the ¡°delete¡± command returns the right status message
		assertEquals("Invalid commands", TextBuddy.executeCommand("delete first item"));
		assertEquals("Invalid commands", TextBuddy.executeCommand("delete 4hefdhd"));
	}
	
	@Test
	public void testDisplay() throws IOException {
		testClear();
		// check if the ¡°display¡± command returns the right status message
		assertEquals("myfile.txt is empty", TextBuddy.executeCommand("display"));
		testAdd();
		// check if the ¡°display¡± command returns the right status message
		assertEquals("1. first item\n2. second item\n", TextBuddy.executeCommand("display"));
		assertEquals("Invalid commands", TextBuddy.executeCommand("display 1"));
	}
	
	@Test
	public void testClear() throws IOException {
		assertEquals("Invalid commands", TextBuddy.executeCommand("clear jsd98g"));
		// check if the ¡°clear¡± command returns the right status message
		assertEquals("all content deleted from myfile.txt", TextBuddy.executeCommand("clear"));
		// check if the file was actually cleared
		assertEquals(0, TextBuddy.readFile());
	}
	
	@Test
	public void testSort() throws IOException {
		testClear();
		TextBuddy.executeCommand("add dhfg");
		TextBuddy.executeCommand("add ytjhds");
		TextBuddy.executeCommand("add xthdf");
		assertEquals("1. dhfg\n2. ytjhds\n3. xthdf\n", TextBuddy.executeCommand("display"));
		
		// check if the ¡°sort¡± command returns the right status message
		assertEquals("Invalid commands", TextBuddy.executeCommand("sort sdhedr"));
		assertEquals("all lines are sorted in alphabetical order", TextBuddy.executeCommand("sort"));
		// check if the lines was actually sorted
		assertEquals("1. dhfg\n2. xthdf\n3. ytjhds\n", TextBuddy.executeCommand("display"));
		
		TextBuddy.executeCommand("add arhha");
		assertEquals("1. dhfg\n2. xthdf\n3. ytjhds\n4. arhha\n", TextBuddy.executeCommand("display"));
		// check if the ¡°sort¡± command returns the right status message
		assertEquals("all lines are sorted in alphabetical order", TextBuddy.executeCommand("sort"));
		// check if the lines was actually sorted
		assertEquals("1. arhha\n2. dhfg\n3. xthdf\n4. ytjhds\n", TextBuddy.executeCommand("display"));
	}
	
	@Test
	public void testSearch() throws IOException {
		testClear();
		TextBuddy.executeCommand("add first");
		TextBuddy.executeCommand("add first item");
		TextBuddy.executeCommand("add firs");
		// check if the ¡°search¡± command returns the right status message
		assertEquals("1. first\n2. first item\nsearch done", TextBuddy.executeCommand("search first"));
		assertEquals("2. first item\nsearch done", TextBuddy.executeCommand("search item"));
		assertEquals("1. first\n2. first item\n3. firs\nsearch done", TextBuddy.executeCommand("search firs"));
		assertEquals("2. first item\nsearch done", TextBuddy.executeCommand("search t it"));
	}
}
