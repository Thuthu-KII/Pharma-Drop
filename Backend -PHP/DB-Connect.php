<?php
include_once 'configuration.php';

Class DB_Connect{
        private $connect;

        public function __construct(){
                $this->connect = mysqli_connect(DB_Host, DB_Username, DB_Password, DB_Name);

                if(mysqli_connect_errno()){
                        echo "Unable to connect to the Database: ".mysqli_connect_error();
                }
        }
        public function getDatabase(){
                return $this->connect;
        }

}

?>


