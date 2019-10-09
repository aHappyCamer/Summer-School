package summerschool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SummerSchool {
	static Scanner scanner = new Scanner(System.in);
	static SchoolDatabaseClient sdbc = new SchoolDatabaseClient();
	
	public static void main(String[] args) {
		System.out.println("Welcome to Summer School!");
		SummerSchool.mainMenu();
		
	}
	
	public static void mainMenu() {
		
        // data field that keeps the menu looping
        int input = -1;       
       // main menu option
        while (input != 0) {
            try{
                System.out.println("\nChoose a menu item:\n" + "1. Add a Course\n" + "2. Add a Student\n"
            
                    + "3. Enroll a Student in a Course\n" + "4. Un-enroll a Student from a Course\n"
                    + "5. Remove a Course\n" + "6. List Courses given a student's first name and last name\n"
                    + "7. List Students given a course name\n" + "0. Exit");
            input = scanner.nextInt();
            scanner.nextLine();
            if (input == 1) {
            	String courseName = SummerSchool.validateCourseName();
            	String time = validateTime();
            	sdbc.addCourse(courseName, time);
            }
            else if (input == 2) {
            	String firstName = validateFirstName();
            	String lastName = validateLastName();
            	int age = SummerSchool.validateAge();
            	sdbc.addStudent(firstName, lastName, age);
            	
            }
            else if (input == 3) {
            	SchoolDatabaseClient.listAllStudents();
            	String sid = validateStudentId();
            	int sID = convertToInt(sid);
            	List<Course> courses = SchoolDatabaseClient.listAllCourses(true);
            	String cid = validateCourseId();
            	int cID = convertToInt(cid);
            	SchoolDatabaseClient.enrollStudent(sID, cID, courses);
            	
            }
            else if (input == 4) {
            	SchoolDatabaseClient.listAllStudents();
            	String sid = validateStudentId();
            	int sID = convertToInt(sid);
            	if(!SchoolDatabaseClient.listCoursePerStudent(sID).isEmpty()) {
            	String cid = validateCourseId();
            	int cID = convertToInt(cid);
            	SchoolDatabaseClient.unenrollStudent(sID, cID);
            	}
            }
            
            else if (input == 5) {
            	SchoolDatabaseClient.listAllCourses(true);
            	String cid = validateCourseId();
            	int cID = convertToInt(cid);
            	sdbc.removeCourse(cID);
            	
            }
            else if (input == 6) {
            	String fn = validateFirstName();
            	String ln = validateLastName();
            	sdbc.showStudentsCourse(fn, ln);
            }
            else if (input == 7) {
            	String courseName = validateCourseName();
            	sdbc.checkIfCourseExists(courseName);
            	
            }
            
            }
            catch (InputMismatchException ex){
                System.out.println("Enter a number from the menu option: ");
                scanner.nextLine(); 
            }
        }
	}
	
	public static String validateCourseName() {
			while(true) {
				System.out.println("Enter a name for the course: ");
				String courseName = scanner.nextLine();
				if (courseName.isEmpty()) {
					System.out.println("Course name cannot be empty");
				}
				else {
					if (Pattern.matches("^[a-zA-Z]*$", courseName)) {
						return courseName;
						}
					else {
						System.out.println("A course cannot contain only characters");
					}
				}
			}
	}
	
	public static String validateTime(){
		while(true){
			System.out.println("Enter a time slot (HH:MM): ");
            String time = scanner.nextLine();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				sdf.setLenient(false);
				sdf.parse(time);
			} catch (IllegalArgumentException ex) {
	            System.out.println("You need to enter a time.");
	            continue;
			} catch (InputMismatchException ex) {
	            System.out.println("Invalid time, you need to enter in 24h in the format HH:MM");
	            continue;
	        } catch (ParseException e) {
	            System.out.println("Invalid time, you need to enter in 24h in the format HH:MM");
	            continue;
	        }
			return time;
		}
	}
	
	public static String validateFirstName() {
		while(true) {
			System.out.println("Enter first name: ");
			String firstName = scanner.nextLine();
			if (firstName.isEmpty()) {
				System.out.println("First name cannot be empty");
			}
			else {
				if (Pattern.matches("^[a-zA-Z]*$", firstName)) {
				return maxLength(firstName);
				}
				else {
				System.out.println("Enter the first name with just characters");
				}
			}
		}
	}
	
	public static String validateLastName() {
		
		while(true) {
			System.out.println("Enter last name: ");
			String lastName = scanner.nextLine();
			if (lastName.isEmpty()) {
				System.out.println("Last name cannot be empty");
			}
			else {
				if (Pattern.matches("^[a-zA-Z]*$", lastName)) {
					return maxLength(lastName);
				}
				else {
					System.out.println("Enter the last name with just characters");
				}
			}
		}
	}
	
	public static String maxLength(String name) {
		if (name.length() > 25)
			name = name.substring(0, 25);
		return name;
		
	}
	
	public static int validateAge() {
		int age = -1;
		while (age == -1) {
            try {
                System.out.println("Enter age: ");
                age = scanner.nextInt();

                if (age < 3) {
                    System.out.println("Student is too young to join");
                    scanner.nextLine();
                    age = -1;
                }
            } catch (InputMismatchException e) {
                System.out.println("Integers only.");
                scanner.nextLine();
            }
	}
		return age;
	}
	
	public static String validateStudentId() {	
		String sid = "o";
		while(!isValidInteger(sid)){ 
			System.out.println("\nPlease select a student id: ");
			sid = scanner.nextLine();
			}
		return sid;
	}
	
	public static String validateCourseId() {
		String cid = "o";
		while(!isValidInteger(cid)){ 
			System.out.println("Please select a course id: ");
			cid = scanner.nextLine();
		}
		return cid;
	}
	
	public static boolean isValidInteger(String str)
	{
		// return 'false' if empty string
		if (str.length() == 0)
			return false;

		int i = 0;
		while (i < str.length()){
			if (!Character.isDigit(str.charAt(i)))
				break;
			i++;
			}
		
		if (i == str.length())
			return true;
		
		else {
			return false;
		}
			
		}
	
	public static int convertToInt(String str) {
		int id = Integer.parseInt(str);
		return id;
		
	}	
	
}


			

