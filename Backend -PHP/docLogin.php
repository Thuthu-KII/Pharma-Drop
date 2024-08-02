<?php
include_once 'DBConnect.php';

class Doctor {
    private $Database = null;
    private $DB_Table = "Doctors";

    public function __construct() {
        $this->Database = new DB_Connect();
    }

    public function ExistsUname($username) {
        $query = "SELECT * FROM " . $this->DB_Table . " WHERE Username = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }

    public function ExistsEmail($email) {
        $query = "SELECT * FROM " . $this->DB_Table . " WHERE Email = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }

    public function ValidityEmail($email) {
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }

    public function ExistsCred($cred) {
        $query = "SELECT * FROM " . $this->DB_Table . " WHERE Medic_Credentials = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);

 $stmt->bind_param("s", $cred);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }
        public function ExistsID($ID) {
        $query = "SELECT * FROM " . $this->DB_Table . " WHERE ID = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("s", $ID);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }



    public function RegisterNewUser($name, $surname, $username, $password, $email, $mediCred, $ID) {
        $existUname = $this->ExistsUname($username);
        $existsEmail = $this->ExistsEmail($email);
        $existsCred = $this->ExistsCred($mediCred);
        $existsID = $this->ExistsID($ID);
        $json = array();

        if ($existUname || $existsEmail || $existsID || $existsCred) {
            $json['success'] = 0;
            $json['message'] = "Error in registering. Username/Email/Id already exist";
        } else {
            $emailValid = $this->ValidityEmail($email);
            if ($emailValid) {
                $query = "INSERT INTO " . $this->DB_Table . " (Medic_Credentials, Email, Name, Surname, Username, Password, ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
                $stmt = $this->Database->getDataBase()->prepare($query);
                $stmt->bind_param("sssssss", $mediCred, $email, $name, $surname, $username, $password, $ID);
                if ($stmt->execute()) {
                    $json['success'] = 1;
                    $json['message'] = "Registration was successful";
                } else {
	  $json['success'] = 0;
                $json['message'] = "Error in registration. Invalid email";
            }
        }
        return $json;
    }

    public function isLoginExist($username, $password) {
        $query = "SELECT * FROM " . $this->DB_Table . " WHERE Username = ? AND Password = ? LIMIT 1";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("ss", $username, $password);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }

    public function UserLogin($username, $password) {
        $json = array();
        $ExistAcc = $this->isLoginExist($username, $password);

        if ($ExistAcc) {
            try {
                $apiKey = bin2hex(random_bytes(32));
            } catch (Exception $e) {
                $apiKey = bin2hex(uniqid($username, true));
            }

            $query = "UPDATE " . $this->DB_Table . " SET apiKey = ? WHERE Username = ?";
            $stmt = $this->Database->getDataBase()->prepare($query);
            $stmt->bind_param("ss", $apiKey, $username);

            if ($stmt->execute()) {
                $json['success'] = 1;
                $json['apiKey'] = $apiKey;
                $json['message'] = "Successful Login";
            } else {
                $json['success'] = 0;
                $json['message'] = "Unsuccessful Login";
  }
            $stmt->close();
        } else {
            $json['success'] = 0;
            $json['message'] = "No user matching supplied credentials";
        }
        return $json;
    }

    public function userLogOut($username, $token) {
        $query = "SELECT * FROM " . $this->DB_Table . " WHERE Username = ? AND apiKey = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("ss", $username, $token);
        $stmt->execute();
        $result = $stmt->get_result();
        $json = array();

        if ($result->num_rows != 0) {
            $queryUpdate = "UPDATE " . $this->DB_Table . " SET apiKey = '' WHERE Username = ?";
            $stmtUpdate = $this->Database->getDataBase()->prepare($queryUpdate);
            $stmtUpdate->bind_param("s", $username);
            if ($stmtUpdate->execute()) {
                $json['success'] = 1;
                $json['message'] = "Logout Successful !";
            } else {
                $json['success'] = 0;
                $json['message'] = "Logout Failed";
            }
            $stmtUpdate->close();
        } else {
             $json['success'] = 0;
             $json['message'] = "Logout Failed, Check Username.";
        }
        $stmt->close();
        return $json;
    }
        public function addPrescription($Pname, $mediNr, $med_id, $duname) {
        $query = "SELECT * FROM Patients WHERE Name = ? AND Medical_Aid_nr = ? Limit 1";
        $stmt = $this->Database->getDataBase()->prepare($query);

  $stmt->bind_param("ss", $Pname, $mediNr);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $patAc = $row['Pat_AccountNum'];
        } else {  //in case there is patient is not found in the database
            $stmt->close();
            return array('success' => 0, 'message' => 'Patient not found');
        }

        $query = "SELECT * FROM ". $this->DB_Table." WHERE Username = ? Limit 1";
        $stmt = $this->Database->getDataBase()->prepare($query);

        $stmt->bind_param("s", $duname);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $docAc = $row['Doc_AccountNum'];
        }

        $query = "SELECT * FROM Medications WHERE Med_ID = ? Limit 1";
        $stmt = $this->Database->getDataBase()->prepare($query);

        $stmt->bind_param("s", $med_id);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $med = $row['MedName'];
        }
        $insertQuery = "INSERT INTO Prescriptions (Pat_AccountNum, Doc_AccountNum, Med_ID, DatePrescribed) VALUES (?, ?, ?,?)";
        $insertQuery2 = "INSERT INTO Orders (Order_Date, Pat_AccountNum, OrderStatus, Medicine) VALUES (?, ?, ?,?)";
        $insertStmt = $this->Database->getDataBase()->prepare($insertQuery);
        $insertStmt2 = $this->Database->getDataBase()->prepare($insertQuery2);
	 $date = "".date("Y-m-d");
        $zero = 0;
        $insertStmt->bind_param("ssss", $patAc, $docAc, $med_id, $date);
        $insertStmt2->bind_param("ssss", $date, $patAc, $zero, $med);
        if ($insertStmt->execute() && $insertStmt2->execute()) {
            $stmt->close();
            $insertStmt->close();
            $insertStmt2->close();
            return array('success' => 1, 'message' => 'Prescription added successfully and Order Placed');
        } else {
            $stmt->close();
            $insertStmt->close();
            $insertStmt2->close();
            return array('success' => 0, 'message' => 'Failed to add prescription');
        }

    }

        public function returnStock(){
        $output = array();
        if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM Medications")){
                while ($row = $result->fetch_assoc()){
                        $output[] = $row;
                }
        }

        return $output;
        }


        public function returnPrescrips(){
        $output = array();
        if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM Prescriptions")){
                while ($row = $result->fetch_assoc()){
                        $output[] = $row;
                }
        }

        return $output;
        }

 public function returnPrescrips(){
        $output = array();
        if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM Prescriptions")){
                while ($row = $result->fetch_assoc()){
                        $output[] = $row;
                }

        }

        return $output;
        }

        public function returnProfile($token){
        $output = array();
        if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM ".$this->DB_Table." WHERE apiKey = '".$token."'")){
                while ($row = $result->fetch_assoc()){
                        $output[] = $row;
                }
        }
        return $output;
        }

        public function editProfile($name, $lname, $username, $password,$email, $token){
        $updateQuery = "UPDATE ".$this->DB_Table." SET Email = ? , Name = ? , Surname = ? ,Username = ?, Password = ?  WHERE apiKey = ?";
        $updateStmt = $this->Database->getDataBase()->prepare($updateQuery);
        $updateStmt->bind_param("ssssss", $email, $name, $lname, $username, $password, $token);
        if($updateStmt->execute()){
           $updateStmt->close();
           return array('success' => 1, 'message' => 'Profile succesfully updated');
        }else{
           $updateStmt->close();
           return array('success' => 0, 'message' => 'Profile update unsuccessful');
        }
        }

}
?>
