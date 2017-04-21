package kostiskag.unitynetwork.bluenode.RunData.tables;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import kostiskag.unitynetwork.bluenode.RunData.tables.AccountsTable;

public class AccountsTableTest {
	
	@Test
	public void initTest() {
		AccountsTable table = new AccountsTable();
		try {
			table.insert("pakis", "1234", "pakis-1", 1);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(true);
		System.out.println(table.toString());
	}
	
	@Test
	public void checkUniqueTest() {
		AccountsTable table = new AccountsTable();
		try {
			table.insert("pakis", "1234", "pakis-1", 1);
			//same ip with the above
			table.insert("makis", "12345", "pakis-2", 1);
			//even if the below statement is correct the catch does not allow it to be executed
			table.insert("makis", "12345", "pakis-4", 3);
		} catch (Exception e) {
			assertTrue(true);
		}
		assertEquals(table.getSize(),1);
		
		
		try {
			table.insert("lakis", "12346", "pakis-3", 4);
			//same hostname with the above
			table.insert("takis", "12347", "pakis-3", 5);
		} catch (Exception e) {
			assertTrue(true);
		}
		assertEquals(table.getSize(),2);
		System.out.println(table.toString());		
	}
	
	@Test
	public void insetCheckTest() {
		AccountsTable table = new AccountsTable();
		try {
			table.insert("pakis", "1234", "pakis-1", 1);
			table.insert("pakis", "1234", "pakis-2", 3);
			table.insert("pakis", "1234", "pakis-3", 4);
			table.insert("pakis", "1234", "pakis-4", 5);
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(table.checkList("pakis-1", "pakis", "597fd1c04c2543183fd58155d560844883a4709f13b0090de00fde6214b681d1"));		
	}
	
	@Test
	public void getvaddrTest() {
		AccountsTable table = new AccountsTable();
		try {
			table.insert("pakis", "1234", "pakis-1", 2);
			table.insert("pakis", "1234", "pakis-2", 3);
			table.insert("pakis", "1234", "pakis-3", 4);
			table.insert("pakis", "1234", "pakis-4", 5);
		} catch (Exception e) {
			assertTrue(false);
		}
		System.out.println(table.toString());
		assertEquals(table.getVaddrIfExists("pakis-3", "pakis", "597fd1c04c2543183fd58155d560844883a4709f13b0090de00fde6214b681d1"), "10.0.0.6");
	}
}