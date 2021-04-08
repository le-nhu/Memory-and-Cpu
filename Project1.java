/*Project1 main will set up a process and begin the actual instruction w the CPU.
 * Works with memory class where CPU and memory will function together after
 * receiving in a text file containing instructions and a timer. Text file will contain 
 * the instruction set as well as data to be format and display to screen.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Project1
{
	public static void main(String[] args){
		//no txt file or timer; program wont run
		if(args.length < 2)	{
			System.err.println("Insufficent arguments entered. 2 arguments needed: txt file and timer");
			System.exit(1);
		}
		
		//get txt input and timeout from user
		String name = args[0];
		int timeout = Integer.parseInt(args[1]);	
		
		// Call the Memory process with the input program argument
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("java Memory " + name);

			//input & output from memory for CPU to work with
			InputStream is = proc.getInputStream();
			OutputStream os = proc.getOutputStream();
        
			PrintWriter pw = new PrintWriter(os);
			Scanner readMem = new Scanner(is); 
        
			//begin
			CPU cpu = new CPU(readMem, pw, timeout);	
		} 
		catch (IOException exp) 
		{
			exp.printStackTrace();
			System.exit(1);
		}
	}

	//Acts as the CPU, will work with memory
	private static class CPU{
		int PC =0, SP = 1000, IR =0, AC=0, X=0, Y=0; //registers
		int timer = 0, timeout;
		private boolean kernelMode = false, work = true;	//starts in user mode; work represent the program executing
		private Scanner is;
		private PrintWriter os;
		
		public CPU(Scanner readMem, PrintWriter pw, int time) {
			is = readMem;
			os = pw;
			timeout = time;

			//go through set of instructions
			while(work){
				IR = read(PC++);	//get instruction
				instructionSet();
			}
			
			System.exit(0);	
		}
		
		//user program cannot access system memory; check for violation
		public void violation(int address) {
			if(!kernelMode){
				if(address>=1000 ) {
					System.err.println("Memory violation: accessing system address 1000 in user mode");
					System.exit(0);
				}
			}
		}
		
		//run based on instruction set
		private void instructionSet(){
		switch(IR){
			case 1: //Load the IR into the AC
				IR = read(PC++);
				AC = IR;
				break;
			case 2: //Load the IR at the address into the AC
				IR = read(PC++);
				AC = read(IR);
				break;
			case 3: //Load the IR from the address found in the given address into the AC
				IR = read(PC++);
				AC = read(read(IR));
				break;
			case 4: //Load the IR at (address+X) into the AC
				IR = read(PC++);
				AC = read(IR + X);
				break;
			case 5://Load the IR at (address+Y) into the AC
				IR = read(PC++);
				AC = read(IR + Y);
				break;
			case 6://Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
				AC = read(SP+X);
				break;
			case 7: //Store the IR in the AC into the address
				IR = read(PC++);
				write(IR, AC);
				break;
			case 8://Gets a random int from 1 to 100 into the AC
				AC = (int) (Math.random()*100+1);
				break;
			case 9: //If port=1, writes AC as an int to the screen
				//If port=2, writes AC as a char to the screen
				IR = read(PC++);
				if(IR == 1){
					System.out.print(AC);
				}		
				else if(IR == 2){
					System.out.print((char)AC);
				}		
				break;
			case 10: //Add the IR in X to the AC
				AC += X; 
				break;
			case 11: //Add the IR in Y to the AC
				AC += Y; 
				break;
			case 12: //Subtract the IR in X from the AC
				AC -= X; 
				break;
			case 13: //Subtract the IR in Y from the AC
				AC -= Y; 
				break;
			case 14: //Copy the IR in the AC to X
				X = AC; 
				break;
			case 15: //Copy the IR in X to the AC
				AC = X; 
				break;
			case 16: //Copy the IR in the AC to Y
				Y = AC; 
				break;
			case 17: //Copy the IR in Y to the AC
				AC = Y; 
				break;
			case 18: //Copy the IR in AC to the SP
				SP = AC; 
				break;
			case 19://Copy the IR in SP to the AC 
				AC = SP; 
				break;
			case 20: //Jump to the address
				IR = read(PC++);
				PC = IR;
				break;
			case 21: //Jump to the address only if the IR in the AC is zero
				IR = read(PC++);
				if(AC == 0) {
					PC = IR;
				}
				break;
			case 22: //Jump to the address only if the IR in the AC is not zero
				IR = read(PC++);
				if(AC != 0)
					PC = IR;
				break;
			case 23: //Push return address onto stack, jump to the address
				IR = read(PC++);
				write(--SP,PC);
				PC = IR;
				break;
			case 24://Pop return address from the stack, jump to the address
				PC = read(SP++);
				break;
			case 25://Increment the IR in X
				X++; 
				break;
			case 26://Decrement the IR in X
				X--; 
				break;
			case 27://Push AC onto stack
				write(--SP,AC);
				break;
			case 28: //Pop from stack into AC
				AC = read(SP++);
				break;
			case 29: //Perform system call
				if(!kernelMode){
					kernelMode = true;
					int tempSP = SP; 
					SP = 2000;
					write(--SP, tempSP);
					write(--SP, PC);
					write(--SP, IR);
					write(--SP, AC);
					write(--SP, X);
					write(--SP, Y);
					PC = 1500;
				}
				break;
			case 30: //Return from system call
				Y = read(SP++);
				X = read(SP++);
				AC = read(SP++);
				IR = read(SP++);
				PC = read(SP++);
				SP = read(SP++);
				kernelMode = false;
				break;
			case 50: //End execution
				work = false;
			default: //End execution
				work = false;
			}
		
			//timer increase after each instruction;check for the input timeout
			timer++;
			
			//interrupt switch from user -> kernel
			if(timer >= timeout){
				if(!kernelMode) {
					timer = 0;
					kernelMode = true;
					int tempSP = SP; 
					SP = 2000;
					write(--SP, tempSP);
					write(--SP, PC);
					write(--SP, IR);
					write(--SP, AC);
					write(--SP, X);
					write(--SP, Y);
					PC = 1000;
				}	
			}
		}
	
		//pipe read from memory
		private int read(int address){
			violation(address);
			os.printf("1," + address + "\n");
			os.flush();
			return Integer.parseInt(is.nextLine());
		}
		
		//pipe write to memory
		private void write(int address, int data){
			os.printf("2," + address + "," + data + "\n");
			os.flush();
		}			
	}
}