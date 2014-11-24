BEGIN TRANSACTION;
CREATE TABLE `Profile` (
	`ID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
	`Gender`	INTEGER NOT NULL, /* 0 - Man, 1 - Woman*/
	`DateOfBirth`	INTEGER NOT NULL /* Unixtime representing date */
);
CREATE TABLE `DayRecord` (
	`ID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`SleepDate`	INTEGER NOT NULL, /* Unixtime representing date date + time */
	`Exhaustion`	INTEGER, /* 1 - not tired, 2 - neutral , 3 - tired */
	`WakeupDate`	INTEGER NOT NULL, /* Unixtime representing date + time*/
	`SleepQuality`	INTEGER /* 1 - bad, 2 - neutral , 3 - good  */
);
COMMIT;
