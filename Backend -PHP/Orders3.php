<?php
require_once 'pharmLogin.php';
$pharmObject = new Pharmacy();

$response = array();

$response = $pharmObject->returnOrders();

echo json_encode($response);

?>

