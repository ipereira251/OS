/*
 * Isabella Pereira
 * IAP200002
 * CS 4348.001
 * Greg Ozbirn
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Project3{
    private static ArrayList<Job> jobs = new ArrayList<Job>(); 

    public static void main(String[] args){
        //read file
        if(args.length == 1 && readFile(new File(args[0]))){ 
            //uses java's short-circuiting, if args.length is not 1, will not try to read file

            //fcfs scheduler
            System.out.println("FCFS\n");
            printJobs();
            while(!allCompleted())
                FCFSScheduler.run();

            //reset all jobs to not completed 
            reset(jobs);
            System.out.println();

            //rr scheduler
            System.out.println("RR\n");
            printJobs();
            while(!allCompleted())
                RRScheduler.run();
        }
        else{
            readFile(new File("jobs2.txt"));
            System.out.println("FCFS\n");
            printJobs();
            while(!allCompleted())
                FCFSScheduler.run();

            //reset all jobs to not completed 
            reset(jobs);
            System.out.println();

            //rr scheduler
            System.out.println("RR\n");
            printJobs();
            while(!allCompleted())
                RRScheduler.run();
        }

    }
    public static boolean readFile(File file){
        try{
            Scanner fileChop = new Scanner(file);
            while(fileChop.hasNext()){
                int startTime = -1;
                int duration = -1;
                String nameString = fileChop.next();
                if(fileChop.hasNextInt())
                    startTime = fileChop.nextInt();
                if(fileChop.hasNextInt())
                    duration = fileChop.nextInt();
                jobs.add(new Job(nameString.charAt(0), startTime, duration));
            }
            fileChop.close();
            return true;
        } catch (FileNotFoundException e){
            return false;
        }
    }
    public static boolean allCompleted(){
        boolean ret = true;
        for(int i = 0; i < jobs.size(); i++)
            if(!jobs.get(i).isCompleted())
                ret = false;
        return ret;
    }
    public static void printJobs(){
        for(int i = 0; i < jobs.size(); i++)
            System.out.print(jobs.get(i).getName() + " ");
        System.out.println();
    }
    public static void reset(ArrayList<Job> jobs){
        for(int i = 0; i < jobs.size(); i++){
            jobs.get(i).resetNumQuanta();
        }
    }
    public static class FCFSScheduler{
        private static int currentJob = -1;
        private static int currentTime = 0;
        private static boolean[] eligible = new boolean[jobs.size()];
        public static void run(){
            boolean hasEligible = false;
            //reset all eligible to false 
            for(int i = 0; i < eligible.length; i++)
                eligible[i] = false;
            for(int i = 0; i < jobs.size(); i++){
                if(jobs.get(i).getStartTime() <= currentTime && !jobs.get(i).isCompleted()){
                    eligible[i] = true;
                    hasEligible = true;
                }
            }
            
            if(!hasEligible){
                currentJob = jobs.size() + 1;
            } else{
                currentJob = 0;
                //choose next job
                if(currentJob >= eligible.length)
                    currentJob %= eligible.length;
                while(!eligible[currentJob]){
                    currentJob++;
                    if(currentJob >= eligible.length)
                        currentJob %= eligible.length;
                    
                }
            }    
                //print jobs
                for(int i = 0; i < jobs.size(); i++){
                    if(i == currentJob){
                        System.out.print("X ");
                        jobs.get(i).incNumQuanta();
                    } else {
                        System.out.print("  ");
                    }
                }
            
            System.out.println(); //new line
            currentTime++;
        }
    }
    public class RRScheduler{
        private static int currentJob = 0;
        private static int currentTime = 0;
        private static boolean[] eligible = new boolean[jobs.size()];
        public static void run(){
            boolean hasEligible = false;
            //reset all eligible to false
            for(int i = 0; i < eligible.length; i++)
                eligible[i] = false;
                
            for(int i = 0; i < jobs.size(); i++){
                if(jobs.get(i).getStartTime() <= currentTime && !jobs.get(i).isCompleted()){
                    eligible[i] = true;
                    hasEligible = true;
                }
            }
            
            if(!hasEligible){
                currentJob = -1;
            } else {
                //choose new current job
                if(currentJob >= eligible.length)
                    currentJob %= eligible.length;
                while(!eligible[currentJob]){
                    currentJob++;
                    if(currentJob >= eligible.length)
                        currentJob %= eligible.length;
                }
            }

            //print jobs
            for(int i = 0; i < jobs.size(); i++){
                if(i == currentJob){
                    System.out.print("X ");
                    jobs.get(i).incNumQuanta();
                } else 
                    System.out.print("  ");
            }

            System.out.println();
            currentJob++;
            currentTime++;
        }
    }
 }
 class Job{
    private char name;
    private int startTime, duration, numQuanta;
    public Job(char name, int startTime, int duration){
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.numQuanta = 0;
    }
    public char getName(){
        return name;
    }
    public int getStartTime(){
        return startTime;
    }
    public int getDuration(){
        return duration;
    }
    public int getNumQuanta(){
        return numQuanta;
    }
    public void incNumQuanta(){
        numQuanta++;
    }
    public void resetNumQuanta(){
        numQuanta = 0;
    }
    public boolean isCompleted(){
        return numQuanta == duration;
    }
    public String toString(){
        return "Job " + name + ": Start time: " + startTime + ", Duration: " + duration;
    }
 }