package pt.isec.gps.g22.sleeper.dal;

import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;

/**
 * DAO for the day record
 */
public interface DayRecordDAO {

	/**
	 * Returns all the records relevant for the week containing the date
	 * 
	 * @param date
	 * @return
	 */
	List<DayRecord> getWeekRecords(int date);

	/**
	 * Returns a record by id
	 * 
	 * @param id
	 *            the record id
	 * @return the day record or null if no record with the id is found
	 */
	DayRecord getRecord(int id);

	/**
	 * Creates a new day record
	 * 
	 * @param day
	 *            record the record to insert
	 */
	void insertRecord(DayRecord dayRecord);

	/**
	 * Updates a day record
	 * 
	 * @param dayRecord
	 *            the record to update
	 */
	void updateRecord(DayRecord dayRecord);

	/**
	 * Deletes a day record by id
	 * 
	 * @param id
	 *            the id of the record to delete
	 */
	void deleteRecord(int id);
}
