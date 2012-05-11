<?php


/** 
 * Institution related actions go here
 */  
class InstitutionActions
{  
	public $log;
	
	public function getInstitutionInfo( $database, $jsonData)
	{
		$result = $database->execSql("SELECT * FROM `InstitutionTable` WHERE ID=".$jsonData['ID']);
		
		$row = mysql_fetch_assoc($result);
				
		return $row;
	}
	
	public function getTaxYearBudgetInfo( $database, $jsonData)
	{
		$result = $database->execSql("SELECT * FROM `InstitutionBudgetTable` WHERE ID=".$jsonData['ID']);
		
		$row = mysql_fetch_assoc($result);
				
		return $row;
	}
	
	public function createTaxYearBudget( $database, $jsonData)
	{
		$result = $database->execSql("INSERT INTO `InstitutionBudgetTable` (TaxYearID, InstitutionID, ConfirmedDate) VALUES ((SELECT ID from TaxYearTable where Year=".$jsonData['Year']."),".$jsonData['InstitutionID'].", NULL)");
						
		return 1;
	}
	
	public function taxYearBudgetGrid( $database, $jsonData)
	{
		
		$result = $database->execSql("select * from (SELECT bt.ID, y.Year, y.State, bt.ConfirmedDate FROM InstitutionBudgetTable bt, TaxYearTable y
WHERE bt.TaxYearID = y.ID and bt.InstitutionID =".$jsonData['instID']." and ConfirmedDate is not null
UNION
SELECT bt.ID, y.Year, y.State, bt.ConfirmedDate FROM InstitutionBudgetTable bt, TaxYearTable y
WHERE bt.TaxYearID = y.ID and bt.InstitutionID = ".$jsonData['instID']." and y.Year=Year((SELECT Date from SystemTable where ID=1)) and bt.ConfirmedDate is null)
RESULT LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
						
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;
	}
	
	public function taxProgramGrid( $database, $jsonData)
	{
		
		$result = $database->execSql("SELECT tp.ID, tp.Name, tp.PredictedBudget, tp.PredictedUsers, tp.BenefitsExpireInYears, tp.MinimumYears, tp.BenefitsApplyTo, tp.ExtendsProgramID, 
IF(ib.ConfirmedDate != null, (select sum(pp.CalculatedTaxAmount) from TaxPayerProgramTable pp where pp.TaxProgramID=tp.ID and pp.ConfirmedDate !=null) ,0) as ConfirmedBudget
 FROM TaxProgramTable tp, InstitutionBudgetTable ib
 WHERE tp.InstitutionBudgetID=ib.ID and  tp.InstitutionBudgetID =".$jsonData['ID'].
 " LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
						
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;
	}
	
	public function deleteTaxYearBudget( $database, $jsonData)
	{
		$result = $database->execSql("DELETE FROM `InstitutionBudgetTable` WHERE ID =".$jsonData." and ConfirmedDate is null");
						
		return 1;
	}
	
	public function changeTaxProgram( $database, $jsonData)
	{
		$extends = $jsonData['ExtendsProgramID'];
		
		$sql = "";
		
		if($jsonData['ID'] == NULL)
		{
			$sql = "INSERT INTO TaxProgramTable(InstitutionBudgetID, Name, PredictedBudget, PredictedUsers, 
			BenefitsExpireInYears, MinimumYears, BenefitsApplyTo";
			
			if( $extends != null )
			{
				$sql = $sql.", ExtendsProgramID";
			}
			
			$sql = $sql.")";
			
			$sql = $sql." VALUES (".$jsonData['InstitutionBudgetTable'].",'".
			$jsonData['Name']."',".$jsonData['PredictedBudget'].",".$jsonData['PredictedUsers'].",".$jsonData['BenefitsExpireInYears'].",".
			$jsonData['MinimumYears'].",".$jsonData['BenefitsApplyTo'];
			
			if( $extends != null )
			{	
				$sql = $sql.", ".$jsonData['ExtendsProgramID'];
			}
			
			$sql = $sql.")";
		}
		else
		{
			$sql = "UPDATE TaxProgramTable SET InstitutionBudgetID=".$jsonData['InstitutionBudgetTable'].",Name=".
			$jsonData['Name'].",PredictedBudget=".$jsonData['PredictedBudget'].",PredictedUsers=".$jsonData['PredictedUsers'].",BenefitsExpireInYears=".
			$jsonData['BenefitsExpireInYears'].",MinimumYears=".$jsonData['MinimumYears'].",BenefitsApplyTo="
			.$jsonData['BenefitsApplyTo'];
			
			if( $extends != null )
			{	
				$sql = $sql.",ExtendsProgramID=".$jsonData['ExtendsProgramID'];
			}
			
			$sql = $sql." WHERE ID=".$jsonData['ID'];
		}

		
		$result = $database->execSql( $sql );
						
		return 1;
	}

	public function deleteTaxProgram( $database, $jsonData)
	{
		$notConf = $database->execSql("SELECT count(*) from InstitutionBudgetTable where ID=(SELECT InstitutionBudgetID from TaxProgramTable where ID=".$jsonData.") and ConfirmedDate is null");
		
		$row = mysql_fetch_row($notConf);
		if($row[0]==1){
			$database->execSql("DELETE FROM TaxProgramTable WHERE ID=".$jsonData);
		}
						
		return 1;
	}
	
	public function generateTaxProgram( $database, $jsonData)
	{
		$result = $database->execSql("SELECT InstitutionID from InstitutionBudgetTable where ID=".$jsonData);
		$row = mysql_fetch_row($result);
		$instID = $row[0];
	
		$result = $database->execSql("SELECT count(*) from InstitutionBudgetTable where InstitutionID=".$instID." and ID!=".$jsonData);
		$row = mysql_fetch_row($result);
		if($row[0]!=0){
			$resultn = $database->execSql("SELECT max(TaxYearID) from InstitutionBudgetTable where InstitutionID=".$instID." and ID!=".$jsonData);
			$rown = mysql_fetch_row($resultn);
			$prevYearID =$rown[0];
			
			$result = $database->execSql("SELECT ID from InstitutionBudgetTable where InstitutionID=".$instID." and TaxYearID=".$prevYearID);
			$row = mysql_fetch_row($result);
			$prevBudgetID = $row[0];
			
			$result = $database->execSql("SELECT * from TaxProgramTable where InstitutionBudgetID=".$prevBudgetID);
			while($row = mysql_fetch_array($result)){

				$actBudgetRes = $database->execSql("select sum(CalculatedTaxAmount) from TaxPayerProgramTable where TaxProgramID=".$row['ID']." and ConfirmedDate is not null");
				$actBudgetArr = mysql_fetch_row($actBudgetRes);
				$prevActBudget = $actBudgetArr[0];
				$newPredBudget = ($prevActBudget+$row['PredictedBudget'])/2;
				
				$actUsersRes = $database->execSql("select sum(1) from TaxPayerProgramTable where TaxProgramID=".$row['ID']." and ConfirmedDate is not null");
				$actUsersArr = mysql_fetch_row($actUsersRes);
				$prevActUsers = $actBudgetArr[0];
				$newPredUsers = ($prevActUsers+$row['PredictedUsers'])/2;
				
				$database->execSql("INSERT INTO TaxProgramTable(InstitutionBudgetID, Name, BenefitsExpireInYears, MinimumYears, BenefitsApplyTo, ExtendsProgramID, PredictedBudget, PredictedUsers) 
			VALUES (".$jsonData.",'".$row['Name']."',".$row['BenefitsExpireInYears'].",".$row['MinimumYears'].",".$row['BenefitsApplyTo'].",".$row['ID'].",".$newPredBudget.",".$newPredUsers.")");
			}
		}
						
		return 1;
	}
	
	public function confirmTaxYearBudget( $database, $jsonData){
		
		$database->execSql("UPDATE InstitutionBudgetTable SET ConfirmedDate=(SELECT Date from SystemTable where ID = 1) WHERE ID =".$jsonData);
		
		return 1;
	}
	
	public function getTaxYearBudgetStats( $database, $jsonData){
		
		$result = $database->execSql("SELECT count(*) FROM InstitutionBudgetTable WHERE ID =".$jsonData." and ConfirmedDate is not null");
		$row = mysql_fetch_row($result);
		if($row[0]==1){
			$incomeApprRes = $database->execSql("SELECT SUM(pp.CalculatedTaxAmount) as IncomeApproved FROM TaxProgramTable tp, TaxPayerProgramTable pp
		WHERE tp.ID=pp.TaxProgramID and tp.InstitutionBudgetID=".$jsonData." and pp.ConfirmedDate is not null");
			$incomeApprArr = mysql_fetch_row($incomeApprRes);
			$incomeApproved = $incomeApprArr[0];
			
			$programsBudgetRes = $database->execSql("SELECT SUM(PredictedBudget) FROM TaxProgramTable WHERE InstitutionBudgetID=".$jsonData);
			$programsBudgetArr = mysql_fetch_row($programsBudgetRes);
			$programsBudget = $programsBudgetArr[0];
		
			return array(
    			"IncomeApproved" => $incomeApproved, 
    			"ProgramsBudget" => $programsBudget);
		}
		
	}
	
	public function getTaxYearBudgetProgramsGrid( $database, $jsonData){
		
		$result = $database->execSql("SELECT ID, Name, PredictedBudget, PredictedUsers, ExtendsProgramID, MinimumYears, BenefitsExpireInYears, BenefitsApplyTo
		FROM TaxProgramTable WHERE InstitutionBudgetID=".$jsonData['instBudgID'].
		" LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
						
		$rows = array();
		
		while($r = mysql_fetch_assoc($result)) {
			$rows[] = $r;
		}
		
		return $rows;
	}
	
	public function getTaxProgramUsersGrid( $database, $jsonData){
		
		$result = $database->execSql("SELECT p.ID, p.Name, p.Code FROM TaxProgramTable pr, TaxPayerProgramTable pp, TaxPayerTable p
WHERE pr.ID=pp.TaxProgramID and pp.TaxPayerID=p.ID and pr.InstitutionBudgetID=".$jsonData['ID']." and pp.ConfirmedDate is not null".
" GROUP BY p.ID, p.Name, p.Code LIMIT ".$jsonData['ItemsOffset']." , ".$jsonData['ItemsPerPage']);
			
		$rows = array();
		while($row = mysql_fetch_array($result)){
			$userID = $row['ID'];
			$userName = $row['Name'];
			$userCode = $row['Code'];
			
			$programs = array();
			$programsRows = $database->execSql("SELECT pr.Name FROM TaxPayerProgramTable pp, TaxProgramTable pr
			WHERE pp.TaxProgramID=pr.ID and pp.TaxPayerID=".$userID." and pp.ConfirmedDate is not null and pr.InstitutionBudgetID=".$jsonData['ID']);
			while($pr = mysql_fetch_array($programsRows)){
				$programs[]=$pr[0];
			}
			
			$benefitsAccRes = $database->execSql("SELECT SUM(pp.BenefitsAccumulated) FROM TaxPayerProgramTable pp, TaxProgramTable pr
			WHERE pp.TaxProgramID=pr.ID and pp.TaxPayerID=".$userID." and pp.ConfirmedDate is not null and pr.InstitutionBudgetID=".$jsonData['ID']);
			$benefitsAccArr = mysql_fetch_row($benefitsAccRes);
			$benefitsAcc = $benefitsAccArr[0];
			
			$calcTaxAmountRes = $database->execSql("SELECT SUM(pp.CalculatedTaxAmount) FROM TaxPayerProgramTable pp, TaxProgramTable pr
			WHERE pp.TaxProgramID=pr.ID and pp.TaxPayerID=".$userID." and pp.ConfirmedDate is not null and pr.InstitutionBudgetID=".$jsonData['ID']);
			$calcTaxAmountArr = mysql_fetch_row($calcTaxAmountRes);
			$calcTaxAmount = $calcTaxAmountArr[0];
			
			$arr= array("ID" => $userID,
			"Name" => $userName,
			"Code" => $userCode,
			"Programs" => $programs,
			"BenefitsAccumulated" => $benefitsAcc,
			"TotalPayment" => $calcTaxAmount);
			$rows[]=$arr;
		}
		return $rows;
	}
	
}

?>