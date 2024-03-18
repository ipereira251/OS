Problem Overview: 
The clinic to be simulated has doctors, each of which has their own nurse.  Each doctor has an office of his or her own in which to visit patients.  Patients will enter the clinic to see a doctor, which should be randomly assigned.  Initially, a patient enters the waiting room and waits to register with the receptionist.  Once registered, the patient sits in the waiting room until the nurse calls.  The receptionist lets the nurse know a patient is waiting.  The nurse directs the patient to the doctor’s office and tells the doctor that a patient is waiting.  The doctor visits the patient and listens to the patient’s symptoms.  The doctor advises the patient on the action to take.  The patient then leaves.

Pseudocode:
```
void patient(){ 
	//wait(max);
	enter();
	signal(waiting_room);
    wait(receptionist);
    wait(mutex_front_desk);
    enqueue(pt_num);	
    signal(mutex_front_desk);

    //leave and sit, could remove?
    wait(sit);
    sit();

	wait(nurse);
    wait(mutex_nurse);
    enqueue(pt_num);	
	
	wait(finished[pt_num]);
	leave();
	//signal(max);
}
```
```
void receptionist(){
	int pt_num;
	while(true){
        wait(waiting_room);
        wait(mutex_front_desk);
        dequeue_front_desk(pt_num);
        signal(mutex_front_desk);
        register(pt_num);
        signal(sit); //unnecessary, i think, but we’ll see :)
        signal(ready_for_nurse); //receptionist tells nurse
        signal(receptionist);
    }
}
```
```
void nurse(){
	int pt_num;
	while(true){
        wait(ready_for_nurse);
        wait(mutex_nurse);
        dequeue(pt_num);
        signal(mutex_nurse);
        take_to_office(pt_num);
        signal(ready_for_doctor);
        signal(nurse);
    }
}
```
```
void doctor(){
	int pt_num;
	while(true){
        wait(ready_for_doctor);
        wait(mutex_doctor);
        dequeue(pt_num);
        signal(mutex_doctor);
        visit_pt(pt_num);
        listen_to_pt_symptoms(pt_num);
        advise(pt_num);
        signal(finished[pt_num]);
    }
}
```
