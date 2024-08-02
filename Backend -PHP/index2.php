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

//Returning the profile Info
if(!empty($dToken) && empty($Dname) && empty($Dsurname) && empty($password) && empty($email) && empty($Dusername)){
   $response = $docObject->returnProfile($dToken);
} elseif (!empty($pToken)){
   $response = $patientObject->returnProfile($pToken);
} elseif (!empty($phToken)){
   $response = $pharmObject->returnProfile($phToken);
}

//Updating the profile info
elseif (!empty($Dname) && !empty($Dsurname) && !empty($password) && !empty($email) && !empty($dToken) && !empty($Dusername) ){
        $hashed_password = md5($password);
        $response = $docObject->editProfile($Dname, $Dsurname, $Dusername, $hashed_password, $email, $dToken);
} elseif(!empty($Pname) && !empty($Psurname) && !empty($password) && !empty($email) && !empty($pToken) && !empty($Pusername)){
        $hashed_password = md5($password);
        $response = $patientObject->editProfile($Pname, $Psurname, $Pusername, $hashed_password, $email, $pToken);
}
//Removin ordrs  had a confilict of parameters in index
elseif (!empty($medID) && !empty($password) && empty($mediNr)){     //Pharmacy removing of stock
        $response = $pharmObject->removeStock($medID);
}
//Patient Orders
elseif(!empty($Pusername)){
        $response = $patientObject->returnOrders($Pusername);
}
else {
    $response = ['success' => 0, 'message' => 'Invalid request parameters'];
}
echo json_encode($response);
?>
