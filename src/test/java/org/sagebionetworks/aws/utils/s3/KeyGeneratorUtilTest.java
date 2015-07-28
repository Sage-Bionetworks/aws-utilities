package org.sagebionetworks.aws.utils.s3;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class KeyGeneratorUtilTest {

	@Test
	public void testPadding(){
		// Since S3 does alpha-numeric sorting on key names, we must pad all numbers
		String expected = "000000008/2020-01-01/01-09-04-003-uuid.csv.gz";
		String resultsString = KeyGeneratorUtil.createKey(8, 2020, 1, 1, 1,9,4,3, "uuid", false);
		assertEquals(expected, resultsString);
	}
	
	@Test
	public void testPadding2(){
		// Since S3 does alpha-numeric sorting on key names, we must pad all numbers
		String expected = "000000900/2020-12-25/59-58-57-999-uuid.csv.gz";
		String resultsString = KeyGeneratorUtil.createKey(900, 2020, 12, 25, 59,58,57,999, "uuid", false);
		assertEquals(expected, resultsString);
	}
	
	@Test
	public void testCreateKey(){
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(2012, 11, 30, 22, 49);
		String resultsString = KeyGeneratorUtil.createNewKey(101, cal.getTimeInMillis(), false);
		assertNotNull(resultsString);
		System.out.println(resultsString);
		assertTrue(resultsString.startsWith("000000101/2012-12-30/22-"));
		assertTrue(resultsString.endsWith(".csv.gz"));
		resultsString = KeyGeneratorUtil.createNewKey(101, cal.getTimeInMillis(), true);
		System.out.println(resultsString);
		assertTrue(resultsString.startsWith("000000101/2012-12-30/22-"));
		assertTrue(resultsString.endsWith("-rolling.csv.gz"));
	}
	
	@Test
	public void testCreateKeyBigInstanceNumber(){
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(2012, 11, 30, 22, 49);
		String resultsString = KeyGeneratorUtil.createNewKey(999999999, cal.getTimeInMillis(), false);
		assertNotNull(resultsString);
		System.out.println(resultsString);
		assertTrue(resultsString.startsWith("999999999/2012-12-30/22-"));
		assertTrue(resultsString.endsWith(".csv.gz"));
	}
	
	@Test
	public void testGetInstancePrefix(){
		String expected = "000000123";
		String results = KeyGeneratorUtil.getInstancePrefix(123);
		assertEquals(expected, results);
	}
	
	@Test
	public void testGetDateString(){
		String expected = "2020-01-02";
		String results = KeyGeneratorUtil.getDateString(2020, 1, 2);
		assertEquals(expected, results);
	}
	
	@Test
	public void testGetDateStringTimeMS(){
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(2012, 11, 30, 22, 49);
		String expected = "2012-12-30";
		String results = KeyGeneratorUtil.getDateString(cal.getTimeInMillis());
		assertEquals(expected, results);
	}
	
	@Test
	public void testExtractDateFromKey(){
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(1982, 0, 1, 22, 49);
		String expected = "1982-01-01";
		String resultsString = KeyGeneratorUtil.createNewKey(101, cal.getTimeInMillis(), false);
		String resultDateString = KeyGeneratorUtil.getDateStringFromKey(resultsString);
		assertEquals(expected, resultDateString);
	}
	
	@Test
	public void testExtractDateHourFromKey(){
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(1982, 0, 1, 22, 49);
		String expected = "1982-01-01/22";
		String resultsString = KeyGeneratorUtil.createNewKey(101, cal.getTimeInMillis(), false);
		String resultDateString = KeyGeneratorUtil.getDateAndHourFromKey(resultsString);
		assertEquals(expected, resultDateString);
	}
	
	@Test
	public void testGetDateAndHourFromTimeMS(){
	    Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(1984, 2, 13, 22, 49);
		String expected = "1984-03-13/22";
		String resultsString = KeyGeneratorUtil.getDateAndHourFromTimeMS(cal.getTimeInMillis());
		assertEquals(expected, resultsString);
	}
	
	@Test
	public void testParse(){
		String key = "000000901/2020-12-25/23-58-57-999-uuid.csv.gz";
		// call under test
		KeyData data = KeyGeneratorUtil.parseKey(key);
		assertNotNull(data);
		assertEquals(901, data.getStackInstanceNumber());
		assertEquals("accessrecord", data.getType());
		assertEquals("000000901/2020-12-25", data.getPath());
		assertEquals("23-58-57-999-uuid.csv.gz", data.getFileName());
		assertFalse(data.isRolling());
		// check the date by creating a new key.
		String clone = KeyGeneratorUtil.createNewKey(data.getStackInstanceNumber(), data.getTimeMS(), data.isRolling());
		assertTrue("Clone: "+clone, clone.startsWith("000000901/2020-12-25/23-58-57-999-"));
		assertTrue("Clone: "+clone, clone.endsWith(".csv.gz"));
	}
	
	@Test
	public void testParseRolling(){
		String key = "000000901/2020-12-25/23-58-57-999-uuid-rolling.csv.gz";
		// call under test
		KeyData data = KeyGeneratorUtil.parseKey(key);
		assertNotNull(data);
		assertEquals(901, data.getStackInstanceNumber());
		assertEquals("accessrecord", data.getType());
		assertEquals("000000901/2020-12-25", data.getPath());
		assertEquals("23-58-57-999-uuid-rolling.csv.gz", data.getFileName());
		assertTrue(data.isRolling());
		// check the date by creating a new key.
		String clone = KeyGeneratorUtil.createNewKey(data.getStackInstanceNumber(), data.getTimeMS(), data.isRolling());
		assertTrue("Clone: "+clone, clone.startsWith("000000901/2020-12-25/23-58-57-999-"));
		assertTrue("Clone: "+clone, clone.endsWith("-rolling.csv.gz"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testParseRollingTooManyParts(){
		String key = "000000901/2020-12-25/23-58-57-999-uuid-rolling.csv./one";
		// call under test
		KeyGeneratorUtil.parseKey(key);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testParseRollingTooFewParts(){
		String key = "000000901/2020-12-25";
		// call under test
		KeyGeneratorUtil.parseKey(key);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testParseBadDate(){
		String key = "000000901/2020-12-25-3/23-58-57-999-uuid-rolling.csv";
		// call under test
		KeyGeneratorUtil.parseKey(key);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testParseBadTime(){
		String key = "000000901/2020-12-25/23-58-57.csv";
		// call under test
		KeyGeneratorUtil.parseKey(key);
	}

	@Test
	public void testSnapshotKey(){
		// Since S3 does alpha-numeric sorting on key names, we must pad all numbers
		String expected = "000000008/type/2020-01-01/01-09-04-003-uuid.csv.gz";
		String resultsString = KeyGeneratorUtil.createKey(8, "type", 2020, 1, 1, 1,9,4,3, "uuid", false);
		assertEquals(expected, resultsString);
	}

	@Test
	public void testParseKeyWithType(){
		String key = "000000901/type/2020-12-25/23-58-57-999-uuid.csv.gz";
		// call under test
		KeyData data = KeyGeneratorUtil.parseKey(key);
		assertNotNull(data);
		assertEquals(901, data.getStackInstanceNumber());
		assertEquals("type", data.getType());
		assertEquals("000000901/type/2020-12-25", data.getPath());
		assertEquals("23-58-57-999-uuid.csv.gz", data.getFileName());
		assertFalse(data.isRolling());
		// check the date by creating a new key.
		String clone = KeyGeneratorUtil.createNewKey(data.getStackInstanceNumber(), data.getType(), data.getTimeMS(), data.isRolling());
		assertTrue("Clone: "+clone, clone.startsWith("000000901/type/2020-12-25/23-58-57-999-"));
		assertTrue("Clone: "+clone, clone.endsWith(".csv.gz"));
	}
}
