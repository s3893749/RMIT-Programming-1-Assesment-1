package com.jackgharris.rmit.cos2135.assesment1;

//IMPORT PACKAGES
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//START CLASS
public class EasyTroubleshooter {
	
	//Initialize the class variables that will be needed for this program.
	//name of the current staff member accepting calls
	private String staffName;
	//name of the caller
	private String callerName;
	//selected department this call applies too
	private String selectedDepartment[];
	//selected staff member to transfer this call too
	private String selectedStaffMember[];
	//issue description for this call
	private String issueDescription;
	//log file location
	private String logFile;
	//current step of the call we are on
	private int currentStep;
	
	//Initialize the arrays needed by the program, these are set to 0 to start and automatically increased in size as needed
	//departments list to be loaded from CSV
	private String[][] departments =  new String[0][0];
	//script to be loaded from CSV
	private String[][] script = new String[0][0];
	//department members list to be loaded from CSV
	private String[][] departmentMembers = new String[0][0];
	

    public EasyTroubleshooter(){
    	
    	//set the starting step
    	this.currentStep = 0;
    	
    	//setup the Arrays, arrays are auto expanding on the first dimension with the second dimension being set by the file loader
    	String[][] config = new String[1][1];
    	
    	//set the configuration path
     	String configPath = "config.csv";
     	//load the configuration from the file to the configuration array
        config = this.loadFile(configPath,config,",");        
        
        //load the departments from a file
        this.departments = this.loadFile(config[0][1], this.departments,",");
        
        //load the department members from file
        this.departmentMembers = this.loadFile(config[3][1], this.departmentMembers, ",");
        
        //load the script file
        this.script = this.loadFile(config[1][1], this.script,"``");
        
        //set the log file location
        this.logFile = config[2][1];
        
        //get the first input request from the user
        //INPUT LOOP STARTS WHEN THIS REQUEST IS CALLED
        this.request(null);
        

       
    }
    
    //**** REQUEST INPUT METHOD ****\\
    //request is private as it can only be called by this call and accepts a error input
    private void request(String error) {
    	
    	//setup error variables to be called and thrown when input validation fails
    	//failure for entering a invalid string
    	String incorrectString = "ERROR: Please enter a valid string, no numbers";
    	//failure for entering a invalid number
    	String incorrectInteger = "ERROR: Please enter a valid department/product number, no letters";
    	//failure for selecting a number out of bounds of an array
    	String integerOutOfBounds = "ERROR: Provided Integer is out of bounds";
    	//failure as input was null
    	String nullValueProvided = "ERROR: Provided input is null, Please provide a valid input";
    	
    	//check for errors from prior request
    	if(error != null) {
    		System.out.println(error);
    	}else {
    			
	    	//print the current response for the support staff to read to the caller
	    	System.out.println(this.response(this.script[this.currentStep][0]));
    		}
    		
    		//create the buffered reader object and pass it the input stream reader for System in (Console)
	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	//open try method to attempt the reading of the system input
	    	try {
	    		
	    		//**** STEP 0: Perform checks for first step of the program ****\
	    		//first check if the current step is equal to this step, if so proccede
	    		if(this.currentStep == 0) {
	    			//now set the staff name to the input from the buffered reader
	    			this.staffName = br.readLine();
	    			//next check if that input was blank, if not then proceed
	    			if(!this.staffName.isBlank()) {
	    				//next we are checking that the input was not a number, names should not be a number.
		    			if(!this.intChecker(this.staffName)) {
		    				//if this succeeds and the entered name was not a number and not blank then increase to the next step with ++ and call the request method.
			    			this.currentStep ++;
			    			this.request(null);
		    			}else {
		    				//else if that failed that means we have a number as a name not a string and we should throw the incorrect string error message.
		    				this.request(incorrectString);
		    			}
	    			}else {
	    				//else if our isblank came back true then throw a null value provided error and recall the request.
	    				this.request(nullValueProvided);
	    			}
	    			
	    		//**** STEP 1: Perform checks for the second step of the program ****
		    	//first check if the current step is equal to this step, if so proceed
	    		}else if(this.currentStep == 1) {
	    			//next we are setting the caller name to the buffered reader input
	    			this.callerName = br.readLine();
	    				//next check if that input was blank, if not then proceed
		    			if(!this.callerName.isBlank()) {
		    				//now as the caller name should not be a number check that this name is not a number, proceed if not
							if(!this.intChecker(this.callerName)) {
								//if this name is not blank or a number then proceed to the next step and recall the request method
								this.currentStep ++;
								this.request(null);
							}else{
								//else if this request input was a number not a name then recall the request and throw the incorrect string error
								this.request(incorrectString);
								}
		    			}else {
		    				//else if this request i blank and no input was provided recall the request and throw a null value error
		    				this.request(nullValueProvided);
		    			}
		    			
			    //**** STEP 2: Perform checks for the third step of the program ****
				//first check if the current step is equal to this step, if so proceed
				}else if(this.currentStep == 2) {
					//unlike the prior steps we need to use a temporary variable so we can test the input as a integer before setting the values, this is done below with the local selectedDepartment variable
					String selectedDepartment = br.readLine();
					//next we perform the integer checker, but this time we are checking for a valid number from the user.
					//as this requires the input of a valid integer we do not need to test for a null value as a nmull value called on the integer checker will throw the user error regardless.
					if(this.intChecker(selectedDepartment)) {
						//check we check if the number provided is with in our list of departments, if so proceed, if not throw a out of bounds errors
						if(Integer.parseInt(selectedDepartment) < this.departments.length) {
							//if the input was a number and is with in our range then finally we set the selectedDepartment class variable, increase the current step and then call the new request.
							this.selectedDepartment = this.departments[Integer.parseInt(selectedDepartment)];
							//now we have selected the department the system will automatically assign a staff member for this call to be transfered too
							this.selectedStaffMember = this.getStaffMember(this.selectedDepartment);
							//next increase the step and call the next request
							this.currentStep ++;
							this.request(null);
						}else {
							//else throw the integer out of bounds error in the next request
							this.request(integerOutOfBounds);
						}
					}else{
						//else throw incorrect integer error in next request
						this.request(incorrectInteger);
						}
					
				//**** STEP 3: Perform checks for the forth step of the program ****
				//first check if the current step is equal to this step, if so proceed
				}else if(this.currentStep == 3) {
					
					//firstly get the user input from the read line and set it to the issue description class variable
					this.issueDescription = br.readLine();
					//now check if that input was blank, if not proceed, if so throw a null error
					if(!this.issueDescription.isBlank()) {
						//increase to next step
						this.currentStep ++;
						//recall request method with no error
						this.request(null);
					}else {
						//else if no value was provided recall the request and throw a null value provided error
						this.request(nullValueProvided);
					}
				//**** STEP 4: Perform checks for the fifth step of the program ****
				//first check if the current step is equal to this step, if so proceed
				}else if(this.currentStep == 4) {
					//lastly we want to get the confirmation from the user to transfer the call and save the log, to do this we use a local variable called input
					String input = br.readLine();
					//firstly we check if the input is a valid number, else we throw a error
					//the integer checker will cause a error throw when no value is provided so it is not necessary to check for a null input
					if(this.intChecker(input)) {
						//next we check if the input contains the confirmation number of 1, if so proceed else throw a out of bounds error
						if(input.contains("1")) {
							//if the input is 1 then save the code and rerun the program 
							
							//create a new instance of the date time formatter object and provide it with the date time pattern
							DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
							//get the current local time and store it as a now local variable
					    	LocalDateTime now = LocalDateTime.now();
					    	
					    		//now create the try catch loop to run the file writer in
					    		try {
					    			
					    			//create the file writer and pass it the log file, also set true to tell it to append to the file not replace
					    			FileWriter fileWriter = new FileWriter(this.logFile,true);
					    			//now create an instance of the print writer called writer and pass it the file writer
									PrintWriter writer = new PrintWriter(fileWriter);
									
									//next we add a blank break line to separate call logs in the file
									writer.println("____________________________________");
									//next add the call log text and current date and time
									writer.println("TRANSFER CALL LOG: "+dateTimeFormatter.format(now));
									//next add the staff members name
									writer.println("Staff Member: "+this.staffName);
									//next add the callers name
									writer.println("Caller: "+this.callerName);
									//next add the product / department this call apply's to
									writer.println("Product/Department: "+this.selectedDepartment[1]);
									//next add the issue description 
									writer.println("Issue: "+this.issueDescription);
									//finally add the transfer details of the person this call was transfered too
									writer.println("Transfered To: "+this.selectedStaffMember[2]+this.selectedStaffMember[3]);
									//now that is complete we flush and close the print writer
									writer.flush();
									writer.close();
									
									//now the call is stored in the log we can reset the call specific variables to null
									this.callerName = null;
									this.selectedDepartment = null;
									this.issueDescription = null;
									
								} catch (IOException e) {
									// if we have a error print the stack trace else do nothing
									e.printStackTrace();
								}
					    		
							//if that is a success then reset the current step to step 1, the first step after setting our staff name then recall the request method with no error
							this.currentStep = 1;
							this.request(null);
						}else {
							//else throw integer our of bounds and recall the request
							this.request(integerOutOfBounds);
						}
					}else {
						//else throw a incorrect integer error
						this.request(incorrectInteger);
					}
					
				}
				
			//finally we finish of the try statement with a catch watching for IOexceptions, if one is found we print the stack trace else do nothing
			} catch (IOException e) {
				//print stack trace for that exception.
				e.printStackTrace();
			}
    		
    }
    
    //**** RESPONSE METHOD ****\\
    //this method takes the script and creates a response based on it, it accepts the script template as a variable
    private String response(String template) {
    	
    	//step one set the response to be returned to the value of the template
    	String response = template;
    	
    	//next we create our array of placeholder variables that are going to be checked for a replaced with real data
    	String[] placeholders = {"{{staff-name}}","{{products}}","{{caller-name}}","{{product-selected}}","{{assigned-staff-member}}"};
    	
    	//now we create our loop to check for these placeholder and create a counter i to keep track of our positon in the loop
    	int i =0;
    	//whilst our place holder i is less than the number of placeholder we have run this loop
    	while(i < placeholders.length) {
    		//check if the responses contains a placeholder
    		if(response.contains(placeholders[i])) {
    			
    			//check if the response is the staff name is so perform the change
    			if(placeholders[i].contentEquals("{{staff-name}}")) {
    				
    				//replace the placeholder with the current staff name
    		    	response = response.replace("{{staff-name}}", this.staffName);
    		    	
    		    	//check if the response is the products key if so perform the change
    			}else if(placeholders[i].contentEquals("{{products}}")) {
    				
    				//for this replacement we need to get a list of products / departments from the departments array that has been loaded from CSV
    				//step one we push this to a new line
    				String products = "\n";
    				//now we create a variable p in this case to keep track of what product / department we are on
    				int p = 0;
    				//next we perform this loop for each department in the list
    				while(p < this.departments.length) {
    					//add a new line as well as the number and department name to the list and increase to the next value in the loop
    					products += "\n"+p+") "+this.departments[p][1]+ ", ";
    					p++;
    				}
    				//next we remove two left over characters from the list
    				products = products.substring(0,products.length()-2);
    				//finally we replace the placeholder with our new response
    				response = response.replace("{{products}}", products+".");
    				
    			//next we check if the response has the caller name placeholder is so replace the name	
    			}else if(placeholders[i].contentEquals("{{caller-name}}")) {
    				
    				//set the response to the replacement of the caller name
    				response = response.replace("{{caller-name}}", this.callerName);
    			
    			//next we check if the placeholder is the current selected product 
    			}else if(placeholders[i].contentEquals("{{product-selected}}")) {
    				
    				//if so we replace the placeholder with the selected product variable
    				response = response.replace("{{product-selected}}", this.selectedDepartment[1]);
    			
    			//next check for the assigned staff member variable, this is user who the call will be transfer too
    			}else if(placeholders[i].contentEquals("{{assigned-staff-member}}")) {
    				//replace the placeholder with the staff member name and extentsion, located at index 2 and 3 in the selected staff member array.
    				response = response.replace("{{assigned-staff-member}}", this.selectedStaffMember[2]+this.selectedStaffMember[3]);
    			}

    		}
    		//finally increase our i count and perform the loop again
    		i++;
    	}
    	    	
    	//when the loop has completed we return that correctly translated response
    	return response;
    }
    

    //**** LOAD FILE METHOD ****\\
    //this method takes a string and a target array and separator and converts a CSV file into a 2D array that is returned.
    private String[][] loadFile(String path, String[][] targetArray, String seperator){
    	
        //due to this code having the ability to throw errors we need to contain it a try catch loop
    	try {
    		
    		//step one we create the buffered reader and pass it a file reader with our path string
            BufferedReader br = new BufferedReader(new FileReader(path));
            //next we initialize our local variables of line and count, these store the line and count data that is used in the while loop
            String line = "";
            int count = 0;

            //now we create a while loop that runs whilst the buffered reader is reading a input from the file
            while((line = br.readLine()) != null) {
            	
            	//check if the line contains // is so skip it, else process the line input	
            	if(!line.contains("//")) {
            		//now we check if our current count is less than the array size, if not add the data, else increase the array size
            		if(count < targetArray.length) {
            			//create a local array input variable that is the data split by the seperator
		                String[] arrayinput = line.split(seperator);
		                //then add the array input to the target array
		                targetArray[count] = arrayinput;
		                //finally increase the count
		                count++;
            		}else {
            			//else we expand the array with the array expand method and pass it the array
            			targetArray = this.arrayExpand(targetArray);
            			//now we repeat the steps of creating the tempery array that is eqal to the line split by the seperator
            			String[] arrayinput = line.split(seperator);
            			//now we add that to the target array at this count location
		                targetArray[count] = arrayinput;
		                //finally we increase the count and repead the loop
		                count++;
            		}
            	}
            }
            //once the loop is completed and we have received all the data we close the buffered reader with br.clsoe;
           br.close();
        } catch (IOException e) {
        	//lastly we tell the system that if we have a IO exception we print the stack trace/
            e.printStackTrace();
        }
        
    	//finally we return this newly created 2D array of data back to the caller.
        return targetArray;
    }
    
    
    //**** GET STAFF MEMBER FUNCTION ****\\
    //gets a random staff member based on the selected department
    private String[] getStaffMember(String[] selectedDepartment) {
    	
    	//declare an array of staff members
    	String[][] staff = new String[0][0];
    	//declare a staff count integer variable
    	int staffCount = 0;
    	
    	//create a while loop to get each of the staff members that are part of the selected group
    	int x =0;
    	while(x < this.departmentMembers.length) {
    		//check if this staff member in this cycle is part of the group, is so process the next section, else skip
    		if(this.departmentMembers[x][1].contentEquals(selectedDepartment[0])) {
    			//now check if the staff array size can fit the new value, if so continue
    			if(x < staff.length) {
    				//now ad the new staff to the staff array and increase the staff count variable
    				staff[staffCount] = this.departmentMembers[x];
        			staffCount++;
    			}else {
    				//else this indicates the array is too small, call the array function to expand
	    			staff= this.arrayExpand(staff);
	    			//now we have expanded the array we add staff member to the list and increase the count
					staff[staffCount] = this.departmentMembers[x];
					staffCount++;
    			}
    		}
    		//finally we increase the while loop to check the next staff member in the array
    		x++;
    	}
    	
    	
    	//return a random staff member from the list of staff members with in that department
		return this.departmentMembers[(int) (Math.random() * (staff.length - 0)) + 0];
    	
    }
  
    
    
    //**** ARRAY EXPANDER FUNCTION ****\\
    //main array expander function, takes an array as input and returns the expanded array
    private String[][] arrayExpand(String[][] targetArray){
    	
    	//create a new array that is the length of the old array +1
    	String[][] temperaryArray = new String[targetArray.length+1][];
    	
    	//create while loop to copy all the data from the old array to the new one
    	int i = 0;
    	while(i < targetArray.length) {
    		temperaryArray[i] = targetArray[i];
    		i++;
    	}

    	//finally return the new array back to the calling method;
		return temperaryArray;
    	
    }
    
    
    //**** INTEGER CHECKER FUNCTION ****\\
    //checks to see if a value is an integer
    private boolean intChecker(String value) {
    	
    	//create the output result variable
    	boolean result =true;
    	
    	//try the integer conversion
    	try {
    	Integer.parseInt(value);
    	}catch(NumberFormatException e) {
    		//if we catch a error set the result to false to indicate the value is not an integer
    		result = false;
    	}

    	//finally return the result
		return result;
    }

    
    //main program entry point
    public static void main(String[] args){
    	//Suppress warning is used as we create a instance of this class but never call it
        @SuppressWarnings("unused")
        //create a instance of the class
		EasyTroubleshooter easyTroubleshooter = new EasyTroubleshooter();
    }

}
