<?php 
error_reporting(E_ERROR | E_PARSE); //quitarme los deprecated
$imageDataEncoded = base64_encode(file_get_contents('../naruhina.jpg'));

$RB = new SoapClient('http://localhost/decodeImagen/ws/index.php?wsdl');

 		try
		{	 
		$json=$RB->SubirImagenNoWrap($imageDataEncoded); 
		}catch (SoapFault $fault){ 
			var_dump($fault);
		}	
		catch(Exception $e){
			echo 'bbbb';
		  // handle PHP issues with the request
		}
		   print_r($json);	
			exit();

?>