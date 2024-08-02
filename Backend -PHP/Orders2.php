<?php
require_once 'pharmLogin.php';
$pharmObject = new Pharmacy();

$response = array();

$response = $pharmObject->returnPendingOrders();

echo json_encode($response);

?>
