# Pharma-Drop = Pharmacy locator and prescription tracking app.

There app allows for 3 types of users : Doctors(who prescribe medicine to patients), Patients and Pharmacies(who supply the medicine)
I implemeted an auhentication feature for all three types of users : For doctors you will need a doctors certification number, as a patient you will need an ID number and for pharmacies you will need a business registartion number.

Each user type has a feature/fuction unique to them :

#Doctors : They're the only user that can add a prescripton for a patient and order on their behalf.

#Patients : Can view the prescriptions (pending and fulfllied-history) and they can view nearby pharmacies they can fetch their order from.
(Patients cannot order their own medicine as the app specilises strictly with S3 and S4 scheduling medications.

#Pharmacies : They're the only user that can update the medicine stock.

THE TECH STACK :

We used the LAMP software stack. 

We used our Wits supplied lamp server accounts.

We SSHed to the server, where we store the php files and our MySQL database

For the frontend, we used Java through android studio.




