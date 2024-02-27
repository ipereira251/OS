/*
 * Isabella Pereira, IAP200002
 * 4348.001 Operating Systems Concepts, Greg Ozbirn
 * Project 1
 */

import java.io.*;
import java.lang.Runtime;
import java.util.Scanner;
import java.util.Random;

public class Project1 { //CPU class, changed name for ease of testing
    private static int pc, sp, ir, ac, x, y; 
    private static boolean interrupt;
    private static int mode;
    private static int numInstr;

    public static void main(String args[]){
        pc = ir = ac = x = y = 0;
        sp = 1000;
        mode = 0;
        numInstr = 0;
        try{
            String fileName = "sample5.txt"; //default file to read
            int interruptTime = 5;  //default number of instructions before interrupt
            if(args.length == 2){
                fileName = args[0];
                interruptTime = Integer.parseInt(args[1]);
            } else {
                //oh no!
                if(args.length == 1)
                    fileName = args[0];
                //sys exit
            }
            Runtime rt = Runtime.getRuntime();
            String cmd[] = {"java", "Mem", fileName}; //start Memory
            Process proc = rt.exec(cmd);

            InputStream is = proc.getInputStream();
            OutputStream os = proc.getOutputStream();
            Scanner sc = new Scanner(is);
            PrintWriter pw = new PrintWriter(os);

            pw.printf("0 0\n"); //ask Memory for first instruction
            pw.flush();

            ir = sc.nextInt();  
            int operand = 0;
            while(true){ 
                if(interruptNeeded(interrupt, interruptTime, numInstr)){
                    interrupt = true; //start interrupt
                    mode = 1;
                    int tempSP = sp;
                    sp = 2000;      //switch to system stack
                    push(tempSP, pw);   //push user program's sp to stack
                    push(pc, pw);   //push user program's pc to stack
                    pc = 1000;      //begin at system program
                    ir = read(pc, pw, sc); //get next instruction at location 1000
                }
                switch(ir){
                    case 1: //Load value into AC
                        pc++;
                        operand = read(pc, pw, sc);     //ask Memory for operand for this instruction
                        ac = operand;
                        pc++;       //increment program counter to fetch next instruction
                        numInstr++; //increment number of instructions executed for interrupt timer
                        break;
                    case 2: //Load the value at the address into the AC
                        pc++;
                        operand = read(pc, pw, sc); //load address
                        ac = read(operand, pw, sc); //load value at the address
                        pc++;
                        numInstr++;
                        break;
                    case 3: //Load value from the address found in the given address into the AC
                        pc++;
                        operand = read(pc, pw, sc); //1st address
                        operand = read(operand, pw, sc); //2nd address
                        operand = read(operand, pw, sc); //value at second address
                        ac = operand;
                        pc++;
                        numInstr++;
                        break;
                    case 4: //Load value at add+X into the AC
                        pc++;
                        operand = read(pc, pw, sc); //read address
                        operand = read(operand + x, pw, sc); //read value at add+x
                        ac = operand;
                        pc++;
                        numInstr++;
                        break;
                    case 5: //Load value at add+y into the AC
                        pc++;
                        operand = read(pc, pw, sc); //read address
                        operand = read(operand + y, pw, sc); //read value at add+y
                        ac = operand; 
                        pc++;
                        numInstr++;
                        break;
                    case 6: //Load from SP+X into the AC
                        operand = read(sp + x, pw, sc);
                        ac = operand;
                        pc++;
                        numInstr++;
                        break;
                    case 7: //Store the value in the AC into the address
                        pc++;
                        operand = read(pc, pw, sc);
                        write(operand, ac, pw); //write AC value in address
                        pc++;
                        numInstr++;
                        break;
                    case 8: //Gets a random int from 1 to 100 into the AC
                        Random r = new Random();
                        int rand = r.nextInt(99) + 1;
                        ac = rand;
                        pc++; //increment program counter
                        numInstr++;
                        break;
                    case 9: //Writes AC as an int/char to the screen
                        pc++;
                        operand = read(pc, pw, sc);
                        if(operand == 1)        //write AC as an int
                            System.out.print((int)ac);
                        else if(operand == 2)   //write AC as a char
                            System.out.print((char)ac);
                        pc++; 
                        numInstr++;
                        break;
                    case 10:  //Add the value in X to the AC
                        ac += x;
                        pc++; 
                        numInstr++;
                        break;
                    case 11: //Add the value in Y to the AC
                        ac += y;
                        pc++; 
                        numInstr++;
                        break;
                    case 12: //Subtract the value in X from the AC
                        ac -= x;
                        pc++;
                        numInstr++;
                        break;
                    case 13: //Subtract the value in Y from the AC
                        ac -= y;
                        pc++;
                        numInstr++;
                        break;
                    case 14:  //Copy the value in the AC to X
                        x = ac;
                        pc++; 
                        numInstr++;
                        break;
                    case 15: //Copy the value in X to the AC
                        ac = x; 
                        pc++;
                        numInstr++;
                        break;
                    case 16: //Copy the value in the AC to Y
                        y = ac;
                        pc++; 
                        numInstr++;
                        break;
                    case 17: //Copy the value in Y to AC
                        ac = y;
                        pc++;
                        numInstr++;
                        break;
                    case 18: //Copy the value in AC to SP
                        sp = ac;
                        pc++;
                        numInstr++;
                        break;
                    case 19: //Copy the value in SP to AC
                        ac = sp;
                        pc++;
                        numInstr++;
                        break;
                    case 20: //Jump to the given address
                        pc++;
                        operand = read(pc, pw, sc); //read address
                        pc = operand;
                        numInstr++;
                        break;
                    case 21: //Jump to address only if value in AC is 0
                        pc++;
                        operand = read(pc, pw, sc); //read address
                        if(ac == 0)
                            pc = operand;
                        else 
                            pc++;
                        numInstr++;
                        break;
                    case 22: //Jump to address if AC is not 0
                        pc++;
                        operand = read(pc, pw, sc); //read address
                        if(ac == 0)
                            pc++; 
                        else
                            pc = operand; 
                        numInstr++;
                        break;
                    case 23: //Push return address onto stack, jump to address
                        pc++; //increment program counter to get address to jump to 
                        operand = read(pc, pw, sc);
                        push(pc + 1, pw); //push return address to stack
                        pc = operand;
                        numInstr++;
                        break;
                    case 24: //Pop return address from stack, jump to the address
                        operand = pop(pw, sc); //pop return address
                        pc = operand;
                        numInstr++;
                        break;
                    case 25: //Increment value in X
                        x++;
                        pc++;
                        numInstr++;
                        break;
                    case 26: //Decrement value in X
                        x--;
                        pc++;
                        numInstr++;
                        break;
                    case 27: //Push AC onto stack
                        push(ac, pw);
                        pc++;
                        numInstr++;
                        break;
                    case 28: //Pop from stack into AC
                        operand = pop(pw, sc);
                        ac = operand;
                        pc++;
                        numInstr++;
                        break;
                    case 29: //Perform system call
                        interrupt = true;   //do not allow interrupts during system call
                        mode = 1;           //entering kernel mode
                        int tempSP = sp;
                        sp = 2000;          //will be decremented immediately on push
                        push(tempSP, pw);   //push user program's sp to the stack 
                        int tempPC = pc + 1;
                        pc = 1500;
                        push(tempPC, pw);   //push user program's incremented pc to the stack
                        numInstr++;
                        break;
                    case 30: //Return from system call
                        pc = pop(pw, sc);
                        sp = pop(pw, sc);
                        mode = 0;           //user mode again
                        interrupt = false;  //interrupts allowed again
                        numInstr++;
                        break;
                    case 50: //end
                        pw.printf("2\n");           //tell memory to exit
                        pw.flush();
                        proc.waitFor();                    //wait for memory to exit
                        System.exit(0); 
                    default: 
                        System.out.println("Encountered a weird instruction: " + ir);
                        System.exit(0);
                }
                ir = read(pc, pw, sc); //fetch next instruction
            }
        }
        catch(Throwable t){
            t.printStackTrace();
        }
    }
    public static int read(int address, PrintWriter pw, Scanner sc){
        //reading past 1000 in user mode
        if(address > 999 && mode == 0){
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            System.exit(0);
        }
        pw.printf("0 %s\n", address);   //ask Memory for value at address
        pw.flush();
            return sc.nextInt();
    }
    public static void write(int address, int value, PrintWriter pw){
        pw.printf("1 %s %s\n", address, value);
        pw.flush();
    }
    public static void push(int value, PrintWriter pw){
        sp--;
        if(sp < 2000){
            write(sp, value, pw);       //ask Memory to write value to sp location
        }
        else{
            System.out.println("Tried to push to address " + sp);
            System.exit(0);
        }
    }
    public static int pop(PrintWriter pw, Scanner sc){
        if(sp >= 2000){ //outside of bounds
            System.out.println("Tried to pop from address " + sp);
            System.exit(0);
        }
        int operand = read(sp, pw, sc);
        write(sp, 0, pw);   //set location in memory equal to zero again
        sp++;
        return operand;
    }
    public static boolean interruptNeeded(boolean interrupt, int interruptTime, int numInstr){
        return (!interrupt && (numInstr % interruptTime) == 0 && numInstr > 0);
    }
}

class Mem{
    private static int memory[];
    public static void main(String args[]){
        memory = new int[2000];
        int i = 0;
        File file = null;
        if(args.length > 0){
            file = new File(args[0]);
        }
        try {
            Scanner fileChop = new Scanner(file);
            while (fileChop.hasNext()) {
                if (fileChop.hasNextInt()) { 
                    memory[i] = fileChop.nextInt(); 
                    i++;
                } else {
                    String line = fileChop.next();
                    if (line.charAt(0) == '.') { 
                        i = Integer.parseInt(line.substring(1)); 
                    }else { 
                        fileChop.nextLine();
                    }
                }
            }
            fileChop.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        Scanner sc = new Scanner(System.in);
        String line = null;
        int address = 0;
        int data = 0;
        while(true){                
            line = sc.nextLine();
            Scanner strChop = new Scanner(line);
            strChop.useDelimiter(" ");
            int op = strChop.nextInt();
            if(op == 0){        //read
                address = strChop.nextInt();
                System.out.println((int)memory[address]);   //send back to CPU
            }else if(op == 1){  //write
                address = strChop.nextInt();
                data = strChop.nextInt();
                memory[address] = data;
            }
            else if(op == 2){   //exit
                //close scanners and exit
                sc.close();
                strChop.close();
                System.exit(0);
            }
        }
    }
}