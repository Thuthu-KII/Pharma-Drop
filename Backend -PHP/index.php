<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require_once 'patientLogin.php';
require_once 'docLogin.php';
require_once 'pharmLogin.php';

$Pusername = $_POST['pUsername'] ?? null;
$Dusername = $_POST['dUsername'] ?? null;
$PHusername = $_POST['phUsername'] ?? null;
$password = $_POST['Password'] ?? null;
$email = $_POST['Email'] ?? null;
$Pname = $_POST['pName'] ?? null;
$Dname = $_POST['dName'] ?? null;
$PHname = $_POST['phName'] ?? null;
$Psurname = $_POST['pSurname'] ?? null;
$Dsurname = $_POST['dSurname'] ?? null;
$pID = $_POST['pID'] ?? null;
$dID = $_POST['dID'] ?? null;
$docNr = $_POST['Doc_AccountNum'] ?? null;
$mediNr = $_POST['Medical_Aid_nr'] ?? null;
$mediCred = $_POST['Medic_Credentials'] ?? null;
$busNr = $_POST['Bus_Reg_Num'] ?? null;
$pToken = $_POST['pToken'] ?? null;
$dToken = $_POST['dToken'] ?? null;
$phToken = $_POST['phToken'] ?? null;
$medID = $_POST['medID'] ?? null;
$descrip = $_POST['description'] ?? null;
$count = $_POST['count'] ?? null;
$med_name = $_POST['med_name'] ?? null;


$patientObject = new Patient();
$docObject = new Doctor();
$pharmObject = new Pharmacy();

$response = array();

if (!empty($Pusername) && !empty($password) && !empty($pID) && !empty($Psurname) && !empty($email) && !empty($Pname) && !empty($docNr) && !empty($mediNr)) {
    $hashed_password = md5($password);
    $response = $patientObject->RegisterNewUser($Pname, $Psurname, $Pusername, $hashed_password, $email, $pID, $mediNr, $docNr);
} elseif (!empty($Dusername) && !empty($password) && !empty($mediCred) && !empty($Dsurname) && !empty($email) && !empty($Dname) && !empty($dID)) {
    $hashed_password = md5($password);
    $response = $docObject->RegisterNewUser($Dname, $Dsurname, $Dusername, $hashed_password, $email, $mediCred, $dID);
} elseif (!empty($PHusername) && !empty($password) && !empty($busNr) && !empty($email) && !empty($PHname)) {
    $hashed_password = md5($password);
    $response = $pharmObject->RegisterNewUser($PHname, $PHusername, $hashed_password, $email, $busNr);
} elseif (!empty($Pusername) && !empty($password) && empty($pID) && empty($Psurname) && empty($email) && empty($Pname) && empty($docNr) && empty($mediNr)) {
    $hashed_password = md5($password);
    $response = $patientObject->UserLogin($Pusername, $hashed_password);
} elseif (!empty($Pusername) && !empty($pToken) && empty($password) && empty($pID) && empty($Psurname) && empty($email) && empty($Pname) && empty($docNr) && empty($mediNr)) {
       $response = $patientObject->UserLogOut($Pusername, $pToken);
} elseif (!empty($Dusername) && !empty($password) && empty($mediCred) && empty($Dsurname) && empty($email) && empty($Dname)) {
    $hashed_password = md5($password);
    $response = $docObject->UserLogin($Dusername, $hashed_password);
} elseif (!empty($Dusername) && !empty($dToken) && empty($password) && empty($mediCred) && empty($Dsurname) && empty($email) && empty($Dname)) {
    $response = $docObject->userLogOut($Dusername, $dToken);
} elseif (!empty($PHusername) && !empty($password) && empty($busNr) && empty($email) && empty($PHname)) {
    $hashed_password = md5($password);
    $response = $pharmObject->UserLogin($PHusername, $hashed_password);
} elseif (!empty($PHusername) && !empty($phToken) && empty($password) && empty($busNr) && empty($email) && empty($PHname)) {
    $response = $pharmObject->userLogOut($PHusername, $phToken);
} elseif (!empty($Pname) && !empty($mediNr) && !empty($Dusername) && !empty($medID)){
//   $hashed_password = md5($password);
   $response = $docObject->addPrescription($Pname, $mediNr, $medID, $Dusername);    //Doctor add a prescip
} elseif (!empty($password) && empty($medID)){
    $response = $docObject->returnPrescrips();      //viewing the prescription tabl
} elseif ((!empty($dToken) || !empty($phToken)) && empty($dUsername) && empty($PHusername)){  //avail add
    $response = $docObject->returnStock();
} elseif(!empty($count) && !empty($med_name) && !empty($descrip) ){   //Pharmacy adding of stock
        $response = $pharmObject->addStock($med_name, $descrip, $count);
} elseif(!empty($medID) && !empty($count)){                           //Pharmacy update amount of stock available
        $response = $pharmObject->updateCount($medID, $count);
} elseif(!empty($Pusername)){
        $response = $patientObject->returnPrescrips($Pusername);
}
else {
    $response = ['success' => 0, 'message' => 'Invalid request parameters'];
}

echo json_encode($response);
?>