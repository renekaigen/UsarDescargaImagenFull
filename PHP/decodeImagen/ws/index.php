<?php
//error_reporting(0);
error_reporting(E_ERROR | E_PARSE); //quitarme los deprecated
require_once ('class.nusoap.php');
require_once("../conexiones/conexion.phtml");


$ns = "http://localhost/decodeImagen/ws/nusoap/";
$server = new soap_server ();
function SubirImagen($imageDataEncoded) {
	$imageData = base64_decode($imageDataEncoded);
	$source = imagecreatefromstring($imageData);
	$angle = 0;
	$rotate = imagerotate($source, $angle, 0); // if want to rotate the image
	$imageName = "imagen_subida_naruhina.jpg";
	$imageSave = imagejpeg($rotate,$imageName,100);
	$respuesta='{ "success" : "1" }';
	return $respuesta;
}

function SubirImagenNoWrap($imageDataEncoded) {
	$link=Conectarse();
	$imageName = "imagen2.jpg";
	$FichaData =base64_decode($imageDataEncoded);//base64_decode(trim(addslashes(mysql_real_escape_string($imageDataEncoded))));
	file_put_contents($imageName, $FichaData);

	//save the base64 
	 $query='insert into imagen (baseimagen) values ("'.$imageDataEncoded.'")';
					$sql=mysql_query($query,$link);
					if(!$sql){
						$respuesta='{ "success" : "0", "respuesta" : "error en query" }';	
					}
					else{
						$respuesta='{ "success" : "1" }';
					}

	//$respuesta='{ "success" : "1" }';
	return $respuesta;
}

function getBaseImageWeb(){
	$link=Conectarse();
	 $query='select baseimagen from imagen order by idimagen DESC limit 1';
					$sql=mysql_query($query,$link);
					if(!$sql){
						$respuesta='{ "success" : "0", "respuesta" : "error en query" }';	
					}
					else{
						$row=mysql_fetch_assoc($sql);
						$respuesta=$row['baseimagen'];
						//$respuesta='{ "success" : "1" }';
					}
					return $respuesta;
}

$server->configurewsdl ( 'ApplicationServices', $ns );
$server->wsdl->schematargetnamespace = $ns;

$server->register ( 'SubirImagen',array ('cadena' => 'xsd:string' ), array ('resultado' => 'xsd:string' ), $ns );
$server->register ( 'SubirImagenNoWrap',array ('cadena' => 'xsd:string' ), array ('resultado' => 'xsd:string' ), $ns );
$server->register ( 'getBaseImageWeb',array ('cadena' => 'xsd:string' ), array ('resultado' => 'xsd:string' ), $ns );


if (isset ( $HTTP_RAW_POST_DATA )) {
	$input = $HTTP_RAW_POST_DATA;
} else {
	$input = implode ( "\r\n", file ( 'php://input' ) );
}

$server->service ( $input );
exit ();
?> 