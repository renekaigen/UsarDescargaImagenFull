<?php

$imageDataEncoded = base64_encode(file_get_contents('naruhina.jpg'));
$imageData = base64_decode($imageDataEncoded);
$source = imagecreatefromstring($imageData);
$angle = 90;
$rotate = imagerotate($source, $angle, 0); // if want to rotate the image
$imageName = "hello1.png";
$imageSave = imagejpeg($rotate,$imageName,100);
//imagedestroy($source);
?>