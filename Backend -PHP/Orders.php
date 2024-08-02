<?php
require_once 'pharmLogin.php';
$pharmObject = new Pharmacy();

$response = array();

$response = $pharmObject->updateOrders();

echo json_encode($response);

?>