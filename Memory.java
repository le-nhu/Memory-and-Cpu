/* Memory will act to read and write instructions.
 * Works with CPU to return data and instructions after reading in commands
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//memory class
public class Memory {
	static int [] memory = new int[2000]; //memory; 0-999 for the user program, 1000-1999 for system code
	
	public static void main(String[] args){
		File file = new File(args[0]); 
		
		try {
			Scanner	inputFile = new Scanner(file);		
			int num; //the instruction of each line from file
			String line; //info from file
			int i = 0;
		
		while(inputFile.hasNext()) {
			//get line wo spaces
			line = inputFile.nextLine().trim();
			//skip empty
			if(line.length() < 1){	
				continue;
			}
			//go to position in memory
			else if (line.charAt(0)=='.')  {
				i = Integer.parseInt(line.substring(1));
			}
			//skip if not number
			if(line.charAt(0) < '0' || line.charAt(0) > '9') {
				continue;
			}
			//get instruction of each line
			String[] number = line.split("\\s+");
			if(number.length < 1) {
				continue;
			}
			//store to memory
			else { 
			memory[i] = Integer.parseInt(number[0]);
			i++;
			}
		}
			inputFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		//get requests from pipe; read or write
		Scanner instruction = new Scanner(System.in); 
		while(instruction.hasNextLine()){
			//get command, if input starts w a 1 = execute read method; 2 = write method
			String lines = instruction.nextLine();
			String[] cpuTask = lines.split(",");
			char task = lines.charAt(0);
			int address, data;
			
			switch(task){
				case '1'://CPU request read, output data from address to CPU  
					address = Integer.parseInt(cpuTask[1]);
					System.out.println(read(address));
					break;
				case '2': //CPU request write
					address = Integer.parseInt(cpuTask[1]);
					data = Integer.parseInt(cpuTask[2]);
					write(address, data);
					break;
			}	
		}
		instruction.close(); 	
	}

	//returns the value at the address
	private static int read(int address){
		return memory[address];
	}

	//writes the data to the address
	private static void write(int address, int data){
		memory[address] = data;
	}

}