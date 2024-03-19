/*
 * Isabella Pereira
 * IAP200002
 * CS 4348.001
 * Greg Ozbirn 
 * 
 * Notes: 
 * signal: release
 * wait: acquire
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Project2{
    //semaphores
    private static Semaphore receptionist = new Semaphore(1);
    private static Semaphore mutexFrontDesk = new Semaphore(1);
    private static Semaphore msgFrontDesk = new Semaphore(0);

    private static Queue<Integer> toReceptionist = new LinkedList<>(); 
    //array of patients
    //array of nurses
    //array of doctors

    public static void main(String[] args){
        //receive command line input for number of patients and doctors
        int numPts = 1;
        int numDrs = 1;

        /*
        Thread[] patients = new Thread[numPts];
        Thread[] nurses = new Thread[numDrs];
        Thread[] doctors = new Thread[numDrs];

        for(int i = 0; i < numPts; i++){
            patients[i] = new Thread(new Patient(i));
            System.out.println("Patient " + i+ " created."); //remove later
            patients[i].start();
        }*/
        //create one patient thread
        Thread pt = new Thread(new Patient(0));
        pt.start();
        //create one receptionist thread
        Thread rec = new Thread(new Receptionist());
        rec.start();

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

                sit();

                //so on and so forth
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
        private void enterDrOffice(int doctorNum){
            System.out.println("Patient " + ptNum + " enters doctor " + doctorNum + "'s office");
        }
        private void receiveAdvice(int doctorNum){
            System.out.println("Patient " + ptNum + " receives advice from doctor " + doctorNum);
        }
        private void leave(){
            System.out.println("Patient " + ptNum + " leaves");
        }
    }
    private static class Receptionist implements Runnable{
        private int ptNum;
        public void run(){
            try {
                //wait for a message
                msgFrontDesk.acquire();
                //get patient number, critical section
                mutexFrontDesk.acquire();
                ptNum = toReceptionist.remove();
                mutexFrontDesk.release();
                register();

                //receptionist.release();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        private void register(){
            System.out.println("Receptionist registers patient " + ptNum);
        }
    }
    class Nurse implements Runnable{
        private int ptNum;
        private int doctorNum;
        public Nurse(int doctorNum){
            this.doctorNum = doctorNum;
        }
        public void run(){
    
        }
        //Nurse and doctor pair share the same number
        private void takeToDoctor(){
            System.out.println("Nurse " + doctorNum + " takes patient " + ptNum + " to doctor's office");
        }
    
    }
    class Doctor implements Runnable{
        private int ptNum;
        private int doctorNum;
        public Doctor(int doctorNum){
            this.doctorNum = doctorNum;
        }
        public void run(){
    
        }
        private void listen(){
            System.out.println("Doctor " + doctorNum + " listens to symptoms from patient " + ptNum);
        }
    
    }
}