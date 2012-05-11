<?php

include 'adminTest.php';

/** 
 * Administrator actions are added here
 */  
class AdminActions
{  
	public $log;
	
	public function clearDatabase( $database )
	{
		$database->clearTables();
		$database->createTables();
	}
	
	public function createWorld( $database)
	{
		$database->createWorld();
	}
	
	public function getDate( $database)
	{
		return $database->getDate();
	}
	
	public function changeDate( $database, $jsonData)
	{
		$date = strtotime( $jsonData." UTC" );
		$database->changeDate($date);
	}
	
	public function createTaxYear( $database, $jsonData)
	{
		$database->createTaxYear($jsonData);
		return 1;
	}
	
	public function deleteTaxYear( $database, $jsonData)
	{
		$database->execSql("DELETE FROM TaxYearTable WHERE ID=".$jsonData." and State < 2");

		return 1;
	}
	
	public function getTaxYearGrid( $database, $jsonData)
	{
		$result = $database->execSql("SELECT * FROM `TaxYearTable` LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
		
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;

	}
	
	public function getTaxYearGridCount($database)
	{
		$result = $database->countSql("SELECT * FROM `TaxYearTable`");
		
		return $result;

	}
	
	public function searchGrid( $database, $jsonData )
	{
		
		//filter by type
		$filterType = "";
		
		
		
		//by default show all the types type = 0
		$taxPayerPart = "SELECT ID, Name, Code, ParentID, 
			IF( TaxPayer.Type = 0,1,2) AS Type
			FROM `TaxPayerTable` TaxPayer WHERE Name LIKE '%".$jsonData['Name']."%'";
			
			
		$middlePart = "";
		
		$institutionPart = "SELECT ID, Name, Code, NULL as ParentID, 3 AS Type
			FROM `InstitutionTable` Institution WHERE Name LIKE '%".$jsonData['Name']."%'";
		

		
		//any other type, add filters
		if($jsonData['Type'] == 0 )
		{
			$middlePart = "
			UNION ALL
			"; //only for type = 0 - to show all results
		}
		else
		if( ($jsonData['Type'] > 0)&&($jsonData['Type'] < 3) )
		{
			//add type filtering for specific type, individual or business
			$newType = $jsonData['Type']  - 1;
			$taxPayerPart = $taxPayerPart." AND Type = ".$newType;
			$institutionPart = "";
		}
		else
		if( $jsonData['Type'] == 3 )
		{
			$taxPayerPart = "";
		}
		else
		{
			throw "Invalid type found!";
		}
		
		//combine parts together
		$innerPart = $taxPayerPart.$middlePart.$institutionPart;
					
		
		$request = $database->execSql(							 
		"SELECT *
		FROM (
			".$innerPart."
		) Combined ORDER BY Name LIMIT ".$jsonData['ItemsOffset'].", ".$jsonData['ItemsPerPage'] );
									 
		
		$rows = array();
		
		while($r = mysql_fetch_assoc($request)) {
			

			
			$childrensResult = $database->execSql("SELECT ID, Name, 
									Type FROM `TaxPayerTable` TaxPayer
									WHERE ParentID = ".$r["ID"]);
			
			
			$childrens = array();
			
			while($c = mysql_fetch_assoc($childrensResult)) {
				$childrens[] = $c;
			}
			
			$r["Children"] = $childrens;
			
			$rows[] = $r;
		}
		
		return $rows;
	}
	
	public function getTaxYearData( $database, $jsonData)
	{
		$result = $database->execSql("SELECT * FROM `TaxYearTable` WHERE Year=".$jsonData);
		
		$row = mysql_fetch_assoc($result);
				
		return $row;

	}
	
	public function login( $database, $jsonData)
	{
		$institutionPart = "SELECT ID, 3 AS Type FROM `InstitutionTable` WHERE Code = '".$jsonData['Code']."'";
			
		$taxPayerPart = "SELECT ID, (Type + 1) AS Type FROM `TaxPayerTable` WHERE Code = '".$jsonData['Code']."'";
					
		$sql = $institutionPart."
		UNION ALL
		".$taxPayerPart ;
		
		$result = $database->execSql($sql );
		
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		
		if( count($rows) != 1 )
		{
			//this is so wrong only one row
			throw new Exception("Invalid number of results found = ".count($rows));
		}
		
		return $rows;

	}
}


?>