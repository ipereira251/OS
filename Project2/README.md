"Project 2 README" 

Pseudocode:
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

