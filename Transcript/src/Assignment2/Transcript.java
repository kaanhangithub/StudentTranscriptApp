package Assignment2;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

/**
* This class generates a transcript for each student, whose information is in the text file.
* 
*
*/

public class Transcript {
	private ArrayList<Object> grade = new ArrayList<Object>();
	private File inputFile;
	private String outputFile;
	
	/**
	 * This the the constructor for Transcript class that 
	 * initializes its instance variables and call readFie private
	 * method to read the file and construct this.grade.
	 * @param inFile is the name of the input file.
	 * @param outFile is the name of the output file.
	 */
	public Transcript(String inFile, String outFile) {
		inputFile = new File(inFile);	
		outputFile = outFile;	
		grade = new ArrayList<Object>();
		this.readFile();
	}// end of Transcript constructor

	/** 
	 * This method reads a text file and add each line as 
	 * an entry of grade ArrayList.
	 * @exception It throws FileNotFoundException if the file is not found.
	 */
	private void readFile() {
		Scanner sc = null; 
		try {
			sc = new Scanner(inputFile);	
			while(sc.hasNextLine()){
				grade.add(sc.nextLine());
	        }      
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			sc.close();
		}		
	} // end of readFile
	
	
	/** 
	 * This methods creates and returns an arrayList, whose elements is an 
	 * object of Student class.While creating student objects , this method 
	 * uses grade arrayList of class Transcript.This method uses two private
	 * helper methods (regexGrade(),regexWeight()).
	 * @return arrayList of student objects that found in grade arrayList
	 */
	public ArrayList <Student> buildStudentArray() {
		ArrayList <Student> allStudents = new ArrayList<Student>();
		
		for(int i = 0; i<grade.size(); i++) {	
			ArrayList<Course> courseTaken = new ArrayList<Course>();
			ArrayList<Assessment> assignment = new ArrayList<Assessment>();
			// getting the line of "input.txt"  and converting to string.
			String lineOfInput = (String) grade.get(i);
			String studentName = lineOfInput.substring((lineOfInput.lastIndexOf(",")+1),lineOfInput.length());
			String studentId = lineOfInput.substring(11, 15);
			String courseCode = lineOfInput.substring(0, 8);
			double courseCredit = Double.parseDouble(lineOfInput.substring(9, 10));
			Student student = new Student();
			// getting help by helper methods in order to get all grades and weights in this line of input
			ArrayList<Double> grades = regexGrade(lineOfInput);
			ArrayList<Integer> weights = regexWeight(lineOfInput);
			student.addGrade(grades, weights);
			student.setId(studentId);
			student.setName(studentName);
			Course course = new Course(courseCode,assignment,courseCredit);
			courseTaken.add(course);
			// this "for" statement is for checking the next line of "input.txt"
			for(int j = i+1; j<grade.size(); j++) {
				// getting the next line of "input.txt"  and converting to string.
				String nextLineOfInput = (String) grade.get(j);
				// checking if student ids are same.if it is same collect all information about
				// this student
				if(lineOfInput.substring(11, 15).equals(nextLineOfInput.substring(11, 15))) {
					String courseCodeNext = nextLineOfInput.substring(0, 8);
					double courseCreditNext = Double.parseDouble(nextLineOfInput.substring(9, 10));
					Course courseNext = new Course(courseCodeNext,assignment,courseCreditNext);
					// getting help by helper methods in order to get all grades and weights in next line of input
					ArrayList<Double> gradesNext = regexGrade(nextLineOfInput);
					ArrayList<Integer> weightsNext = regexWeight(nextLineOfInput);
					student.addGrade(gradesNext, weightsNext);
					courseTaken.add(courseNext);
					// we need to remove this line because we do not want to encounter
					// this information again
					grade.remove(j);
				}
			}
			student.setCourseTaken(courseTaken);
			// after collecting everything about one student, we can add this student to allStudents arrayList.
			allStudents.add(student);	
		}
		return allStudents;	
	}
	/** 
	 * This methods is a helper method for buildStudentArray().
	 * This method uses regex expression to find values inside parenthesis.
	 * @param str string to check
	 * @return arrayList of grades that found in a input.txt line
	 */
	
	private ArrayList<Double> regexGrade(String str) {
		ArrayList<Double> grade =  new ArrayList<Double>();
		Matcher matcher = Pattern.compile("\\(([^)]+)\\)").matcher(str);
		while(matcher.find()) {
			grade.add(Double.parseDouble(matcher.group(1)));
		}
		return grade;
		
	}
	/** 
	 * This methods is a helper method for buildStudentArray().
	 * This method uses regex expressions to find values between P--'('
	 * and E--'(' .
	 * @param str string to check
	 * @return arrayList of weights that found in a input.txt line
	 */
	private ArrayList<Integer> regexWeight(String str) {
		ArrayList<Integer> weight = new ArrayList<Integer>();
		String regexString = Pattern.quote("P") + "(.*?)" + Pattern.quote("(");
	    String regexString2 = Pattern.quote(",E") + "(.*?)" + Pattern.quote("(");
	    Pattern pattern = Pattern.compile(regexString);
	    Pattern pattern2 = Pattern.compile(regexString2);
	    Matcher matcher = pattern.matcher(str);
	    Matcher matcher2 = pattern2.matcher(str);
	    while(matcher.find()) {
		    weight.add(Integer.parseInt(matcher.group(1)));
		}
		while(matcher2.find()) {
		    weight.add(Integer.parseInt(matcher2.group(1)));
		}
		return weight;
		
		
	}
	
	/**
	 * This method creates a transcript for given students and 
	 * modify the outputFile attribute of Transcript class.
	 * creates a "output.txt" file in directory and write the transcript data in it.
	 * @param student ArrayList of Students
	 * @exception It throws FileNotFoundException if the file is not found.
	 */
	public void printTranscript(ArrayList<Student> student)   {

		for(int i=0 ; i<student.size() ; i++) {
			this.outputFile = this.outputFile + student.get(i).getName()+ "\t" + student.get(i).getStudentID() +"\n"
					          +"--------------------"+"\n";
			
			for(int j = 0 ; j<student.get(i).getCourse().size();j++) {
				this.outputFile = this.outputFile + student.get(i).getCourse().get(j).getCode() 
						+ "\t" +student.get(i).getFinal().get(j)+"\n";
			}
			this.outputFile= this.outputFile + "--------------------" + "\n"+
							"GPA: " + student.get(i).weightedGPA()+"\n"+"\n";
		}
		//these lines of code that below ,creates "output.txt" and write student transcripts in it.
		try {
			PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
			out.print(outputFile);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * returns the outputFile of this transcript
	 * 
	 * @return the outputFile of this transcript
	 */
	public String getOutputFile() {
		return this.outputFile;
	}
	
	public static void main(String args[])  {
			String output ="";
			Transcript transcript = new Transcript("input.txt", output);
			ArrayList<Student> student = transcript.buildStudentArray();
			transcript.printTranscript(student);
	}
	
	

	
} // end of Transcript

    class Student {
    	
	private String studentID;
	private String name;
	private ArrayList<Course> courseTaken;
	private ArrayList<Double> finalGrade; 
	
	/**
	 * This is the default constructor for Student class. 
	 */
	public Student(){
		this.studentID = null;
		this.name = null;
		this.finalGrade = new ArrayList<Double>();
		this.courseTaken = new ArrayList<Course>();
	} // end of student default constructor
	
	
	/**
	 * This is the constructor for Student class that 
	 * initializes its instance variables and construct this.courseTaken.
	 * @param studentID is the student id of a student.
	 * @param name is the name of student.
	 * @param course is the arrayList that has Course objects
	 */
	public Student(String studentID ,String name , ArrayList<Course> course ){
		this.studentID = studentID;
		this.name = name; 
		this.finalGrade = new  ArrayList<Double>();
		this.courseTaken =new ArrayList<Course>(course.size());
		for(Course obj : course)
			this.courseTaken.add(new Course (obj));
	} // end of student custom constructor
	
	/**
	 * calculates final grade (rounded by one decimal place) based on its weight and grade
	 * add it to the finalGrade attribute.
	 * @param grade arrayList of grades
	 * @param weight arayList of weights 
	 * @exception It throws InvalidTotalException if the sum of weight is not 100 or a grade is bigger than 100.
	 */
	public void addGrade(ArrayList<Double> grade ,ArrayList<Integer> weight ) {
		double lastGrade = 0;
		double sumOfWeights = 0;
		for (int i =0 ; i < grade.size(); i++) {
			sumOfWeights = sumOfWeights + weight.get(i);
			if(grade.get(i)>100) throw new InvalidTotalException("Invalid number !! please check your grades ");
			lastGrade = lastGrade + (grade.get(i)* weight.get(i)/100);
		}
		if(sumOfWeights != 100) throw new InvalidTotalException("Invalid number !! please check your weights ");
				
		finalGrade.add(Math.round(lastGrade*10)/10.0);
	}
	
	/**
	 * calculates GPA by using student`s final grades and assign a Grade Point 
	 * between 0-9.
	 * @return GPA that is rounded by one decimal place.
	 */
	public double weightedGPA() {
		
	 double sumGrade = 0;
	 double sumCredit = 0;
	 for (int i = 0; i < finalGrade.size(); i++ ) {
		sumCredit = sumCredit + courseTaken.get(i).getCredit();
		double grade = finalGrade.get(i);
		if (grade>=90) sumGrade= sumGrade+9*courseTaken.get(i).getCredit();
		if (grade<90 & grade>=80) sumGrade= sumGrade+8*courseTaken.get(i).getCredit();
		if (grade<80 & grade>=75) sumGrade= sumGrade+7*courseTaken.get(i).getCredit();
		if (grade<75 & grade>=70) sumGrade= sumGrade+6*courseTaken.get(i).getCredit();
		if (grade<70 & grade>=65) sumGrade= sumGrade+5*courseTaken.get(i).getCredit();
		if (grade<65 & grade>=60) sumGrade= sumGrade+4*courseTaken.get(i).getCredit();
		if (grade<60 & grade>=55) sumGrade= sumGrade+3*courseTaken.get(i).getCredit();
		if (grade<55 & grade>=50) sumGrade= sumGrade+2*courseTaken.get(i).getCredit();
		if (grade<50 & grade>=47) sumGrade= sumGrade+1*courseTaken.get(i).getCredit();
		if (grade<47) sumGrade = sumGrade+0*courseTaken.get(i).getCredit();
	 }
	double gpa = sumGrade/sumCredit;
	return Math.round(gpa*10)/10.0;
		
	}
	
	/**
	 * adds given Course object to courseTaken arrayList
	 * 
	 * @param course Course
	 */
	public void addCourse(Course course) {
		courseTaken.add(course);
	}
	
	/**
	 * returns the student id of this student
	 * 
	 * @return the student id of this student
	 */
	public String  getStudentID() {
		return this.studentID;
	}
	/**
	 * returns the name of this student
	 * 
	 * @return the name of this student
	 */
	public String  getName() {
		return this.name;
	}
	/**
	 * returns a Course array list of courses taken by this student 
	 * 
	 * @return a Course array list of courses taken by this student 
	 */
	public ArrayList<Course> getCourse(){
		ArrayList<Course> copy = new ArrayList<Course>();
		for (Course c :  this.courseTaken) {
			copy.add(c);
		}
		return copy;
		
	}
	
	/**
	 * returns a Double array list of final grades earned by this student 
	 * 
	 * @return a Double array list of final grades earned by this student 
	 */
	public ArrayList<Double> getFinal(){
		return this.finalGrade;
	}
	
	/**
	 * sets this id to given id
	 * 
	 * @param id given id
	 */
	public void setId(String id) {
		this.studentID = id; 
	}
	
	/**
	 * sets this name to given name
	 * 
	 * @param name given name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * sets this course arrayList to given course arrayList
	 * 
	 * @param course given course arrayList
	 */
	public void setCourseTaken(ArrayList<Course> course) {
		this.courseTaken = course;
	}
	/**
	 * 
	 * Returns a string representation of a Student. 
	 * The string representation of student is the studentID, name
	 * courseTaken arrayList and finalGrade of a Student , separated by a comma.
	 * @return a string representation of a Student
	 */
	public String toString() {
		return this.studentID + ","+ this.name + ","+ this.courseTaken + ","+ this.finalGrade;
	}
}// end of Student

    class  Course {
	private String code;
	private ArrayList<Assessment> assignment;
	private double credit;
	
	
	/**
	 * default constructor of Course class
	 */
	public Course() {
		this.code = null;
		this.credit = 0;
		this.assignment = new ArrayList<Assessment>();
	}
	
	
	/**
	 * This is the constructor for Course class that 
	 * initializes its instance variables and construct this.assignment.
	 * @param code is the code of a Course.
	 * @param credit is the credit of a Course.
	 * @param assignment is the arrayList that has Assessment objects.
	 */
	public Course(String code ,ArrayList<Assessment> assignment, double credit) {
		this.code = code;
		this.credit = credit;
		this.assignment = new ArrayList<Assessment>(assignment.size());
		for(Assessment obj : assignment)
			this.assignment.add(obj);
	}// end of  course custom constructor
	
	/**
	 * Initializes this Course by copying another Course. This Course
	 * will have the same code, the same credit and arrayList of assignment as
	 * the other Course.
	 * @param other the Course to copy
	 */
	public Course(Course other) {
		this (other.code,other.assignment,other.credit);
	}// end of Course copy constructor
	
	/**
	 * Compares this Course to the specified Object.
	 * The result is true if and only if the argument is a Course
	 * object having the same credit, code and assignment value as this object.
	 * @param obj the object to compare with
	 * @return true if this object is equal to obj ;
	 * 		   false otherwise
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals (Object obj) {
		if(this== obj) {
			return true;
			}
		if(obj== null) {
			return false;
			}
		if(this.getClass() != obj.getClass()) {
			return false;
			}
		Course other = (Course) obj;
		if(Double.doubleToLongBits(this.credit)!=Double.doubleToLongBits(other.credit)) {
			return false;
		}
		if(! this.code.equals(other.code)) {
			return false;
		}
		if(! this.assignment.equals(other.assignment)) {
			return false;
		}
		return true;
	}
	
	/**
	 * returns the code of this course
	 * 
	 * @return the code of this course
	 */
	public String getCode() {
		return this.code;
	}
	
	/**
	 * returns the credit id of this course
	 * 
	 * @return the credit id of this course
	 */
	public double getCredit() {
		return this.credit;
	}
	
	/**
	 * returns a Assessment array list of assignments belong to this course 
	 * 
	 * @return a Assessment array list of assignments belong to this course 
	 */
	public ArrayList<Assessment> getAssignment(){
		ArrayList<Assessment> copy = new ArrayList<Assessment>();
		for (Assessment c :  this.assignment) {
			copy.add(c);
		}
		return copy;
	}
	
	/**
	 * sets this code to given code
	 * 
	 * @param code given code
	 */
	public void setCode(String code) {
		this.code = code; 
	}
	
	/**
	 * sets this credit to given credit
	 * 
	 * @param credit given credit
	 */
	public void setCredit(double credit) {
		this.credit = credit;
	}
	
	/**
	 * sets this assignment arrayList to given assignment arrayList
	 * 
	 * @param assignment given assignment arrayList
	 */
	public void setAssignment(ArrayList<Assessment> assignment) {
		this.assignment = assignment;
	}
	/**
	 * 
	 * Returns a string representation of Course. 
	 * The string representation of course is the code, credit
	 * and assignment arrayList of the Course, separated by a comma.
	 * @return a string representation of this Course
	 */
	public String toString() {
		return this.code + ","+ this.credit + ","+this.assignment ;
	}
	
	
} // end of Course

    class Assessment{
	private char type;
	private int weight;
	/**
	 * private default constructor of Assessment class
	 */
	private Assessment() {
		this.type = 0;
		this.weight=0;
	}

	/**
	 * This is the constructor for Assessment class that 
	 * initializes its instance variables.it is private constructor
	 * it has a access via getInstance static factory method. 
	 * @param type is the type of a Assessment.
	 * @param weight is the weight of a Assessment.
	 */
	private Assessment(char type , int weight) {
		this.type = type;
		this.weight = weight;
	}
	
	/**
	 * This is the static factory method for Assessment class that 
	 * returns its private custom constructor.
	 * @param type is the type of a Assessment.
	 * @param weight is the weight of a Assessment.
	 * @return private custom Assessment constructor.
	 */
	public static Assessment getInstance(char type, int weight) {
		return new Assessment(type,weight);
	}
	
	/**
	 * Compares this Assessment to the specified Object.
	 * The result is true if and only if the argument is a Assessment
	 * object having the same weight and type value as this object.
	 * @param obj the object to compare with
	 * @return true if this object is equal to obj ;
	 * 		   false otherwise
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this== obj) {
			return true;
			}
		if(obj== null) {
			return false;
			}
		if(this.getClass() != obj.getClass()) {
			return false;
			}
		Assessment other = (Assessment) obj;
		if(this.weight != other.weight) {
			return false;
		}
		if(this.type != other.type ) {
			return false;
		}
		return true;
	}
	
	/**
	 * returns the weight  of this assessment
	 * 
	 * @return the weight  of this assessment
	 */
	public int getWeight() {
		return this.weight;
	}
	
	/**
	 * returns the type of this assessment
	 * 
	 * @return the type of this assessment
	 */
	public char getType() {
		return this.type;
	}
	
	/**
	 * sets this weight to given weight
	 * 
	 * @param weight given weight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	/**
	 * sets this type to given type
	 * 
	 * @param type given type
	 */
	public void setType(char type) {
		this.type = type;
	}
	
	/**
	 * 
	 * Returns a string representation of Assessment. 
	 * The string representation of Assessment is the type
	 * and the weight of the Assessment, separated by a comma.
	 * @return a string representation of this Assessment
	 */
	public String toString() {
		return this.type + ","+ this.weight ;
	}
} // end of Assessment
    
    
    class InvalidTotalException extends RuntimeException{
		
		private static final long serialVersionUID = 1L;

		public InvalidTotalException(String errorMessage) {
	        super(errorMessage);
	    }
    } // end of InvalidTotalException
    
    
    
    
    