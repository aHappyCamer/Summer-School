package summerschool;

import java.util.List;

public class Course {
	private int id;
	private String name;
	private String time;
	
	public Course(int id, String name, String time) {
		this.id = id;
		this.name = name;
		this.time = time;
	}
	
	public Course(String name) {
		this.name = name;
	}
	
	public Course(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTime() {
		return time;
	}
	
	public static String getName(int courseId, List<Course> courses) {
		String courseName = "";

		// for loop to query thorough each object of courses
		for (int i = 0; i < courses.size(); i++) {
			Course course = courses.get(i);
			if (courseId == course.id) {
				courseName = course.getName();
			}
		}
		return courseName;
	}
	
	public static String getCourseId(int courseId, List<Course> courses) {
		String courseName = "";

		// for loop to query thorough each object of courses
		for (int i = 0; i < courses.size(); i++) {
			Course course = courses.get(i);
			if (courseId == course.id) {
				courseName = course.getName();
			}
		}
		return courseName;
	}
	
}
