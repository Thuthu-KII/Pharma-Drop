<?php
include_once 'DBConnect.php';

class Pharmacy {
    private $Database = null;
    private $DB_Table = "Pharmacies";

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
        $query = "SELECT * FROM ".$this->DB_Table." WHERE Email = ?";
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
public function ExistsBusReg($ID) {
        $query = "SELECT * FROM ".$this->DB_Table." WHERE Bus_Reg_Num = ?";
        $stmt = $this->Database->getDataBase()->prepare($query);
        $stmt->bind_param("s", $ID);
        $stmt->execute();
        $result = $stmt->get_result();
        $exists = $result->num_rows > 0;
        $stmt->close();
        return $exists;
    }

    public function RegisterNewUser($busName, $username, $password, $email, $regisNr) {
        $existUname = $this->ExistsUname($username);
        $existsEmail = $this->ExistsEmail($email);
        $existsID = $this->ExistsBusReg($regisNr);
        $json = array();

        if ($existUname || $existsEmail || $existsID) {
            $json['success'] = 0;
            $json['message'] = "Error in registering. Username/Email/Registration Nr already exist";
        } else {
            $emailValid = $this->ValidityEmail($email);
            if ($emailValid) {
                $query = "INSERT INTO ".$this->DB_Table." (Pharm_Name, Username, Password, Bus_Reg_Num, Email) VALUES (?, ?, ?, ?, ?)";
                $stmt = $this->Database->getDataBase()->prepare($query);
                $stmt->bind_param("sssss", $busName, $username, $password, $regisNr, $email);
                $Qresult = $stmt->execute();
                if ($Qresult) {
                    $json['success'] = 1;
                    $json['message'] = "Registration was successful";
                } else {
                    $json['success'] = 0;
                    $json['message'] = "Error in Registration";
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
            try { // to generate a token for that login session
                $apiKey = bin2hex(random_bytes(32));
            } catch (Exception $e) {
                $apiKey = bin2hex(uniqid($username, true));
            }
            // Query for updating the api key on the table
            $query = "UPDATE ".$this->DB_Table." SET apiKey = ? WHERE Username = ?";
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
        $sql = "SELECT * FROM ".$this->DB_Table." WHERE Username = ? AND apiKey = ?";
        $stmt = $this->Database->getDataBase()->prepare($sql);
        $stmt->bind_param("ss", $username, $token);
        $stmt->execute();
        $result = $stmt->get_result();
        $json = array();

        if ($result->num_rows != 0) {
            $sqlUpdate = "UPDATE ".$this->DB_Table." SET apiKey = '' WHERE Username = ?";
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
            $json['message'] = "Logout Failed, Check Username.";
        }
        $stmt->close();
        return $json;
    }

    public function addStock($med_name, $description, $count){
        $insertQuery = "INSERT INTO Medications (MedName, Description, StockCount) VALUES (?, ?, ?)";
        $insertStmt = $this->Database->getDataBase()->prepare($insertQuery);
        $insertStmt->bind_param("sss", $med_name, $description, $count);
        if ($insertStmt->execute()) {
            $insertStmt->close();
            return array('success' => 1, 'message' => 'Medication Stock added successfully');
        } else {
	    $insertStmt->close();
            return array('success' => 0, 'message' => 'Failed to add Medication Stock');
        }
        }

    public function removeStock($med_id) {
    $deleteQuery = "DELETE FROM Medications WHERE Med_ID = ?";
    $deletestmt = $this->Database->getDataBase()->prepare($deleteQuery);

    if ($deletestmt === false) {
        return array('success' => 0, 'message' => 'Failed to prepare statement');
    }

    $deletestmt->bind_param("s", $med_id);

    if ($deletestmt->execute()) {
        $deletestmt->close();
        return array('success' => 1, 'message' => 'Medication Stock removed successfully');
    } else {
        $deletestmt->close();
        return array('success' => 0, 'message' => 'Failed to remove Medication Stock');
    }
    }


        public function updateCount($med_id, $count){
        $updateQuery = "UPDATE Medications SET StockCOunt = ? WHERE Med_ID = ?";
        $updateStmt = $this->Database->getDataBase()->prepare($updateQuery);
//        $updateStmt->bind_param("ss", $count, $med_id);
        if($updateStmt->execute()){
           $updateStmt->close();
           return array('success' => 1, 'message' => 'Medication Stock Count updated successfully');
        }else{
           $updateStmt->close();
           return array('success' => 0, 'message' => 'Medication Stock Count update unsuccessfull');
        }
        }


 public function returnPendingOrders(){
        $output = array();
        if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM Orders WHERE OrderStatus = 0 ")){
                while ($row = $result->fetch_assoc()){
                        $output[] = $row;
                }
        }

        return $output;
        }
        public function returnOrders(){
        $output = array();
        if ($result = mysqli_query($this->Database->getDataBase(), "SELECT * FROM Orders WHERE OrderStatus = 1")){
                while ($row = $result->fetch_assoc()){
                        $output[] = $row;
                }
        }

        return $output;
        }

    public function updateOrders() {
    // Pharmacy Version - for after they have fulfilled the orders
    $updateQuery = "UPDATE Orders SET OrderStatus = 1 WHERE OrderStatus = 0";
    $updateStmt = $this->Database->getDataBase()->prepare($updateQuery);

    if ($updateStmt === false) {
        return array('success' => 0, 'message' => 'Failed to prepare statement');
    }

    if ($updateStmt->execute()) {
        $updateStmt->close();
        return array('success' => 1, 'message' => 'Orders successfully delivered');
    } else {
        $updateStmt->close();
        return array('success' => 0, 'message' => 'Order update unsuccessful');
    }
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

}
?>