<?php


/** 
 * Logging class: 
 * - contains lopen and lwrite methods 
 * - lwrite will write message to the log file 
 * - first call of the lwrite will open log file implicitly 
 * - message is written with the following format: hh:mm:ss (script name) message 
 *
 * Original file: http://www.redips.net/php/write-to-log-file/
 */  
class Logging
{  

  //turn this to either true or false
  //to disable/enable log
  private $use_log = true;
  

  // define log file  
  // DONT FORGET TO CREATE TEMP DIRECTORY !
  private $log_file = 'tmp/logfile';  
  
  // define file pointer  
  private $fp = null;  
  
  
  // write message to the log file  
  public function lwrite($message)
  {  
    //log disabled - bye bye
    if( !$this->use_log ) return;
  
    // if file pointer doesn't exist, then open log file  
    if (!$this->fp) $this->lopen();  

	
    // define script name  
    $script_name = pathinfo($_SERVER['PHP_SELF'], PATHINFO_FILENAME);  
	
    // define current time  
    $time = date('H:i:s');  
    // write current time, script name and message to the log file  
    $fwr = fwrite($this->fp, "$time ($script_name) $message\n");  
	

 	if ($fwr === FALSE) 
	{
        throw new Exception("Failed to write to $lfile with $fwr");
    }
	
	
	if( fclose($this->fp) === FALSE) 
	{
        throw new Exception("Failed to close file $lfile ");
    }
	$this->fp = null;
	
  }  
  
  //logs all the request params
  public function logREQUEST()
  {
	  	ob_start();
  		var_dump($_REQUEST);
  		$dump = ob_get_clean();
  
	  	$this->lwrite('[REQUEST]<-'.$dump);
  }
  
  //retrieves formatted array
  public function getArrayAsString($inputArray)
  {
	  	ob_start();
  		var_dump($inputArray);
  		$dump = ob_get_clean();
		
		return $dump;
  }
  
  /*
  * Writes error message to log and outputs error message back to client
  */
  public function logException($message)
  {
	  $this->lwrite('[ERROR]<-'.$message);
	  
	  //and throw exception here!
	  throw new Exception($message);
  }

  
  // open log file  
  private function lopen()
  {  
  
    // define the current date (it will be appended to the log file name)  
    $today = date('Y-m-d');  
	
    // define log file path and name  
    $lfile = $this->log_file. '_' . $today . '.txt'; 
	
	
    // open log file for writing only; place the file pointer at the end of the file  
    // if the file does not exist, attempt to create it  
	if (($this->fp = fopen($lfile , 'a')) === FALSE) 
	{
		throw new Exception("Failed to write to $lfile !");  
	}
  }  
  
  
}  


?>