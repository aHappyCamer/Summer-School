package summerschool;

public class StudentCourse {
	private int courseId;
	private int studentId;
	private String courseName;
	
	public StudentCourse(int courseId, int studentId, String courseName) {
		this.courseId = courseId;
		this.studentId = studentId;
		this.courseName = courseName;
	}
	
	public int getcourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getCourseName() {
		return courseName;
	}
	
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	
//	public static List<StudentCourse> getStudentCourse(int courseId, int studentId, List<StudentCourse> course) {
//		//run through array to get courseid and coursename
//		List<StudentCourse> listStudentCourse = new ArrayList<>();
//		
//		for (int i = 0; i < course.size(); i++) {
//			StudentCourse  sc = course.get(i);
//			if(studentId == StudentCourse.studentId) {
//				listStudentCourse.add(courseId,studentId, course);
//			}
//			
//		}
//		return listStudentCourse;
//			
//	}
//
//		// for loop to query through each object of courses
//		for (int i = 0; i < course.size(); i++) {
//			StudentCourse sc = course.get(i);
//			if (studentId == course.id) {
//				courseName = sc.getCourseName();
//			}
//		}
//		return courseName;
//	}
	
//	public static String getCourseName(int courseId, List<Course> courses) {
//		String courseName = "";
//
//		// for loop to query thorough each object of courses
//		for (int i = 0; i < courses.size(); i++) {
//			Course course = courses.get(i);
//			if (courseId == course.id) {
//				courseName = course.getName();
//			}
//		}
//		return courseName;
//	}

}
