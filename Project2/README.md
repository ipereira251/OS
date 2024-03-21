# Problem Overview: 
The clinic to be simulated has doctors, each of which has their own nurse.  Each doctor has an office of his or her own in which to visit patients.  Patients will enter the clinic to see a doctor, which should be randomly assigned.  Initially, a patient enters the waiting room and waits to register with the receptionist.  Once registered, the patient sits in the waiting room until the nurse calls.  The receptionist lets the nurse know a patient is waiting.  The nurse directs the patient to the doctor’s office and tells the doctor that a patient is waiting.  The doctor visits the patient and listens to the patient’s symptoms.  The doctor advises the patient on the action to take.  The patient then leaves.

# Pseudocode:
Semaphores used: 
```
// indicates when a patient enters the waiting room
semaphore waiting_room = 0; 
// represents receptionists available
semaphore receptionist = 1;
// represents nurses available
semaphore nurse = 3;
// ensures mutually exclusive access to receptionist and nurse queues
semaphore mutex_front_desk, mutex_nurse_station = 1;
// ensures mutually exclusive access to queues used to communicate with doctors
semaphore mutex_doctor_office[3] = {1};

// alerts when a new message has been added to the queue
semaphore msg_front_desk, msg_to_nurse_station, msg_from_nurse_station = 0;
semaphore msg_doctor_office[3] = {0};

// shows when a patient is waiting for a nurse/doctor
semaphore ready_for_nurse = 0;
semaphore ready_for_doctor[3] = {0};

// shows when each patient has been taken to see the doctor by a nurse
semaphore taken_to_doctor[15] = {0};

// 
semaphore advice[15] = {0};
// 
semaphore doctor[3] = {1};
//semaphore finished[15] = {0};
//semaphore max;
```
```
void patient(){ 
    int pt_num;
    int dr_num;
    enter();
    //signal(waiting_room);
    wait(receptionist);
    wait(mutex_front_desk);
    enqueue(pt_num);	
    signal(mutex_front_desk);
    signal(msg_front_desk);

    //leave and sit, could remove?
    //wait(sit);
    sit();

    wait(nurse);
    //get dr/nurse name
    wait(msg_nurse_station);
    wait(mutex_nurse_station);
    dequeue(dr_num);
    signal(mutex_nurse_station);

    //give name to nurse
    wait(mutex_nurse_station);
    enqueue(pt_num);	
    signal(mutex_nurse_station);
    signal(msg_nurse_station);

    //enter doctor's office
    wait(doctor[dr_num]);
    enterDrOffice();

    //give name to doctor
    wait(mutex_doctor_office[dr_num]);
    enqueue(pt_num);	
    signal(mutex_doctor_office[dr_num]);

    //receive advice
    wait(advice[pt_num]);
    receiveAdvice();

    //wait(finished[pt_num]);
    leave();
    //signal(max);

}
```
```
void receptionist(){
    int pt_num;
    while(true){
        //wait(waiting_room);

        //get patient name
        wait(msg_front_desk);
        wait(mutex_front_desk);
        dequeue_front_desk(pt_num);
        signal(mutex_front_desk);

        register(pt_num);

        signal(ready_for_nurse); //receptionist tells nurse

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
        signal(msg_nurse_station);

        //////need to ensure the nurse doesn't read their own name

        //get patient num
        wait(msg_nurse_station);
        wait(mutex_nurse_station);
        dequeue(pt_num);
        signal(mutex_nurse_station);

        take_to_office(pt_num);
        signal(ready_for_doctor[dr_num]);
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

        listen_to_pt_symptoms(pt_num);
        //give advice, done with this patient 
        signal(advice[pt_num]);

        //ready for a new patient
        signal(doctor[dr_num]);
        //signal(finished[pt_num]);

    }
}
```
