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
	 * @return the list of records or null if no records exist
	 */
	List<DayRecord> getAllRecords();

	/**
	 * Returns a record by id
	 * 
	 * @param id
	 *            the record id
	 *
	 * @return the day record or null if no record with the id is found
	 */
	DayRecord loadDayRecord(int id);

	/**
	 * Creates a new day record
	 * 
	 * @param dayRecord
	 * 				the record to insert
	 *
	 * @return the row id, or -1 if an error occurred
	 */
	int insertRecord(DayRecord dayRecord);

	/**
	 * Updates a day record
	 *
	 * @param dayRecord
	 * 				the record to update
	 *
	 * @return the number of rows affected
	 */
	int updateRecord(DayRecord dayRecord);


	/**
	 * Deletes a day record by id
	 *
	 * @param dayRecord
	 * 			the record to delete
	 *
	 * @return the number of rows affected
	 */
	int deleteRecord(DayRecord dayRecord);
	/**
	 * 
	 * @param start the period start
	 * @param end the period end
	 * @returnReturns a list of dayRecords
	 */
	List<DayRecord> getRecords(long start, long end);
}
