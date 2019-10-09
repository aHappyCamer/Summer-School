package summerschool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SchoolDatabaseClient {
	
	public static class DBConnector {
		private static String dbURL = "databaseURL";
		private static String driverName = "com.mysql.jdbc.Driver";
		private static String username = "username";
		private static String password = "password";
		private static Connection con = null;

		/**
		 * This method establishes a connection to sql server
		 * 
		 * @Connection returns the connection to the database HotSummer
		 */
		public static Connection getConnection() {
			try {
				Class.forName(driverName);
				try {
					con = DriverManager.getConnection(dbURL, username, password);
				} catch (SQLException ex) {
					String sqlMessage = ex.getMessage();
					String sqlState = ex.getSQLState();
					int vendorCode = ex.getErrorCode();

					System.err.println("Exception occurred:");
					System.err.println("Message: " + sqlMessage);
					System.err.println("SQL State: " + sqlState);
					System.err.println("Vendor code: " + vendorCode);
				}
			} catch (ClassNotFoundException ex) {
				System.out.println("Driver not found.");
			}
			return con;
		}
	}
	
	public static void enrollStudent(int studentId, int courseId, List<Course> courses) {

		// First check array list to see if student is already enrolled in the selected
		// course
		String courseName = Course.getName(courseId, courses);
		List<Student> students = listStudents(courseName);

		for (int i = 0; i < students.size(); i++) {
			Student student = students.get(i);
			if (studentId == student.getId()) {
				System.out.println("This student is already enrolled!\n");
				// early exit if student is already enrolled
				return;
			}

		}
		
		Connection con = DBConnector.getConnection();

		// Nested query allows the distinction between foreign key values
		String sql = "INSERT INTO StudentCourse (studentId, courseId) VALUES (?, (SELECT id from Course where id = ?))";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, studentId);
			ps.setInt(2, courseId);

			ps.executeUpdate();
			// If execution is successful than this message will show on console
			System.out.println("Student with id number " + studentId + " has been sucessfully enrolled into course id "
					+ courseId);

			ps.close();
			con.close();
		}

		catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL State: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);
		}
	}
	
	public void checkIfCourseExists(String courseName) {
		Connection con = DBConnector.getConnection();
		
		String query = "Select `name` FROM Course WHERE `name` = ?";
		
		try {
			// prepared statement
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, courseName);

			ResultSet rs = ps.executeQuery();

				if(rs.next()) {
					String cn = rs.getString("name");
					listStudents(cn);
				}
				else {
					System.out.println("Course does not exist.");			
				}
				
			rs.close();
			ps.close();
			con.close();
			
		} catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}
	}

	public void removeCourse(int courseId) {
		Connection con = DBConnector.getConnection();

		String query = "Select Count(*) as stuNum FROM StudentCourse WHERE courseId = ? ";

		try {
			// prepared statement
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, courseId);

			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt("stuNum") != 0) {
				System.out.println("Cannot delete, students are currently enrolled in this course");
				// This select query will display students details if the course selected
				// currently has students enrolled
				String studentsEnrolledSQL = "SELECT s.firstname, s.lastname, c.name "
						+ "FROM Student s, Course c, StudentCourse sc "
						+ "WHERE sc.studentId = s.id AND sc.courseId = c.id AND c.id = ?";

				ps = con.prepareStatement(studentsEnrolledSQL);
				ps.setInt(1, courseId);
				rs = ps.executeQuery();
				rs.next();

				// Display information of enrolled students to console
				System.out.print("\nStudents Enrolled:");
				System.out.printf("\n%1s %23s %20s %-40s", "First Name", "Last Name", "Course", 
						"\n=====================================================================\n");
				do {
					String firstName = rs.getString(1);
					String lastName = rs.getString(2);
					String courseName = rs.getString(3);
					System.out.printf("%-25s %-25s %-15s\n", firstName, lastName, courseName);
				} while (rs.next());
				// If no students are enrolled than delete statement will remove the course
			} else {
				String sql = "DELETE FROM Course WHERE id = ?";

				PreparedStatement psn = con.prepareStatement(sql);
				psn.setInt(1, courseId);

				psn.executeUpdate();
				System.out.println("Course with id " + courseId + " has been successfully removed!");
				psn.close();
			}

			rs.close();
			ps.close();
			con.close();

		} catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}
	}

	public static List<Student> listStudents(String courseName) {
		ArrayList<Student> students = new ArrayList<>();
		
		Connection con = DBConnector.getConnection();
		PreparedStatement ps = null; 

		String query = "Select `name`, firstName, lastName, sid.id " + "FROM Student sid "
				+ "JOIN StudentCourse sc ON sid.id=sc.studentId " + "JOIN Course c ON sc.courseId=c.id "
				+ "WHERE `name` = ? ";

		try {
			ps = con.prepareStatement(query);
			ps.setString(1, courseName);
			ResultSet rs = ps.executeQuery();
			System.out.println("Students Enrolled in " + courseName + ":");
			System.out.format("%s %10s %10s %s","ID", "First Name", "Last name", "\n==========================\n" );

			while (rs.next()) {
				String fn = rs.getString("firstName");
				String ln = rs.getString("lastName");
				int studid = rs.getInt("sid.id");

				System.out.format("%d %10s %10s\n", studid, fn, ln);
				students.add(new Student(studid, fn, ln));
			}

			rs.close();
			ps.close();
			con.close();
			
		} catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}
		return students;
	}
	
	public static List<String> listCourses(String firstName, String lastName) {
		
		Connection con = DBConnector.getConnection();
		PreparedStatement ps = null; 
		
		String query = "Select firstName, lastName, `name` " + "FROM Student sid "
				+ "JOIN StudentCourse sc ON sid.id=sc.studentId " + "JOIN Course c ON sc.courseId=c.id "
				+ "WHERE firstName = ? AND lastName = ?";

		List<String> courses = new ArrayList<>();

		try {
			// prepared statement
			ps = con.prepareStatement(query);
			ps.setString(1, firstName);
			ps.setString(2, lastName);

			// execute the query and get java resultset
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
					String c = rs.getString("name");
					System.out.format("%s\n", c);
					courses.add(c);
			}
			
			if (courses.isEmpty()) {
				System.out.println("No courses.\n");
			}
			
			rs.close();
			ps.close();
			con.close();

		} catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}

		return courses;
	}
	


	public void addCourse(String courseName, String time){
		
		Connection con = DBConnector.getConnection();
		PreparedStatement ps = null; 
		
		String insertSQL = "INSERT INTO Course (`name`, startTime) " + "VALUE (?, ?)";

		try {
			ps = con.prepareStatement(insertSQL);
			ps.setString(1, courseName);
			ps.setString(2, time);

			ps.execute();
			System.out.println("Added " + courseName + " as a new course");

			ps.close();
			con.close();
		}
		
		catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL State: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);
		}
	}

	public void addStudent(String firstName, String lastName, int age) {
		
		Connection con = DBConnector.getConnection();
		PreparedStatement ps = null; 
		
		String insertSQL = "INSERT INTO Student (firstName, lastName, age) VALUE (?, ?, ?)";

		try {
			ps = con.prepareStatement(insertSQL);
			ps.setString(1, firstName);
			ps.setString(2, lastName);
			ps.setInt(3, age);

			ps.execute();
			System.out.format("Added new student %s  %s, age: %d", firstName, lastName, age);

			ps.close();
			con.close();
		}

		catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL State: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);
		}
		
	}

	public void showStudentsCourse(String firstName, String lastName) {
		
		Connection con = DBConnector.getConnection();
		PreparedStatement ps = null; 
		
		String query = "Select firstName, lastName FROM Student WHERE firstName = ? AND lastName = ?";
		
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, firstName);
			ps.setString(2, lastName);

			ResultSet rs = ps.executeQuery();

			if(rs.next()) {
				String fn = rs.getString("firstName");
				String ln = rs.getString("lastName");
				
				System.out.println("Courses that " + firstName + lastName+ " is enrolled in: ");

				listCourses(fn, ln);
			}
			else {
				System.out.println("Student does not exist");
			}
			
			rs.close();
			ps.close();
			con.close();

		} catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}
		
	}
	
//	public int studentIdExists(int id) {
//		
//		Connection con = DBConnector.getConnection();
//		PreparedStatement ps = null; 
//		
//		String query = "Select id FROM Student WHERE id = ?";
//		
//		try {
//			// prepared statement
//			ps = con.prepareStatement(query);
//			ps.setInt(1, id);
//
//			// execute the query and get java resultset
//			ResultSet rs = ps.executeQuery();
//
//			if(rs.next()) {
//				rs.getInt("id");
//			}
//			
//			rs.close();
//			ps.close();
//			con.close();
//
//		} catch (SQLException ex) {
//			String sqlMessage = ex.getMessage();
//			String sqlState = ex.getSQLState();
//			int vendorCode = ex.getErrorCode();
//
//			System.err.println("Exception occurred:");
//			System.err.println("Message: " + sqlMessage);
//			System.err.println("SQL state: " + sqlState);
//			System.err.println("Vendor code: " + vendorCode);
//
//		}
//		return id;
//		
//	}
	
	public static List<Student> listAllStudents() {
		List<Student> students = new ArrayList<>();
		
		Connection con = DBConnector.getConnection();
		
		String query = "Select * FROM Student ORDER BY id";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			System.out.printf("%s %1s %s %2s %27s %-60s", "ID", "", "First name", "", "Last name", 
					"\n==================================================================\n");

			while (rs.next()) {
				int id = rs.getInt("id");
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				// add to array list
				students.add(new Student(id, firstName, lastName));
				
				System.out.printf("%s %3s %-25s %5s %-25s\n", id, "", firstName, "", lastName);
				}
			
			rs.close();
			ps.close();
			con.close();
		}
		catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}
		return students;
	}
	
	public static List<Course> listAllCourses(boolean showCourses) {
		ArrayList<Course> courses = new ArrayList<>();
		
		Connection con = DBConnector.getConnection();

		String query = "Select id, name FROM Course ORDER BY id";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			System.out.printf("%s %7s %-40s", "ID", "Course", 
					"\n=========================================\n");

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				courses.add(new Course(id, name));

				if (showCourses) {
					System.out.printf("%s %1s %-25s\n", id, "", name);
				}
			}
			
			rs.close();
			ps.close();
			con.close();

		} catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}
		return courses;
	}
	
	
	public static List<StudentCourse> listCoursePerStudent(int studentId) {
		List<StudentCourse> studentCourses = new ArrayList<>();

		Connection con = DBConnector.getConnection(); 
		
		String query = "Select studentId, courseId, `name`" + 
				" FROM StudentCourse sc" + 
				" INNER JOIN Course c on sc.courseId = c.id" + 
				" WHERE studentId = ?";

		try {
			// prepared statement
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, studentId);

			// execute the query and get java resultset
			ResultSet rs = ps.executeQuery();
			
			System.out.println("Courses enrolled in: ");

			while (rs.next()) {
				int cid = rs.getInt("courseId");
				int sid = rs.getInt("studentId");
				String name = rs.getString("name");
				studentCourses.add(new StudentCourse (cid, sid, name));
				System.out.format("%s %s\n", cid, name);
			}
			
			if (studentCourses.isEmpty()) {
				System.out.println("No courses");
			}
			
			rs.close();
			ps.close();
			con.close();

		} catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL state: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);

		}

		return studentCourses;
	}
	
	public static void unenrollStudent(int studentId, int courseId) {
		List<StudentCourse> studentCourse = listCoursePerStudent(studentId);

		for (int i = 0; i < studentCourse.size(); i++) {
			StudentCourse sc = studentCourse.get(i);
			if (studentId != sc.getStudentId()) {
				return;
			}
		}
		
		Connection con = DBConnector.getConnection();
		
		String sql = "DELETE FROM StudentCourse WHERE courseId = ? AND studentId = ?";
		
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, courseId);
			ps.setInt(2, studentId);

			ps.executeUpdate();
			
			System.out.println("Student id: " + studentId
					+ " has been sucessfully unenrolled out of course id: " + courseId);

			ps.close();
			con.close();
			
		}catch (SQLException ex) {
			String sqlMessage = ex.getMessage();
			String sqlState = ex.getSQLState();
			int vendorCode = ex.getErrorCode();

			System.err.println("Exception occurred:");
			System.err.println("Message: " + sqlMessage);
			System.err.println("SQL State: " + sqlState);
			System.err.println("Vendor code: " + vendorCode);
		}
	}
}
