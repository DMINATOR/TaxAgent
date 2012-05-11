<?php
header('Content-Type: text/html; charset=utf-8');
include 'logger.php';

/** 
 * Wrapper for any database operations
 */  
class AdminDatabase
{  
	public $connection;
	
	public $log;
	
	//general methods:
	
	//connects to database
	public function connect()
	{
		$this->log->logSQL("Connecting to database:");
		
		
		$this->connection = mysql_connect("localhost","np33566_agendid","agent007");
		if (!$this->connection)
		{
			   die('Could not connect: ' . mysql_error());
		}
		mysql_select_db("np33566_agendid");
		mysql_set_charset('utf8', $this->connection);
		$this->log->logSQL("Connection OK!");
		
		$this->log->lwrite('test '.$res);
	}
	
	public function execSql( $sql )
	{
		if( $this->connection == null )
		{
			$this->connect();
			mysql_query("set names 'utf8'");
		}

		
		$this->log->logSQL("Exec:". utf8_encode( $sql ));
		$response = mysql_query( utf8_encode( $sql )) ;
		$this->log->logSQL("Exec [Done]:". $response);
		
		return $response;
	}
	
	public function countSql( $sql )
	{
		if( $this->connection == null )
		{
			$this->connect();
			mysql_query("set names 'utf8'");
		}

		
		$this->log->logSQL("Exec (count):". utf8_encode( $sql ));
		$count = mysql_num_rows(mysql_query(utf8_encode( $sql ))) ;
		$this->log->logSQL("Exec (count) [Done]:". $response);
		
		return $count;
	}
	
	// high level calls:
	
	//Clears all tables
	public function clearTables(  )
	{
		$drop_tables = "DROP TABLE IF EXISTS `InstitutionBudgetTable` ,
			`InstitutionTable` ,
			`SystemTable` ,
			`TaxPayerProgramTable` ,
			`TaxPayerTable` ,
			`TaxProgramTable` ,
			`TaxYearTable` ;";
	
		$this->execSql($drop_tables);
		$this->execSql($drop_tables);
	}
	
	
	//Creates new empty tables
	public function createTables(  )
	{
		$this->execSql("
		CREATE TABLE IF NOT EXISTS `InstitutionBudgetTable` (
		  `ID` int(11) NOT NULL AUTO_INCREMENT,
		  `TaxYearID` int(11) NOT NULL,
		  `InstitutionID` int(11) NOT NULL,
		  `ConfirmedDate` date,
		  PRIMARY KEY (`ID`),
		  KEY `TaxYearID` (`TaxYearID`),
		  KEY `InstitutionID` (`InstitutionID`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

		$this->execSql("CREATE TABLE IF NOT EXISTS `InstitutionTable` (
		  `ID` int(11) NOT NULL AUTO_INCREMENT,
		  `Name` text NOT NULL,
		  `Code` text NOT NULL,
		  PRIMARY KEY (`ID`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

		$this->execSql("CREATE TABLE IF NOT EXISTS `SystemTable` (
		  `ID` int(11) NOT NULL,
		  `Date` datetime NOT NULL,
		  PRIMARY KEY (`ID`)
		) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

		$this->execSql("CREATE TABLE IF NOT EXISTS `TaxPayerProgramTable` (
		  `ID` int(11) NOT NULL AUTO_INCREMENT,
		  `TaxPayerID` int(11) NOT NULL,
		  `TaxProgramID` int(11) NOT NULL,
		  `YearsRequired` int(11) NOT NULL,
		  `CalculatedTaxAmount` int(11) NOT NULL,
		  `BenefitsAccumulated` int(11) NOT NULL,
		  `ConfirmedDate` date NOT NULL,
		  PRIMARY KEY (`ID`),
		  KEY `TaxPayerID` (`TaxPayerID`),
		  KEY `TaxProgramID` (`TaxProgramID`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

		$this->execSql("CREATE TABLE IF NOT EXISTS `TaxPayerTable` (
		  `ID` int(11) NOT NULL AUTO_INCREMENT,
		  `Name` text NOT NULL,
		  `Code` text NOT NULL,
		  `Income` int(11) NOT NULL,
		  `ParentID` int(11),
		  `DateOfCreation` date NOT NULL,
		  `Type` int(1) NOT NULL,
		  PRIMARY KEY (`ID`),
		  KEY `ParentID` (`ParentID`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

		$this->execSql("CREATE TABLE IF NOT EXISTS `TaxProgramTable` (
		  `ID` int(11) NOT NULL AUTO_INCREMENT,
		  `Name` text NOT NULL,
		  `PredictedBudget` int(11) NOT NULL,
		  `PredictedUsers` int(11) NOT NULL,
		  `InstitutionBudgetID` int(11) NOT NULL,
		  `ExtendsProgramID` int(11),
		  `MinimumYears` int(11) NOT NULL,
		  `BenefitsExpireInYears` int(11) NOT NULL,
		  `BenefitsApplyTo` int(1) NOT NULL,
		  PRIMARY KEY (`ID`),
		  KEY `InstitutionBudgetID` (`InstitutionBudgetID`),
		  KEY `ExtendsProgramID` (`ExtendsProgramID`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

		$this->execSql("CREATE TABLE IF NOT EXISTS `TaxYearTable` (
		  `ID` int(11) NOT NULL AUTO_INCREMENT,
		  `Year` year(4) NOT NULL,
		  `SubmitDate` date NOT NULL,
		  `StartDate` date NOT NULL,
		  `EndDate` date NOT NULL,
		  `State` int(1) NOT NULL,
		  PRIMARY KEY (`ID`),
		  UNIQUE KEY `Year` (`Year`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

		$this->execSql("ALTER TABLE `InstitutionBudgetTable`
		  ADD CONSTRAINT `InstitutionBudgetTable_ibfk_2` FOREIGN KEY (`InstitutionID`) REFERENCES `InstitutionTable` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
		  ADD CONSTRAINT `InstitutionBudgetTable_ibfk_1` FOREIGN KEY (`TaxYearID`) REFERENCES `TaxYearTable` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;");

		$this->execSql("ALTER TABLE `TaxPayerProgramTable`
		  ADD CONSTRAINT `TaxPayerProgramTable_ibfk_2` FOREIGN KEY (`TaxProgramID`) REFERENCES `TaxProgramTable` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
		  ADD CONSTRAINT `TaxPayerProgramTable_ibfk_1` FOREIGN KEY (`TaxPayerID`) REFERENCES `TaxPayerTable` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;");

		$this->execSql("ALTER TABLE `TaxPayerTable`
		  ADD CONSTRAINT `TaxPayerTable_ibfk_1` FOREIGN KEY (`ParentID`) REFERENCES `TaxPayerTable` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;");

		$this->execSql("ALTER TABLE `TaxProgramTable`
		  ADD CONSTRAINT `TaxProgramTable_ibfk_2` FOREIGN KEY (`ExtendsProgramID`) REFERENCES `TaxProgramTable` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
		  ADD CONSTRAINT `TaxProgramTable_ibfk_1` FOREIGN KEY (`InstitutionBudgetID`) REFERENCES `InstitutionBudgetTable` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;
			");
  
	}
	
	public function createWorld( )
	{
		$this->execSql("insert into SystemTable (ID, Date) Values
			(1, STR_TO_DATE('Jan/01/2012', '%M/%d/%Y'));");
		
		$this->execSql("insert into InstitutionTable (Name, Code) VALUES ('Ministry of education', 'I00001');");
		$this->execSql("insert into InstitutionTable (Name, Code) VALUES ('Social Ministry', 'I00002');");
		$this->execSql("insert into InstitutionTable (Name, Code) VALUES ('Ministry of the defence', 'I00003');");
		$this->execSql("insert into InstitutionTable (Name, Code) VALUES ('Ministry of the interior', 'I00004');");
		
		include "lipsum.php";
		$array = split(" ", str_replace(",", "", str_replace(".", "" , $lipsum)));
		$count = count($array);
			
		for ($i=1; $i<=1000; $i++){
			$firstPart = $array[rand(0, $count )];
			$secondPart = $array[rand(0, $count )];
			$name = ucwords( $firstPart." ".$secondPart );
			$cNr = 10000 + $i;
			$code = "T".$cNr;
			$income = rand(0, 200000);
			$age = rand(0,100);
			$this->execSql("insert into TaxPayerTable (Name, Code, Type, Income, DateOfCreation) VALUES ('".$name."','".$code."',0,'".$income."',DATE_SUB(curdate(),interval ".$age." year));");
		}
		
		$result = $this->execSql("SELECT ID FROM TaxPayerTable where DateOfCreation < date_sub(curdate(),interval 60 YEAR)");
		$nr=1;
		while($row = mysql_fetch_array($result)){
			$i++;
			$firstPart = $array[rand(0, $count )];
			$secondPart = $array[rand(0, $count )];
			$name = ucwords( $firstPart." ".$secondPart );
			$cNr = 10000 + $i;
			$code = "T".$cNr;
			$income = rand(0, 200000);
			$age = rand(30,40);
			$this->execSql("insert into TaxPayerTable (ParentID, Name, Code, Type, Income, DateOfCreation) VALUES (".$row['ID'].",'".$name."','".$code."',0,'".$income."',DATE_SUB(curdate(),interval ".$age." year));");
			$nr++;
			if($nr>300){
				break;
			}
		}
		
		$result = $this->execSql("SELECT ID FROM TaxPayerTable where ParentID is not null");
		$nr=1;
		while($row = mysql_fetch_array($result)){
			$i++;
			$firstPart = $array[rand(0, $count )];
			$secondPart = $array[rand(0, $count )];
			$name = ucwords( $firstPart." ".$secondPart );
			$cNr = 10000 + $i;
			$code = "T".$cNr;
			$age = rand(0,15);
			$this->execSql("insert into TaxPayerTable (ParentID, Name, Code, Type, Income, DateOfCreation) VALUES (".$row['ID'].",'".$name."','".$code."',0,0,DATE_SUB(curdate(),interval ".$age." year));");
			$nr++;
			if($nr>150){
				break;
			}
		}
		
		$typeName = array("AS","OÜ","FIE");
		$typeCount = count($typeName);
		
		for ($i=1; $i<=600; $i++){
			$firstPart = $array[rand(0, $count )];
			$secondPart = $array[rand(0, $count )];
			$name = ucwords( $firstPart." ".$secondPart )." ".$typeName[rand(0, $typeCount -1  )];
			$cNr = 10000 + $i;
			$code = "B".$cNr;
			$income = rand(1000000, 10000000);
			$age = rand(0,100);
			$this->execSql("insert into TaxPayerTable (Name, Code, Type, Income, DateOfCreation) VALUES ('".$name."','".$code."',1,'".$income."',DATE_SUB(curdate(),interval ".$age." year));");
		}
	}
	
	public function getDate( )
	{
		$result = $this->execSql("SELECT Date FROM SystemTable WHERE ID = 1");
		$resArr = mysql_fetch_row($result);
		$result = $resArr[0];
		//$date =	gmdate("Y-m-d\TH:i:s\Z", $result );
		return $result;
	}
	
	public function changeDate( $date )
	{
		$this->execSql("UPDATE SystemTable SET Date='".date( 'Y-m-d H:i:s', $date )."' WHERE ID=1");
		
		//update years now - activate
		$this->execSql("UPDATE TaxYearTable SET State = 2 WHERE StartDate <= '".date( 'Y-m-d H:i:s', $date )."'  AND State = 1");
		
		//set as expired
		$this->execSql("UPDATE TaxYearTable SET State = 3 WHERE EndDate <= '".date( 'Y-m-d H:i:s', $date )."'");
	
	}
	
	public function createTaxYear($jsonData)
	{
		$year = $jsonData['Year'];
		$submitDate = strtotime( $jsonData['SubmitDate']." UTC" );
		$startDate = strtotime( $jsonData['StartDate']." UTC" );
		$endDate = strtotime( $jsonData['EndDate']." UTC" );
		
		$result = $this->execSql("SELECT Count( 1 ) FROM TaxYearTable WHERE Year = ".$year);
		$row = mysql_fetch_row($result);
		
		if( $row[0] == 1 )
		{
			throw new Exception("Year ".$year." already exists in db!");
		}
		
		$this->execSql("INSERT INTO TaxYearTable (Year, SubmitDate, StartDate, EndDate, State) VALUES (".$year.",'".date('Y-m-d', $submitDate)."','".date('Y-m-d', $startDate)."','".date('Y-m-d', $endDate)."',1)"); //State 1 for first creation !
	}
	
	
	
}

?>