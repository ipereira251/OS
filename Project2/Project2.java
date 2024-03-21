/*
 * Isabella Pereira
 * IAP200002
 * CS 4348.001
 * Greg Ozbirn 
 * 
 * Notes: 
 * signal: release
 * wait: acquire
 * 
 * setDaemon() for everyone but patients
 * join patients
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Project2{
    private static int numPts = 3;
    private static int numDrs = 1;
    //semaphores
    private static Semaphore receptionist = new Semaphore(1);
    private static Semaphore nurse = new Semaphore(3);
    private static Semaphore[] doctor = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};


    private static Semaphore mutexFrontDesk = new Semaphore(1);
    private static Semaphore mutexNurseStation = new Semaphore(1);
    private static Semaphore[] mutexDoctorOffice = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};

    private static Semaphore msgFrontDesk = new Semaphore(0);
    private static Semaphore msgToNurseStation = new Semaphore(0);
    private static Semaphore msgFromNurseStation = new Semaphore(0);
    private static Semaphore[] msgDoctorOffice = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0)};

    private static Semaphore readyForNurse = new Semaphore(0);
    private static Semaphore[] readyForDoctor = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0)};
    private static Semaphore[] leftOffice = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0)};

    private static Semaphore[] leftReceptionist = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                  new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                  new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0)};
    private static Semaphore[] sit = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                     new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                     new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0)};

    private static Semaphore[] takenToDoctor = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                               new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                               new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0)};

    private static Semaphore[] advice = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                        new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), 
                                                        new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0), new Semaphore(0)};

       
    //queues
    private static Queue<Integer> toReceptionist = new LinkedList<>(); 
    private static Queue<Integer> toNurse = new LinkedList<>();
    private static Queue<Integer> fromNurse = new LinkedList<>();
    private static Queue<Integer> toDoctor = new LinkedList<>();

    public static void main(String[] args){
        //receive command line input for number of patients and doctors
        if(args.length == 2){
            if(Integer.parseInt(args[0]) > 3 || Integer.parseInt(args[0]) < 0)
                System.exit(0);
            else
                numDrs = Integer.parseInt(args[0]);
            if(Integer.parseInt(args[1]) > 15 || Integer.parseInt(args[1]) < 0)
                System.exit(0);
            else
                numPts = Integer.parseInt(args[1]);
        } else{
            System.exit(0);
        }        
        System.out.println("Run with " + numPts + " patients, " + numDrs + " nurses, " + numDrs + " doctors");
        Thread[] patients = new Thread[numPts];
        Thread[] nurses = new Thread[numDrs];
        Thread[] doctors = new Thread[numDrs];

        //create several patient threads
        for(int i = 0; i < numPts; i++){
            patients[i] = new Thread(new Patient(i));
            patients[i].start();
        }

        //create one receptionist thread
        Thread rec = new Thread(new Receptionist());
        rec.setDaemon(true);
        rec.start();
        
        //create several nurse threads
        for(int i = 0; i < numDrs; i++){
            nurses[i] = new Thread(new Nurse(i));
            nurses[i].setDaemon(true);
            nurses[i].start();
        }

        //create several doctor threads
        for(int i = 0; i < numDrs; i++){
            doctors[i] = new Thread(new Doctor(i));
            doctors[i].setDaemon(true);
            doctors[i].start();
        }

        //join patient threads when they're done, all others are daemon threads
        try{
            for(int i = 0; i < numPts; i++){
                patients[i].join();
            }
        }
        catch (InterruptedException e){}

        System.out.println("Simulation complete");
    }
    private static class Patient implements Runnable{
        private int ptNum;
        private int drNum;
        public Patient(int ptNum){
            this.ptNum = ptNum;
        }
        public void run(){
            enter();
            try {
                receptionist.acquire();
                
                //give patient number to receptionist, critical section
                mutexFrontDesk.acquire();
                toReceptionist.add(ptNum);
                mutexFrontDesk.release();
                msgFrontDesk.release();

                sit[ptNum].acquire();
                sit();
                leftReceptionist[ptNum].release();

                nurse.acquire();
                //get dr/nurse number, critical section
                msgFromNurseStation.acquire();
                mutexNurseStation.acquire();
                drNum = fromNurse.remove();
                mutexNurseStation.release();

                //give number to nurse, critical section
                mutexNurseStation.acquire();
                toNurse.add(ptNum);
                mutexNurseStation.release();
                msgToNurseStation.release();

                //taken to doctor
                takenToDoctor[ptNum].acquire();
                enterDrOffice();

                //give number to dr, critical section
                mutexDoctorOffice[drNum].acquire();
                toDoctor.add(ptNum);
                mutexDoctorOffice[drNum].release();
                msgDoctorOffice[drNum].release();

                advice[ptNum].acquire();
                receiveAdvice();

                leave();
                leftOffice[drNum].release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private void enter(){
            System.out.println("Patient " + ptNum + " enters waiting room, waits for receptionist");
        }
        private void sit(){
            System.out.println("Patient " + ptNum + " leaves receptionist and sits in waiting room");
        }
        private void enterDrOffice(){
            System.out.println("Patient " + ptNum + " enters doctor " + drNum + "'s office");
        }
        private void receiveAdvice(){
            System.out.println("Patient " + ptNum + " receives advice from doctor " + drNum);
        }
        private void leave(){
            System.out.println("Patient " + ptNum + " leaves");

        }
    }
    private static class Receptionist implements Runnable{
        private int ptNum;
        public void run(){
            while(true){ 
                try {
                    //wait for a message
                    msgFrontDesk.acquire();
                    //get patient number, critical section
                    mutexFrontDesk.acquire();
                    ptNum = toReceptionist.remove();
                    mutexFrontDesk.release();
                    register();
                    sit[ptNum].release();

                    //notify nurse of patient
                    readyForNurse.release();

                    leftReceptionist[ptNum].acquire();
                    receptionist.release();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        }
        private void register(){
            System.out.println("Receptionist registers patient " + ptNum);
        }
    }
    private static class Nurse implements Runnable{
        private int ptNum;
        private int doctorNum;
        public Nurse(int doctorNum){
            this.doctorNum = doctorNum;
        }
        public void run(){
            while(true){
                try{
                    //tell patient nurse/doctor's name, critical section
                    readyForNurse.acquire();
                    mutexNurseStation.acquire();
                    fromNurse.add(doctorNum);
                    mutexNurseStation.release();
                    msgFromNurseStation.release();
                    
                    //get patient's name, critical section
                    msgToNurseStation.acquire();
                    mutexNurseStation.acquire();
                    ptNum = toNurse.remove();
                    mutexNurseStation.release();

                    doctor[doctorNum].acquire();
                    takeToDoctor();
                    takenToDoctor[ptNum].release();
                    readyForDoctor[doctorNum].release();

                    //nurse now available
                    nurse.release();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        //Nurse and doctor pair share the same number
        private void takeToDoctor(){
            System.out.println("Nurse " + doctorNum + " takes patient " + ptNum + " to doctor's office");
        }
    
    }
    private static class Doctor implements Runnable{
        private int ptNum;
        private int doctorNum;
        public Doctor(int doctorNum){
            this.doctorNum = doctorNum;
        }
        public void run(){
            while(true){
                try{
                    readyForDoctor[doctorNum].acquire();

                    //get patient name, critical section
                    msgDoctorOffice[doctorNum].acquire();
                    mutexDoctorOffice[doctorNum].acquire();
                    ptNum = toDoctor.remove();
                    mutexDoctorOffice[doctorNum].release();

                    listen();
                    advice[ptNum].release();

                    //once patient left, ready for another
                    leftOffice[doctorNum].acquire();
                    doctor[doctorNum].release();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } 

        }
        private void listen(){
            System.out.println("Doctor " + doctorNum + " listens to symptoms from patient " + ptNum);
        }
    
    }
}