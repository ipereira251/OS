public class Project2{
    //semaphores
    //array of patients
    //array of nurses
    //array of doctors
    public static void Main(String[] args){
        //create one patient thread

        //create one receptionist thread

    }
}
class Patient{
    private static int ptNum;
    private static int drNum;
    public static void Main(String[] args){
        enter();
        //signal(waiting_room);
        //wait(receptionist);
        //wait(mutex_front_desk);
        //enqueue(pt_num);
        //signal(mutex_front_desk);

        sit();

        //wait(nurse);
        //get nurse name 
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //give name to nurse
        //wait(mutex_nurse_station[dr_num]);
        //enqueue(pt_num)
        //signal(mutex_nurse_station[dr_num]);

        //enter doctor's office
        //wait(doctor[nurse_num]);
        enterDrOffice(drNum);
    }
    private static void enter(){
        System.out.println("Patient " + ptNum + " enters waiting room, waits for receptionist");
    }
    private static void sit(){
        System.out.println("Patient " + ptNum + " leaves receptionist and sits in waiting room");
    }
    private static void enterDrOffice(int doctorNum){
        System.out.println("Patient " + ptNum + " enters doctor " + doctorNum + "'s office");
    }
    private static void receiveAdvice(int doctorNum){
        System.out.println("Patient " + ptNum + " receives advice from doctor " + doctorNum);
    }
    private static void leave(){
        System.out.println("Patient " + ptNum + " leaves");
    }
}
class Receptionist{
    private static int ptNum;
    public static void Main(String[] args){
        //while(true){
            //wait(waiting_room);
            //wait(mutex_front_desk);
            //dequeue_front_desk(pt_num);
            //signal(mutex_front_desk);
            //register(pt_num);
            //signal(sit); //unnecessary, i think, but we’ll see :)
            //signal(ready_for_nurse); //receptionist tells nurse
            //signal(receptionist);
        //}
    }
    private static void register(){
        System.out.println("Receptionist registers patient " + ptNum);
    }
}
class Nurse{
    private static int ptNum;
    private static int nurseNum;
    public static void Main(String[] args){
    
    }
    //Nurse and doctor pair share the same number
    private static void takeToDoctor(){
        System.out.println("Nurse " + nurseNum + " takes patient " + ptNum + " to doctor's office");
    }

}
class Doctor{
    private static int ptNum;
    private static int doctorNum;
    public static void Main(String[] args){
        
        //wait(ready_for)
    }
    private static void listen(){
        System.out.println("Doctor " + doctorNum + " listents to symptoms from patient " + ptNum);
    }

}