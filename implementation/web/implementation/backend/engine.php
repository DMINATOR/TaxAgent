<?php
include 'adminActions.php';
include 'institutionActions.php';
include 'taxPayerActions.php';

//LOG REQUEST
$log = new Logging();  
$log->logREQUEST();

$response = null; //no response
$errorCode = 'FAIL'; //by default it is fail



$rawData = json_decode( stripslashes( $_POST['json'] ), true );

//request is json encoded it consists of action and data details
$action = $rawData['action'];
$jsonData = $rawData['data'];

if( $jsonData == null )
{
	$response = 'JSON error: failed to decode JSON data';
}
else
{
	//JSON parsed OK:
	$log->logJSON($action, $jsonData);
	
	
	$adminTest = new AdminTest( &$log );
	
	$adminDatabase = new AdminDatabase();
	$adminDatabase->log = &$log;
	
	$adminActions = new AdminActions();
	$adminActions->log = &$log;
	
	$institutionActions = new InstitutionActions();
	$institutionActions->log = &$log;
	
	$taxPayerActions = new TaxPayerActions();
	$taxPayerActions->log = &$log;
		
	$errorCode = 'OK';
	$total = 1;
	
try
{
	switch( $action ) {
		
	/// --- ADMIN:
	
		// TEST:
		
		case 'Admin.TestJSON':
				$response = $adminTest->testJSON( $jsonData );
			break;
			
		case 'Admin.TestConnection':
				$response = $adminTest->testDatabase( $adminDatabase );
			break;
			
		case 'Admin.CreateTestData':
				$response = $adminTest->createTestData( $adminDatabase );
			break;
		

			
		// ADMIN actions:
		
		case 'Admin.ClearDatabase':
				$response = $adminActions->clearDatabase( $adminDatabase );
			break;
		
		case 'Admin.CreateWorld':
				$response = $adminActions->createWorld( $adminDatabase );
			break;	
		
		case 'Admin.GetDate':
				$response = $adminActions->getDate( $adminDatabase );
			break;	
		
		case 'Admin.ChangeDate':
				$response = $adminActions->changeDate( $adminDatabase, $jsonData );
			break;
			
		case 'Admin.TaxYearCreate':
				$response = $adminActions->createTaxYear( $adminDatabase, $jsonData );
			break;
			
		case 'Admin.TaxYearDelete':
				$response = $adminActions->deleteTaxYear( $adminDatabase, $jsonData );
			break;
			
		case 'Admin.TaxYearGrid':
				$response = $adminActions->getTaxYearGrid( $adminDatabase, $jsonData );
				$total = $adminActions->getTaxYearGridCount($adminDatabase);
			break;
			
		case 'Admin.SearchGrid':
				$response = $adminActions->searchGrid( $adminDatabase, $jsonData );
			break;
			
		case 'Admin.TaxYearShow':
				$response = $adminActions->getTaxYearData( $adminDatabase, $jsonData );
			break;
			
		case 'Admin.Login':
				$response = $adminActions->login(  $adminDatabase, $jsonData );
			break;
		
		
		// INSTITUTION ACTIONS:	
		case 'Institution.GetInfo':
				$response = $institutionActions->getInstitutionInfo( $adminDatabase, $jsonData );
			break;
		
		case 'Institution.GetTaxYearBudgetInfo':
				$response = $institutionActions->getTaxYearBudgetInfo( $adminDatabase, $jsonData );
			break;
			
		case 'Institution.TaxYearBudgetCreate':
				$response = $institutionActions->createTaxYearBudget( $adminDatabase, $jsonData );
			break;	
			
		case 'Institution.TaxYearBudgetGrid':
				$response = $institutionActions->taxYearBudgetGrid( $adminDatabase, $jsonData );
			break;
			
		case 'Institution.TaxProgramGrid':
				$response = $institutionActions->taxProgramGrid( $adminDatabase, $jsonData );
			break;	
			
		case 'Institution.TaxYearBudgetDelete':
				$response = $institutionActions->deleteTaxYearBudget( $adminDatabase, $jsonData );
			break;
			
		case 'Institution.TaxProgramChange':
				$response = $institutionActions->changeTaxProgram( $adminDatabase, $jsonData );
			break;
			
		case 'Institution.TaxProgramDelete':
				$response = $institutionActions->deleteTaxProgram( $adminDatabase, $jsonData );
			break;
		
		case 'Institution.TaxProgramGenerate':
				$response = $institutionActions->generateTaxProgram( $adminDatabase, $jsonData );
			break;
		
		case 'Institution.TaxYearBudgetConfirm':
				$response = $institutionActions->confirmTaxYearBudget( $adminDatabase, $jsonData );
			break;
			
		case 'Institution.GetTaxYearBudgetStats':
				$response = $institutionActions->getTaxYearBudgetStats( $adminDatabase, $jsonData );
			break;
		
		case 'Institution.GetTaxYearBudgetProgramsGrid':
				$response = $institutionActions->getTaxYearBudgetProgramsGrid( $adminDatabase, $jsonData );
			break;
			
		case 'Institution.GetTaxProgramUsersGrid':
				$response = $institutionActions->getTaxProgramUsersGrid( $adminDatabase, $jsonData );
			break;
			
		// TAX PAYER ACTIONS:
		
		case 'TaxPayer.GetInfo':
				$response = $taxPayerActions->getInfo( $adminDatabase, $jsonData );
			break;
			
		case 'TaxPayer.TaxYearGrid':
				$response = $taxPayerActions->taxYearGrid( $adminDatabase, $jsonData );
			break;
			
		case 'TaxPayer.TaxProgramStatsGrid':
				$response = $taxPayerActions->TaxProgramStatsGrid( $adminDatabase, $jsonData );
			break;
			
		case 'TaxPayer.TaxProgramGrid':
				$response = $taxPayerActions->TaxProgramGrid( $adminDatabase, $jsonData );
			break;
		
		// Tax Payer - agent commands:
		
		case 'TaxPayer.Agent.RequestProgramPrice':
				$response = $taxPayerActions->AgentRequestProgramPrice( $adminDatabase, $jsonData );
			break;
			
		case 'TaxPayer.Agent.RequestProgramLowerPrice':
				$response = $taxPayerActions->AgentRequestProgramLowerPrice( $adminDatabase, $jsonData );
			break;
			
		case 'TaxPayer.Agent.ApproveProgramPrice':
				$response = $taxPayerActions->AgentApproveProgramPrice( $adminDatabase, $jsonData );
			break;
			
		//new methods are going here...
			
		default:
			$errorCode = 'FAIL';
			$response = 'Unknown action: '.$action;
			break;
	}
}
catch(Exception $ex)
{
	$errorCode = 'FAIL';
	$response = 'Failed to call action: '.$ex;
}


}

//send back the response
$result = array( "result" => $errorCode, "data" => $response, "total" => $total );

$log->logRESPONSE( $action ,  $result);

//response is json encoded it consists of result and data
echo json_encode(  $result );

?>