# Problem Overview: 
The clinic to be simulated has doctors, each of which has their own nurse.  Each doctor has an office of his or her own in which to visit patients.  Patients will enter the clinic to see a doctor, which should be randomly assigned.  Initially, a patient enters the waiting room and waits to register with the receptionist.  Once registered, the patient sits in the waiting room until the nurse calls.  The receptionist lets the nurse know a patient is waiting.  The nurse directs the patient to the doctor’s office and tells the doctor that a patient is waiting.  The doctor visits the patient and listens to the patient’s symptoms.  The doctor advises the patient on the action to take.  The patient then leaves.

# Pseudocode:
Semaphores used: 

# ADD MUTEX FOR RECEPTIONIST

```
//indicates the availability of the receptionist, nurse(s), doctor(s)
semaphore receptionist = 1;
semaphore nurse = 3;
semaphore doctor[3] = {1};

//ensures mutually exclusive access to the communication queues
semaphore mutex_front_desk = 1;
semaphore mutex_nurse_station = 1;
semaphore mutex_doctor_office[3] = {1};

//signals when a new message has been added to communication queues
semaphore msg_front_desk = 0;
semaphore msg_to_nurse_station = 0;
semaphore msg_from_nurse_station = 0;
semaphore msg_doctor_office[3] = {0};

//used for classes to signal to next 
//e.g., receptionist signals ready_for_nurse so the nurse wakes up and begins handling patient
semaphore ready_for_nurse = 0;
semaphore ready_for_doctor[3] = {0};
semaphore left_office[3] = {0};

//patient-specific semaphores to keep track of progress
semaphore left_reeptionist[15] = {0};
semaphore sit[15] = {0};
semaphore taken_to_doctor[15] = {0};
semaphore advice[15] = {0};
```
```
void patient(){ 
    int pt_num;
    int dr_num;
    enter();

    wait(receptionist);

    //patient number to receptionist
    wait(mutex_front_desk);
    enqueue(pt_num);	
    signal(mutex_front_desk);
    signal(msg_front_desk);

    signal(sit[pt_num]);
    sit();
    signal(left_receptionist[pt_num]);

    wait(nurse);

    //get dr/nurse number
    wait(msg_nurse_station);
    wait(mutex_nurse_station);
    dequeue(dr_num);
    signal(mutex_nurse_station);

    //give number to nurse
    wait(mutex_nurse_station);
    enqueue(pt_num);	
    signal(mutex_nurse_station);
    signal(msg_nurse_station);

    //wait until nurse takes it to doctor, then enter
    wait(takenToDoctor[dr_num]);
    enterDrOffice();

    //give number to doctor
    wait(mutex_doctor_office[dr_num]);
    enqueue(pt_num);	
    signal(mutex_doctor_office[dr_num]);
    signal(msg_doctor_office[dr_num]);

    //receive advice
    wait(advice[pt_num]);
    receiveAdvice();

    leave();
    signal(leftOffice[dr_num]);
}
```
```
void receptionist(){
    int pt_num;
    while(true){

        //get patient number
        wait(msg_front_desk);
        wait(mutex_front_desk);
        dequeue_front_desk(pt_num);
        signal(mutex_front_desk);

        //register patient, tell to sit down
        register(pt_num);
        signal(sit[pt_num]);

        //notify nurse of patient
        signal(ready_for_nurse); 

        //wait for the other patient to leave
        wait(left_receptionist[pt_num]);

        //ready for new patient 
        signal(receptionist);
    }
}
```
```
void nurse(){
    int pt_num;
    int dr_num;
    while(true){
        wait(ready_for_nurse);

        //give dr_num to patient
        wait(mutex_nurse_station);
        enqueue(dr_num);
        signal(mutex_nurse_station);
        signal(msg_from_nurse_station);

        //get patient num
        wait(msg_to_nurse_station);
        wait(mutex_nurse_station);
        dequeue(pt_num);
        signal(mutex_nurse_station);

        //once doctor is ready, take patient, notify doctor
        wait(doctor[dr_num]);
        take_to_doctor();
        signal(taken_to_doctor[pt_num]);
        signal(ready_for_doctor[dr_num]);

        //ready for a new patient
        signal(nurse);
    }
}
```
```
void doctor(){
    int pt_num;
    int dr_num;
    while(true){
        wait(ready_for_doctor[dr_num]);
        
        //get patient name
        wait(msg_doctor_office[dr_num]);
        wait(mutex_doctor_office[dr_num]);
        dequeue(pt_num);
        signal(mutex_doctor_office[dr_num]);

        //listen, then notify patient that advice is available
        listen();
        signal(advice[pt_num]);

        //once patient left, ready for a new patient
        wait(left_office[dr_num]);
        signal(doctor[dr_num]);
    }
}
```
