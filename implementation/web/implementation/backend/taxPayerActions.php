<?php

/** 
 *  Tax payer actions
 */  
class TaxPayerActions
{  
		
	public $log;
		
	public function getInfo( $database, $jsonData)
	{
		$result = $database->execSql("SELECT * FROM `TaxPayerTable` WHERE ID = ".$jsonData['ID'] );
		
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;

	}
	
	
	public function taxYearGrid( $database, $jsonData)
	{
		$result = $database->execSql("SELECT year.ID, year.Year, IF ( year.State = 1  , IF ( 
		(																						  
			SELECT Count(1) FROM TaxPayerProgramTable _tppt
			
			LEFT JOIN TaxProgramTable          _tpt ON _tpt.ID = _tppt.TaxProgramID
			LEFT JOIN InstitutionBudgetTable   _ib ON _ib.ID = _tpt.InstitutionBudgetID
			LEFT JOIN TaxYearTable             _year ON _year.ID = _ib.TaxYearID
			
			WHERE 
			_tppt.ConfirmedDate IS NOT NULL AND 
			_ib.ConfirmedDate IS NOT NULL AND 
			_tppt.TaxPayerID = ".$jsonData['ID']." AND
			_year.ID = year.ID
		) > 0  , 2 , year.State ) , year.State ) AS State, ib.ConfirmedDate

FROM TaxYearTable year

LEFT JOIN TaxPayerProgramTable tpt ON tpt.TaxPayerID = ".$jsonData['ID']." AND tpt.ConfirmedDate IS NOT NULL

LEFT JOIN TaxProgramTable tp ON tp.ID = tpt.TaxProgramID

LEFT JOIN InstitutionBudgetTable ib ON ib.ID = tp.InstitutionBudgetID AND ib.ConfirmedDate IS NOT NULL AND ib.TaxYearID = year.ID

WHERE year.ID = ib.TaxYearID OR year.Year = ( SELECT YEAR( Date ) FROM SystemTable WHERE ID = 1 )

GROUP BY year.ID ".
		" LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
		
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;

	}
	
	public function TaxProgramStatsGrid( $database, $jsonData)
	{
		$result = $database->execSql("SELECT  tp.ID, inst.Name AS 'Institution', tp.Name, tpt.YearsRequired AS 'YearsLeft', tp.MinimumYears, tpt.CalculatedTaxAmount

FROM TaxProgramTable tp

LEFT JOIN TaxPayerProgramTable tpt ON tpt.TaxPayerID = ".$jsonData['TaxPayerID']." AND tp.ID = tpt.TaxProgramID

LEFT JOIN InstitutionBudgetTable ib ON ib.ID = tp.InstitutionBudgetID AND ib.TaxYearID = ".$jsonData['ID']."

LEFT JOIN InstitutionTable inst ON inst.ID = ib.InstitutionID

WHERE tpt.ConfirmedDate IS NOT NULL AND ib.ConfirmedDate IS NOT NULL
".
		" LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
		
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;

	}
	
	
	public function TaxPayerGetCurrentYearPrograms( $TaxPayerID, $TaxYearID )
	{
			$result = $database->execSql("SELECT 
										 tpt.ID,inst.Name AS 'Institution', tpt.Name, tppt.YearsRequired, tpt.MinimumYears, tppt.CalculatedTaxAmount AS 'TaxRate' 
										 
										 FROM TaxPayerProgramTable tppt 
										 
										 LEFT JOIN TaxProgramTable tpt ON tpt.ID = tppt.TaxProgramID
										 LEFT JOIN InstitutionBudgetTable inb ON inb.ID = tpt.InstitutionBudgetID
										 LEFT JOIN InstitutionTable inst ON inst.ID = inb.InstitutionID
										 LEFT JOIN TaxYearTable year ON year.ID = inb.TaxYearID
										 WHERE TaxPayerID = ".$TaxPayerID." AND inb.ConfirmedDate IS NOT NULL AND year.Year = ".$TaxYearID  );
		
	
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;
	}
	
	public function TaxProgramGrid( $database, $jsonData)
	{
		$TaxPayerID = $jsonData['TaxPayerID'];
		$TaxYearID = $jsonData['ID'];
		
			//calculate proposed programs info
			
			//Return 
			// 1: programs for the selected Year id (showing those that are already selected by the tax payer) and 
			// 2: programs for the previous year (matching those as automatically selected)
			$result = $database->execSql(
	/*									 
										 "
										 
										 SELECT tpt.ID,inst.Name AS 'Institution', tpt.Name, 0 AS 'YearsLeft', tpt.MinimumYears, 0 AS 'TaxRate',
										 (SELECT Count(1) FROM TaxPayerProgramTable tppt WHERE tppt.TaxProgramID = tpt.ID AND tppt.TaxPayerID = ".$TaxPayerID.") AS 'IsSelected'
										 
										 FROM TaxProgramTable tpt
										 
										 LEFT JOIN InstitutionBudgetTable inb ON inb.ID = tpt.InstitutionBudgetID
										 LEFT JOIN InstitutionTable inst ON inst.ID = inb.InstitutionID
										 WHERE inb.ConfirmedDate IS NOT NULL AND inb.TaxYearID = ".$TaxYearID."
							".
							*/
			/*
										 
										 UNION ALL
										 
										 SELECT tpt.ID,inst.Name AS 'Institution', tpt.Name, tppt.YearsRequired AS 'YearsLeft', tpt.MinimumYears, tppt.CalculatedTaxAmount AS 'TaxRate', 2 AS 'IsSelected'
										 FROM TaxPayerProgramTable tppt 
										 
										 LEFT JOIN TaxProgramTable tpt ON tpt.ID = tppt.TaxProgramID
										 LEFT JOIN InstitutionBudgetTable inb ON inb.ID = tpt.InstitutionBudgetID
										 LEFT JOIN InstitutionTable inst ON inst.ID = inb.InstitutionID
										 
										 WHERE TaxPayerID = ".TaxPayerID." AND inb.ConfirmedDate IS NOT NULL AND inb.TaxYearID = 
										 (SELECT ID FROM TaxYearTable
										  WHERE ID < ".$TaxYearID."
										  ORDER BY ID DESC LIMIT 0,1)
										 ".*/
										 
		" SELECT tpt.ID,inst.Name AS 'Institution', tpt.Name, IF( tpptOld.ID IS NOT NULL , tpptOld.YearsRequired , 0 ) AS 'YearsLeft',
		tpt.MinimumYears, IF( tpptOld.ID IS NOT NULL , tpptOld.CalculatedTaxAmount , 0 ) AS 'TaxRate',
		
		IF( tppt.ID IS NOT NULL , IF( tpptOld.ID IS NOT NULL , 2 , 0 ), IF( tpptOld.ID IS NOT NULL , 2 , 0 ) ) AS 'IsSelected'

		FROM TaxProgramTable tpt

										 
		LEFT JOIN InstitutionBudgetTable inb ON inb.ID = tpt.InstitutionBudgetID
		LEFT JOIN InstitutionTable inst ON inst.ID = inb.InstitutionID
		LEFT JOIN TaxPayerProgramTable tppt ON tppt.TaxProgramID = tpt.ID AND tppt.TaxPayerID = ".$TaxPayerID."
		LEFT JOIN TaxPayerProgramTable tpptOld ON tpptOld.ID = tpt.ExtendsProgramID  AND tpptOld.TaxPayerID = ".$TaxPayerID."
		
		WHERE inb.ConfirmedDate IS NOT NULL AND inb.TaxYearID = ".$TaxYearID."
 ".
		" LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
		
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;

	}
	
	//Checks if tax payer has already confirmed this year
	public function IsTaxPayerYearConfirmed(  $database, $TaxPayerID, $TaxYearID  )
	{
		$sql = "SELECT Count(1) FROM TaxPayerProgramTable tppt
		
		LEFT JOIN TaxProgramTable          tpt ON tpt.ID = tppt.TaxProgramID
		LEFT JOIN InstitutionBudgetTable   ib ON ib.ID = tpt.InstitutionBudgetID
		LEFT JOIN TaxYearTable             year ON year.ID = ib.TaxYearID
		
		WHERE 
		tppt.ConfirmedDate IS NOT NULL AND 
		ib.ConfirmedDate IS NOT NULL AND 
		tppt.TaxPayerID = ". $TaxPayerID." AND
		year.ID = ".$TaxYearID ;
 
		$result = $database->execSql( $sql );
		$info = mysql_fetch_assoc($result);
		
		if( $info["Count(1)"] > 0 )
		{
			return true;
		}
		else
			return false;
	}
	
	// AGENT actions:
	
	public function AgentGetTaxYearInfo( $database, $TaxProgramID )
	{
		$this->log->logAgent("AgentProgramPrice - Retrieving program details. ID = ".$TaxProgramID);
		
		//get program info:
		$sql = "SELECT * 
		FROM TaxProgramTable tpt
		WHERE  ID = ".$TaxProgramID;
		
		$result = $database->execSql( $sql );
		$taxProgramInfo = mysql_fetch_assoc($result);
		
		return $taxProgramInfo;
	}
	
	public function AgentGetExtendsProgramID( $database, $ExtendsProgramID, $TaxProgramID  )
	{
		$this->log->logAgent("AgentProgramPrice - Retrieving last year program details. ID = ".$ExtendsProgramID);
			
		//get previous year program if it exists
		$lastYearTaxProgramInfo = NULL;
		
		//use original program price here
		if( $ExtendsProgramID != NULL )
		{
				$sql = "SELECT tppt.* , tpt.MinimumYears
				FROM TaxPayerProgramTable tppt
				LEFT JOIN TaxProgramTable tpt ON tpt.ID = tppt.TaxProgramID
				WHERE  tpt.ID = ".$ExtendsProgramID." AND tppt.ConfirmedDate IS NOT NULL";
				
				$result = $database->execSql( $sql );
				$lastYearTaxProgramInfo = mysql_fetch_assoc($result);
		
		}
		/*
		else
		{
			//try to find if this program is from previous years
				$sql = "SELECT tppt.* , tpt.MinimumYears
				FROM TaxProgramTable tpt
				LEFT JOIN TaxPayerProgramTable tppt ON tppt.TaxProgramID = tpt.ID
				LEFT JOIN InstitutionBudgetTable inst ON inst.ID = tpt.InstitutionBudgetID
				LEFT JOIN TaxYearTable year ON year.ID = inst.TaxYearID
				WHERE  tpt.ID = ".$TaxProgramID ." AND tppt.ConfirmedDate IS NOT NULL AND year.Year <> ( SELECT YEAR( Date ) FROM SystemTable WHERE ID = 1 )";

				$result = $database->execSql( $sql );
				$lastYearTaxProgramInfo = mysql_fetch_assoc($result);
		}*/
		
		return $lastYearTaxProgramInfo ;
	}
	
	public function AgentRequestProgramPrice( $database, $jsonData)
	{
		$TaxPayerID = $jsonData['TaxPayerID'];
		$TaxYearID = $jsonData['ID'];
		$TaxProgramID = $jsonData['ProgramID'];
		
		$response = array();
		
		$responseTaxAmount = 0;
		$responseOfferType = 0;
		
		if( $this->IsTaxPayerYearConfirmed(  $database, $TaxPayerID, $TaxYearID  ) )
		{
			throw new Exception("Tax year is already confirmed!");
		}
		
		$taxProgramInfo = $this->AgentGetTaxYearInfo( $database, $TaxProgramID );
		
		$lastYearTaxProgramInfo = $this->AgentGetExtendsProgramID( $database, $taxProgramInfo["ExtendsProgramID"], $TaxProgramID );
		
	
		if( $lastYearTaxProgramInfo != NULL )
		{
			//Last year program exists - get payment details
			$this->log->logAgent("AgentRequestProgramPrice - Last year program exist. ID = ".$lastYearTaxProgramInfo["ID"]);
		
			$responseTaxAmount = $lastYearTaxProgramInfo["CalculatedTaxAmount"];
			
			if( $lastYearTaxProgramInfo["YearsRequired"] == $lastYearTaxProgramInfo["MinimumYears"] )
			{
				//this means that it is our second year, and price need  to be lower in this case !
				$responseTaxAmount = $responseTaxAmount  / 2;
			}
			
			//Make sure this is final offer
			$responseOfferType  = 1;
		}
		else
		{
			//a new program for this year - calculate the price now
			$this->log->logAgent("AgentRequestProgramPrice - New program for Tax payer, calculating price. ID = ".$TaxProgramID);
				
			$predictedUsers = $taxProgramInfo["PredictedUsers"];
			$predictedBudget = $taxProgramInfo["PredictedBudget"];

			
			//calculate average rate per user:
			$averageRate = (float)$predictedBudget / (float)$predictedUsers;
			
			//add additional 100% for first year, and addtional 50%
			$val1 = 2.5;
			$val2 = $averageRate ;
			
			$averageRate = $val1 * $val2;//((float)$averageRate * (float)1.5);
			
			$this->log->logAgent("AgentRequestProgramPrice - Details: users = ".$predictedUsers." Budget = ".$predictedBudget ." Rate = ".$averageRate);
			
			//return back the result:
			$responseTaxAmount = $averageRate;
		}
		
		//return result:
		return array(
    			"ID" => $TaxProgramID, 
				"Price" => $responseTaxAmount,
				"Type" => $responseOfferType
				);
	}
	
	
	public function AgentRequestProgramLowerPrice( $database, $jsonData)
	{
		$TaxPayerID = $jsonData['TaxPayerID'];
		$TaxYearID = $jsonData['ID'];
		$TaxProgramID = $jsonData['ProgramID'];
		$ProposedPrice = $jsonData['ProposedPrice'];
		
		$response = array();
		
		$responseTaxAmount = 0;
		$responseOfferType = 0;
		
		if( $this->IsTaxPayerYearConfirmed(  $database, $TaxPayerID, $TaxYearID  ) )
		{
			throw new Exception("Tax year is already confirmed!");
		}
		
		$taxProgramInfo = $this->AgentGetTaxYearInfo( $database, $TaxProgramID );
		
		$lastYearTaxProgramInfo = $this->AgentGetExtendsProgramID( $database, $taxProgramInfo["ExtendsProgramID"], $TaxProgramID );
		
		if( $lastYearTaxProgramInfo != NULL )
		{
			throw new Exception("Cannot lower prices for existing program ID = ".$TaxProgramID);
		}
		else
		{
			//try to check if the price can be lowered
			//a new program for this year - calculate the price now
			$this->log->logAgent("AgentRequestProgramLowerPrice - Trying to lower Tax payer price ".$ProposedPrice." ID = ".$TaxProgramID);
			
			$predictedUsers = $taxProgramInfo["PredictedUsers"];
			$predictedBudget = $taxProgramInfo["PredictedBudget"];

			
			//calculate average rate per user:
			$minimumRate = (float)$predictedBudget / (float)$predictedUsers;
			
		
			$val1 = 1.25; //as long as it is it is over 125% for first year, 75% for next year, then it is acceptable
			$val2 = $minimumRate ;
			
			$avrgRate = 2.5 * $val2;
			
			$minimumRate = $val1 * $val2;
			
			
			
			$this->log->logAgent("AgentRequestProgramPrice - Details: users = ".$predictedUsers." Budget = ".$predictedBudget ." Minimum Rate = ".$minimumRate." AvrgRate = ".$avrgRate);
			
			if( $minimumRate < $ProposedPrice)
			{
				//This is fine !
				$responseTaxAmount = $ProposedPrice;
			}
			else
			{
				//lower the avrg rate a little bit
				$responseTaxAmount = $avrgRate * 0.5 + ( $avrgRate - ($minimumRate - $ProposedPrice)) * 0.5;
				
				if( $responseTaxAmount < $minimumRate )
				{
					//we cannot go any lower than that, make it final offer!
					$responseOfferType = 1;
				}
			}
			
		}
		
		//return result:
		return array(
    			"ID" => $TaxProgramID, 
				"Price" => $responseTaxAmount,
				"Type" => $responseOfferType
				);
		
	}
	

		
	public function AgentApproveProgramPrice( $database, $jsonData)
	{
		$TaxPayerID = $jsonData['TaxPayerID'];
		$TaxYearID = $jsonData['ID'];
		$TaxProgramID = $jsonData['ProgramID'];
		$ProposedPrice = $jsonData['ProposedPrice'];
		
		//find if there is already such program
		$taxProgramInfo = $this->AgentGetTaxYearInfo( $database, $TaxProgramID );
		
		$lastYearTaxProgramInfo = $this->AgentGetExtendsProgramID( $database, $ExtendsProgramID, $TaxProgramID );
		
		//Start with years
		$YearsRequired = $taxProgramInfo["MinimumYears"];
		
		//program exists at previous year
		if( $lastYearTaxProgramInfo != NULL )
		{
			$YearsRequired = $lastYearTaxProgramInfo["YearsRequired"];
			
			//take program details from last year
			if( $YearsRequired <= 0 )
			{
				//use the new values
				$YearsRequired = $taxProgramInfo["MinimumYears"];
			}
			else
			{
				//decrease current years:
				$YearsRequired = $YearsRequired - 1;
			}
			
		}
		else
		{
			//new program
		}
		
		$result = $database->execSql("INSERT INTO `TaxPayerProgramTable` (TaxPayerID, TaxProgramID, YearsRequired, CalculatedTaxAmount, BenefitsAccumulated, ConfirmedDate) VALUES (".$TaxPayerID.",".$TaxProgramID.",".$YearsRequired.",".$ProposedPrice.", 0 , (SELECT Date from SystemTable where ID = 1)) ");
						
		return 1;
	}
}

?>