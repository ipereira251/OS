/*
 * Isabella Pereira
 * IAP200002
 * CS 4348.001
 * Greg Ozbirn 
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Project2{
    private static int numPts, numDrs;
    private static Thread[] patients;
    private static Thread[] nurses;
    private static Thread[] doctors;
    private static int[] drLookup = new int[15];
    //semaphores
    private static Semaphore receptionist = new Semaphore(1);
    private static Semaphore[] nurse = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};
    private static Semaphore[] doctor = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};


    private static Semaphore mutexFrontDesk = new Semaphore(1);
    private static Semaphore[] mutexWaitingForNurse = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};
    private static Semaphore[] mutexWaitingForDoctor = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};

    private static Semaphore msgFrontDesk = new Semaphore(0);

    private static Semaphore[] readyForNurse = new Semaphore[]{new Semaphore(0), new Semaphore(0), new Semaphore(0)};
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
    @SuppressWarnings("unchecked")
    private static Queue<Integer>[] waitingForNurse = new LinkedList[]{new LinkedList<>(), new LinkedList<>(), new LinkedList<>()};
    @SuppressWarnings("unchecked")
    private static Queue<Integer>[] waitingForDoctor = new LinkedList[]{new LinkedList<>(), new LinkedList<>(), new LinkedList<>()};

    public static void main(String[] args){
        //receive command line input for number of doctors and patients
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
        createThreads(numDrs, numPts);

        //join patient threads when they're done, all others are daemon threads
        try{
            for(int i = 0; i < numPts; i++){
                patients[i].join();
            }
        }
        catch (InterruptedException e){}

        System.out.println("Simulation complete");
    }
    private static void createThreads(int numDrs, int numPts){
        patients = new Thread[numPts];
        nurses = new Thread[numDrs];
        doctors = new Thread[numDrs];

        //create several patient threads
        for(int i = 0; i < numPts; i++){
            int x = getRandomDr();
            drLookup[i] = x;
            patients[i] = new Thread(new Patient(i, x));
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
    }
    private static int getRandomDr(){
        Random r = new Random();
        return r.nextInt(numDrs);
    }
    private static class Patient implements Runnable{
        private int ptNum;
        private int drNum;
        public Patient(int ptNum, int drNum){
            this.ptNum = ptNum;
            this.drNum = drNum;
        }
        public void run(){
            try {
                enter();
                receptionist.acquire();
                
                //give patient number to receptionist, critical section
                mutexFrontDesk.acquire();
                toReceptionist.add(ptNum);
                mutexFrontDesk.release();
                msgFrontDesk.release();

                //wait until receptionist says to sit
                sit[ptNum].acquire();
                sit();
                //let receptionist know they're gone
                leftReceptionist[ptNum].release();

                //wait for nurse
                nurse[drNum].acquire();

                //taken to doctor
                takenToDoctor[ptNum].acquire();
                enterDrOffice();

                //get advice from doctor
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
        private int drNum;
        public void run(){
            while(true){ 
                try {
                    //wait for a message
                    msgFrontDesk.acquire();
                    //get patient number, critical section
                    mutexFrontDesk.acquire();
                    ptNum = toReceptionist.remove();
                    mutexFrontDesk.release();

                    //register patient, tell to sit down
                    register();
                    sit[ptNum].release();

                    //look up which doctor the patient will see
                    drNum = drLookup[ptNum];

                    //critical section for queue of patients waiting for this nurse
                    mutexWaitingForNurse[drNum].acquire();
                    waitingForNurse[drNum].add(ptNum);
                    mutexWaitingForNurse[drNum].release();
                
                    //notify nurse of patient
                    readyForNurse[drNum].release();

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
                    readyForNurse[doctorNum].acquire();

                    //get patient number, queue needs critical section
                    mutexWaitingForNurse[doctorNum].acquire();
                    ptNum = waitingForNurse[doctorNum].remove();
                    mutexWaitingForNurse[doctorNum].release();

                    //wait until doctor is ready
                    doctor[doctorNum].acquire();
                    takeToDoctor();

                    //add to dr queue, critical section
                    mutexWaitingForDoctor[doctorNum].acquire();
                    waitingForDoctor[doctorNum].add(ptNum);
                    mutexWaitingForDoctor[doctorNum].release();

                    //tell patient they're here
                    takenToDoctor[ptNum].release();
                    //tell doctor that a patient is here
                    readyForDoctor[doctorNum].release();

                    //nurse now available
                    nurse[doctorNum].release();
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

                    //get patient number from queue, critical section
                    mutexWaitingForDoctor[doctorNum].acquire();
                    ptNum = waitingForDoctor[doctorNum].remove();
                    mutexWaitingForDoctor[doctorNum].release();

                    //listen, then give advice to patient
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