package org.sagebionetworks.aws.utils.s3;

import java.util.Calendar;

import static java.util.Calendar.*;

import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Utility for generating key for AccessRecords batches and object snapshot.
 * 
 * @author John
 * @author kimyen
 *
 */
public class KeyGeneratorUtil {

	private static final String INSTANCE_PREFIX_TEMPLATE = "%1$09d";
	private static final String INSTANCE_AND_TYPE_PREFIX_TEMPLATE = "%1$09d/%2$s";
	private static final String DATE_TEMPLATE = "%1$04d-%2$02d-%3$02d";
	public static final String ROLLING = "-rolling";
	/**
	 * This template is used to generated a key for a batch of AccessRecords:
	 * <stack_instance>/<year><month><day>/<hour>/<uuid>(-rolling).csv.gz
	 */
	private static final String KEY_TEMPLATE = "%1$s/%2$s/%3$02d-%4$02d-%5$02d-%6$03d-%7$s%8$s.csv.gz";
	/**
	 * This template is used to generated a key for object snapshot:
	 * <stack_instance>/<type>/<year><month><day>/<hour>/<uuid>(-rolling).csv.gz
	 */
	private static final String SNAPSHOT_KEY_TEMPLATE = "%1$s/%2$s/%3$s/%4$02d-%5$02d-%6$02d-%7$03d-%8$s%9$s.csv.gz";


	/**
	 * Create a new Key.
	 * @return
	 */
	public static String createNewKey(int stackInstanceNumber, long timeMS, boolean rolling){
		return createNewKey(stackInstanceNumber, null, timeMS, rolling);
	}

	/**
	 * Create a new Key.
	 * @return
	 */
	public static String createNewKey(int stackInstanceNumber, String type, long timeMS, boolean rolling){
		Calendar cal = getCalendarUTC(timeMS);
		int year = cal.get(Calendar.YEAR);
		// We do a +1 because JANUARY=0 
		int month = cal.get(Calendar.MONTH) +1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int mins = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		int milli = cal.get(Calendar.MILLISECOND);
		if (type != null) {
			return createKey(stackInstanceNumber, type, year, month, day, hour, mins, sec, milli, UUID.randomUUID().toString(), rolling);
		}
		return createKey(stackInstanceNumber, year, month, day, hour, mins, sec, milli, UUID.randomUUID().toString(), rolling);
	}

	/**
	 * Get a new UTC calendar set to the given time.
	 * @param timeMS
	 * @return
	 */
	public static Calendar getCalendarUTC(long timeMS) {
		Calendar cal = getClaendarUTC();
	    cal.setTime(new Date(timeMS));
		return cal;
	}
	
	/**
	 * Get a new Calendar Set to UTC time zone.
	 * @return
	 */
	public static Calendar getClaendarUTC(){
		return Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"));
	}

	/**
	 * Create a key from all of the parts.
	 * @param instance The stack instance number must be padded with 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param uuid
	 * @return
	 */
	static String createKey(int instance, int year, int month, int day, int hour, int min, int sec, int milli, String uuid, boolean rolling){
		String roll = rolling ? ROLLING : "";
		return String.format(KEY_TEMPLATE, getInstancePrefix(instance), getDateString(year, month, day), hour, min, sec, milli, uuid, roll);
	}

	/**
	 * Create a key from all of the parts.
	 * @param instance The stack instance number must be padded with 
	 * @param type The type of the object
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param uuid
	 * @return
	 */
	static String createKey(int instance, String type, int year, int month, int day, int hour, int min, int sec, int milli, String uuid, boolean rolling){
		String roll = rolling ? ROLLING : "";
		return String.format(SNAPSHOT_KEY_TEMPLATE, getInstancePrefix(instance), type, getDateString(year, month, day), hour, min, sec, milli, uuid, roll);
	}

	/**
	 * Get the prefix used for this instance.
	 * @param instance
	 * @return
	 */
	public static String getInstancePrefix(int instance){
		return String.format(INSTANCE_PREFIX_TEMPLATE, instance);
	}
	
	/**
	 * Get the date string
	 * @param timeMS
	 * @return
	 */
	public static String getDateString(long timeMS){
	    Calendar cal = getCalendarUTC(timeMS);
	    int year = cal.get(Calendar.YEAR);
	    // We do a +1 because JANUARY=0 
	    int month = cal.get(Calendar.MONTH) + 1;
	    int day = cal.get(Calendar.DAY_OF_MONTH);
		return getDateString(year, month, day);
	}
	
	/**
	 * Get the date String
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getDateString(int year, int month, int day){
		return String.format(DATE_TEMPLATE, year, month, day);
	}
	
	/**
	 * Extract the date string from a key
	 * @param key
	 * @return
	 */
	public static String getDateStringFromKey(String key){
		String[] split = key.split("/");
		return split[1];
	}
	
	/**
	 * Extract the date and hour from the key
	 * @param key
	 * @return
	 */
	public static String getDateAndHourFromKey(String key){
		String[] split = key.split("/");
		StringBuilder builder = new StringBuilder();
		builder.append(split[1]);
		builder.append("/");
		builder.append(split[2].substring(0, 2));
		return builder.toString();
	}
	
	/**
	 * Create a string with the date and hour for UTC time in MS.
	 * 
	 * @param timeMs
	 * @return
	 */
	public static String getDateAndHourFromTimeMS(long timeMs){
		// Create a key that would contain the passed time.
		String tempKey = createNewKey(1, timeMs, false);
		return getDateAndHourFromKey(tempKey);
	}
	
	/**
	 * Parse the given key generated by this utility.
	 * Access record key has 3 parts, and snapshot record key has 4 parts.
	 * 
	 * @param key
	 * @return
	 */
	public static KeyData parseKey(String key){
		String[] split = key.split("/");
		if (split.length == 3) {
			return parseKey(split, key);
		}
		if (split.length == 4) {
			return parseKeyWithType(split, key);
		}
		throw new IllegalArgumentException("Unknown key format: "+key);
	}

	/**
	 * Parse the given key that contains an object type.
	 * 
	 * @param split 4 parts of the snapshot record key
	 * @param key the given key
	 * @return the keydata object for the given key
	 */
	private static KeyData parseKeyWithType(String[] split, String key) {
		KeyData data = new KeyData();
		data.setStackInstanceNumber(Integer.parseInt(split[0]));
		data.setType(split[1]);
		data.setTimeMS(getTimeMS(split[2].split("-"), split[3].split("-"), key));
		data.setPath(getPathWithType(split));
		data.setFileName(split[3]);
		data.setRolling(split[3].contains(ROLLING));
		return data;
	}

	/**
	 * Parse the given key
	 * 
	 * @param split 3 parts of the access record key
	 * @param key the given key
	 * @return the keydata object for the given key
	 */
	private static KeyData parseKey(String[] split, String key) {
		KeyData data = new KeyData();
		data.setStackInstanceNumber(Integer.parseInt(split[0]));
		data.setType("accessrecord");
		data.setTimeMS(getTimeMS(split[1].split("-"), split[2].split("-"), key));
		data.setPath(getPath(split));
		data.setFileName(split[2]);
		data.setRolling(split[2].contains(ROLLING));
		return data;
	}

	/**
	 * Build the path from the parts of the key
	 * 
	 * @param split 3 parts of the access record key
	 * @return
	 */
	private static String getPath(String[] split) {
		StringBuilder builder = new StringBuilder();
		builder.append(split[0]).append("/").append(split[1]);
		return builder.toString();
	}

	/**
	 * Build the path from the parts of the key
	 * 
	 * @param split 4 parts of the snapshot record key
	 * @return
	 */
	private static String getPathWithType(String[] split) {
		StringBuilder builder = new StringBuilder();
		builder.append(split[0]).append("/").append(split[1]).append("/").append(split[2]);
		return builder.toString();
	}

	/**
	 * Get the time in milisecond from date and time
	 * 
	 * @param dateSplit
	 * @param timeSplit
	 * @param key
	 * @return
	 */
	private static long getTimeMS(String[] dateSplit, String[] timeSplit, String key) {
		if(dateSplit.length != 3){
			throw new IllegalArgumentException("Unknown key format: "+key);
		}
		Calendar cal = getClaendarUTC();
		cal.set(YEAR, Integer.parseInt(dateSplit[0]));
		cal.set(MONTH, Integer.parseInt(dateSplit[1])-1);
		cal.set(DATE, Integer.parseInt(dateSplit[2]));

		if(timeSplit.length < 4){
			throw new IllegalArgumentException("Unknown key format: "+key);
		}
		cal.set(HOUR_OF_DAY, Integer.parseInt(timeSplit[0]));
		cal.set(MINUTE, Integer.parseInt(timeSplit[1]));
		cal.set(SECOND, Integer.parseInt(timeSplit[2]));
		cal.set(MILLISECOND, Integer.parseInt(timeSplit[3]));

		return cal.getTimeInMillis();
	}

	/**
	 * Get the prefix for this instance and type
	 * 
	 * @param instance
	 * @param type
	 * @return
	 */
	public static String getInstanceAndTypePrefix(int instance, String type) {
		return String.format(INSTANCE_AND_TYPE_PREFIX_TEMPLATE, instance, type);
	}
}
