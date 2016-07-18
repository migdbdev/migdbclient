<h1>MIGDB-CLIENT-APPLICATION</h1>


---------------------------- Regarding Application Related Filepaths -------------------------------
There is a enum called 'FilePath' under the 'org.migdb.migdbclient.config' package. All the application regarding file paths are mentioned in there

Ex : As a example if you need database structure json file you can follow this way. FilePath.DOCUMENT.getPath() + FilePath.DBSTRUCTURE.getPath();
