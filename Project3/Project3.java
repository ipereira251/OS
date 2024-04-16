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
            System.out.println(jobs);

            //fcfs scheduler
            printJobs();
            while(!allCompleted())
                FCFSScheduler.run();

            //reset all jobs to not completed 
            reset(jobs);
            System.out.println();
            //rr scheduler
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
        private static int currentJob = 0;
        public static void run(){
            //switch to new job if current one is done       
            if(jobs.get(currentJob).isCompleted())
                currentJob++;
            for(int i = 0; i < jobs.size(); i++){
                if(i == currentJob){
                    System.out.print("X ");
                    jobs.get(i).incNumQuanta();
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println(); //new line
        }
    }
    public class RRScheduler{
        private static int currentJob = 0;
        public static void run(){
            //choose new current job
            if(currentJob >= jobs.size())
                currentJob %= jobs.size();
            while(jobs.get(currentJob).isCompleted()){
                currentJob++;
                if(currentJob >= jobs.size())
                    currentJob %= jobs.size();
            }

            //run new current job
            for(int i = 0; i < jobs.size(); i++){
                if(i == currentJob){
                    System.out.print("X ");
                    jobs.get(i).incNumQuanta();
                } else 
                    System.out.print("  ");
            }

/* 
            //run new current job
            for(int i = 0; i < jobs.size(); i++){
                if(currentJob % jobs.size() == i && !jobs.get(i).isCompleted()){
                    System.out.print("X ");
                    jobs.get(i).incNumQuanta();
                } else
                    System.out.print("  ");
            }*/
            System.out.println();
            currentJob++;
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