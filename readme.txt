Files:
Project1.java

This program takes in 2 argument. The name of the file that contains the instruction as well as a timer.
A process to communicate between the CPU and memory is created. The CPU class also resides in this java file.
The CPU contains a violation check so that when in user mode it cannot access certain data. It also contains 
a set of instructions as well as a read/write that will be used alongside Memory.java.
-----------------------
Memory.java

This java file contains the memory where it takes in a text file and store the int contents into an array.
It has a read method which returns the data at a requested address as well as a write method that will
put the given data into the given address. These 2 methods are called based on the input given from CPU where
memory will take in a next line using scanner and based on the first digit: a 1 or 2, will indicate what 
method is to be done. If it is read then it will print the returned data so that the CPU can take that data 
and store it.
-----------------------
The following are the text files that contains the program where instructions and values are stored.

sample1.txt
sample2.txt
sample3.txt
sample4.txt
sample5.txt
-----------------------


Compile and run:
javac Memory.java Project1.java
java Project1 sample1.txt 30