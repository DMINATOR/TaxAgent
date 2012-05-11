<?php

include 'adminDatabase.php';

/** 
 * Admin test, used for test operations only by Admin
 */  
class AdminTest
{  
	//use logger if needed
	private $logger;
	
	function __construct($loggerx ) 
	{
		$this->logger = $loggerx; 
	}

	
	
	//tests json input and creates and returns back json output
	public function testJSON( $jsonData )
	{
		
		//possible option when associative arrays are used
		
		return array(
    			"number" => $jsonData['number'], 
    			"text" => $jsonData['text'], 
				"bool" => $jsonData['bool'], 
				"date" => gmdate("Y-m-d\TH:i:s\Z", strtotime( $jsonData['date']." UTC" ) ) , //no better option to use there is no JS standard, ms not supported
				"array" =>  array( $jsonData['array'][0] , $jsonData['array'][1] , $jsonData['array'][2] ), 
				"objArray" =>  array( 
									  	array(
											  	"name" 		=> $jsonData['objArray'][0]['name'],
												"age" 		=> $jsonData['objArray'][0]['age'],
												"relation" 	=> $jsonData['objArray'][0]['relation']
											  ),
										
										array(
											  	"name" 		=> $jsonData['objArray'][1]['name'],
												"age" 		=> $jsonData['objArray'][1]['age'],
												"relation" 	=> $jsonData['objArray'][1]['relation']
											  )
									 )
				);
		
		
		//default php json decoding option
		/*
		return array(
    			"number" => $jsonData->number, 
    			"text" => $jsonData->text, 
				"bool" => $jsonData->bool, 
				"array" =>  array( $jsonData->array[0] , $jsonData->array[1] , $jsonData->array[2] ), 
				"objArray" =>  array( 
									  	array(
											  	"name" 		=> $jsonData->objArray[0]->name,
												"age" 		=> $jsonData->objArray[0]->age,
												"relation" 	=> $jsonData->objArray[0]->relation
											  ),
										
										array(
											  	"name" 		=> $jsonData->objArray[1]->name,
												"age" 		=> $jsonData->objArray[1]->age,
												"relation" 	=> $jsonData->objArray[1]->relation
											  )
									 )
				);*/
	}
	
	
	public function testDatabase( $database )
	{

			
		//create script:
		$createTest_table = "CREATE TABLE IF NOT EXISTS `test_table` (
							  `id` int(11) NOT NULL AUTO_INCREMENT,
							  `test` varchar(255) NOT NULL,
							  PRIMARY KEY (`id`)
							) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=22 ;";
		
		$database->execSql( $createTest_table );
						   
		//insert results to table
		$database->execSql("INSERT INTO `test_table` VALUES ('', 'dbtest');");
		
		//drop table
		$database->execSql("DROP TABLE `test_table`");
		
		return 1;
	}
	
	public function createTestData( $database )
	{
		$this->logger->logSQL("Creating test data");
		
		$startYear = 1920;
		
		$lastYearID = 92;

		for ($i=1; $i <= $lastYearID; $i++){
			$state = 3;
			$year = ($startYear+$i);
			
			$database->execSql("insert into TaxYearTable (Year, SubmitDate, StartDate, EndDate, State) VALUES ('".$year."','".$year."-04-26','".$year."-04-21','".$year."-02-21','".$state."');");
		}
		
		$database->execSql("UPDATE TaxYearTable SET State = 1 WHERE `TaxYearTable`.`ID` = 92;");
		
		$database->execSql("UPDATE TaxPayerTable SET Income = '2605' WHERE `TaxPayerTable`.`ID` =12;");
		
		//Insert institution budget for 2011
		$database->execSql("insert into InstitutionBudgetTable (TaxYearID, InstitutionID, ConfirmedDate) VALUES (91,4,'2011-01-21');");
		$database->execSql("insert into InstitutionBudgetTable (TaxYearID, InstitutionID, ConfirmedDate) VALUES (91,1,'2011-01-21');");
		$database->execSql("insert into InstitutionBudgetTable (TaxYearID, InstitutionID, ConfirmedDate) VALUES (91,2,'2011-01-21');");
		$database->execSql("insert into InstitutionBudgetTable (TaxYearID, InstitutionID, ConfirmedDate) VALUES (91,3,'2011-01-21');");
		
		//budget for this year
		$database->execSql("insert into InstitutionBudgetTable (TaxYearID, InstitutionID, ConfirmedDate) VALUES (".$lastYearID.",4,'2012-01-21');");
			
		//Insert programs 1:
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES  (NULL, 'Free Stuff 1', '800000', '15000', '1', NULL, '5', '5', '0');");
		
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Free Stuff 2', '400000', '15000', '1', NULL, '15', '15', '0');");
				
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Free Stuff 3', '600000', '15000', '1', NULL, '2', '3', '1');");
		
		
		//Insert programs 2:
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Free Everything 1', '1400000', '15000', '2', NULL, '5', '5', '0');");
		
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Free Everything 2', '1200000', '15000', '2', NULL, '15', '15', '0');");
				
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Free Everything 3', '700000', '15000', '2', NULL, '2', '3', '1');");
		
		//Insert programs for this year:
		
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Moonwalk 1', '800000', '15000', '5', NULL, '5', '5', '0');");
		
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Free Everything 2', '500000', '15000', '5', '5', '15', '15', '0');");
				
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Nothing', '10000', '1500000', '5', NULL, '1', '1', '1');");
		
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Free Stuff 1', '500000', '15000', '5', '1', '5', '5', '0');");
			
		//Some new programs to choose from
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES  (NULL, 'Million dollars', '800000', '15000', '5', NULL, '5', '5', '0');");
		
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Cable TV', '400000', '15000', '5', NULL, '15', '15', '0');");
				
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Music', '600000', '15000', '5', NULL, '2', '3', '1');");
		
		
		//Insert programs 2:
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Phone', '1400000', '15000', '5', NULL, '5', '5', '0');");
		
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Nothing special', '1200000', '15000', '5', NULL, '15', '15', '0');");
				
		$database->execSql("INSERT INTO TaxProgramTable (`ID`, `Name`, `PredictedBudget`, `PredictedUsers`, `InstitutionBudgetID`, `ExtendsProgramID`, `MinimumYears`, `BenefitsExpireInYears`, `BenefitsApplyTo`) VALUES (NULL, 'Problem', '700000', '15000', '5', NULL, '2', '3', '1');");
		
		//Few programs selected by 
		
		
		$database->execSql("INSERT INTO `TaxPayerProgramTable` (`ID`, `TaxPayerID`, `TaxProgramID`, `YearsRequired`, `CalculatedTaxAmount`, `BenefitsAccumulated`, `ConfirmedDate`) VALUES (NULL, '12', '1', '5', '120', '0', '2012-05-03');" );
		
				
		$database->execSql("INSERT INTO `TaxPayerProgramTable` (`ID`, `TaxPayerID`, `TaxProgramID`, `YearsRequired`, `CalculatedTaxAmount`, `BenefitsAccumulated`, `ConfirmedDate`) VALUES (NULL, '12', '2', '10', '231', '0', '2012-05-03');" );
		
		
		$database->execSql("INSERT INTO `TaxPayerProgramTable` (`ID`, `TaxPayerID`, `TaxProgramID`, `YearsRequired`, `CalculatedTaxAmount`, `BenefitsAccumulated`, `ConfirmedDate`) VALUES (NULL, '12', '3', '2', '89', '0', '2012-05-03');" );
		
		$database->execSql("INSERT INTO `TaxPayerProgramTable` (`ID`, `TaxPayerID`, `TaxProgramID`, `YearsRequired`, `CalculatedTaxAmount`, `BenefitsAccumulated`, `ConfirmedDate`) VALUES (NULL, '12', '4', '10', '110', '0', '2012-06-03');" );
			
		$database->execSql("INSERT INTO `TaxPayerProgramTable` (`ID`, `TaxPayerID`, `TaxProgramID`, `YearsRequired`, `CalculatedTaxAmount`, `BenefitsAccumulated`, `ConfirmedDate`) VALUES (NULL, '12', '5', '10', '78', '0', '2012-02-02');" );
		//Add any data with SQL scripts that you need here...
		
		return 1;
	}
}

?>