<?php
include_once 'DBConnect.php';

class Patient {
    private $Database;
    private $DB_Table = "Patients";

    public function __construct() {
        $this->Database = new DB_Connect();
    }

    private function closeConnection() {
        $this->Database->close();
    }

    public function ExistsUname($username) {
        $query = "SELECT * FROM ".$this->DB_Table." WHERE Username = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }

    public function ExistsEmail($email) {
        $query = "SELECT * FROM {$this->DB_Table} WHERE Email = ?";
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
    public function ExistsID($ID) {
        $query = "SELECT * FROM ".$this->DB_Table." WHERE ID = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("s", $ID);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }

    public function RegisterNewUser($name, $surname, $username, $password, $email, $ID, $medical, $docAcc) {
        $existUname = $this->ExistsUname($username);
        $existsEmail = $this->ExistsEmail($email);
        $existsID = $this->ExistsID($ID);
        $json = array();

        if ($existUname || $existsEmail || $existsID) {
            $json['success'] = 0;
            $json['message'] = "Error in registering. Username/Email/ID already exists";
        } else {
            $emailValid = $this->ValidityEmail($email);
            if ($emailValid) {
//                $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
                $query = "INSERT INTO {$this->DB_Table} (Email, ID, Name, Surname, Username, Doc_AccountNum, Medical_Aid_nr, Password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";                $stmt = $this->Database->getDataBase()->prepare($query);
                $stmt->bind_param("ssssssss", $email, $ID, $name, $surname, $username, $docAcc, $medical, $password);
                $Qresult = $stmt->execute();
                if ($Qresult) {
                    $json['success'] = 1;
                    $json['message'] = "Registration was successful";
                } else {
                    $json['success'] = 0;
                    $json['message'] = "Error in Registration. Ensure you have the correct Doctor's Code";
                }
                $stmt->close();
            } else {
                $json['success'] = 0;
                $json['message'] = "Error in registration. Invalid email";
            }
        }
        return $json;
    }

    public function isLoginExist($username, $password) {
//      $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
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
            $json['message'] = "No user matching supplied credentials";
        }
        return $json;
    }



    public function userLogOut($username, $token) {
        $sql = "SELECT * FROM {$this->DB_Table} WHERE Username = ? AND apiKey = ?";
        $stmt = $this->Database->getDataBase()->prepare($sql);
        $stmt->bind_param("ss", $username, $token);
        $stmt->execute();
        $result = $stmt->get_result();
        $json = array();

        if ($result->num_rows != 0) {
            $sqlUpdate = "UPDATE {$this->DB_Table} SET apiKey = '' WHERE Username = ?";
            $stmtUpdate = $this->Database->getDataBase()->prepare($sqlUpdate);
            $stmtUpdate->bind_param("s", $username);
            if ($stmtUpdate->execute()) {
                $json['success'] = 1;
                $json['message'] = "Logout Success!";
            } else {
                $json['success'] = 0;
                $json['message'] = "Logout Failed";
            }
            $stmtUpdate->close();
        } else {
            $json['success'] = 0;
            $json['message'] = "Logout Failed";
        }
        $stmt->close();
        return $json;
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

        public function returnOrders($pUname){

        $query = "SELECT * FROM ". $this->DB_Table." WHERE Username = ? Limit 1";
        $stmt = $this->Database->getDataBase()->prepare($query);

        $stmt->bind_param("s", $pUname);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $patNum = $row['Pat_AccountNum'];
        }

        $output = array();
	                if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM Orders WHERE Pat_AccountNum = ".$patNum."")){
                        while ($row = $result->fetch_assoc()){
                                $output[] = $row;
                        }
                }

                return $output;
        }

        public function returnPrescrips($pUname){

        $query = "SELECT * FROM ". $this->DB_Table." WHERE Username = ? Limit 1";
        $stmt = $this->Database->getDataBase()->prepare($query);

        $stmt->bind_param("s", $pUname);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $patNum = $row['Pat_AccountNum'];
        }

        $output = array();
        if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM Prescriptions WHERE Pat_AccountNum = ".$patNum."")){
                while ($row = $result->fetch_assoc()){
                        $output[] = $row;
                }
        }

        return $output;
        }
}
?>


