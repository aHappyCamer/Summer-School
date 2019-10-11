/****************************************************************************
* Create the SummerSchool inventory database and all associated tables         *
*****************************************************************************/

DROP DATABASE IF EXISTS SummerSchool;

CREATE DATABASE SummerSchool;

USE SummerSchool;

CREATE TABLE Student(
	id INT NOT NULL AUTO_INCREMENT,
	firstName VARCHAR(25) NOT NULL,
	lastName VARCHAR(25) NOT NULL,
	age INT NOT NULL,

	PRIMARY KEY (id)
);

CREATE TABLE Course(
	id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(25) NOT NULL,
    startTime TIME NOT NULL,
    
    PRIMARY KEY(id)
);
    
CREATE TABLE StudentCourse(
	studentId INT,
    courseId INT,
    
    FOREIGN KEY(studentId) REFERENCES Student(id),
    FOREIGN KEY(courseId) REFERENCES Course(id)
);

INSERT INTO Student 
	(id, firstName, lastName, age)
VALUES
	(1, 'John', 'Johnson', 10),
	(2, 'Bob', 'Bobson', 11),
	(3, 'Maddie', 'Maddison', 15),
	(4, 'Mary', 'Molson', 12),
	(5, 'Ed', 'Edison', 13),
	(6, 'Mike', 'Molson', 11);
    
INSERT INTO Course
	(id, name, startTime)
VALUES
	(1, 'Swimming', '9:00'),
	(2, 'Tennis', '11:00'),
	(3, 'Soccer', '13:00');

INSERT INTO StudentCourse
	(studentId, courseId)
VALUES
	(1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (3, 1),
    (3, 2),
    (3, 3),
    (4, 1),
    (4, 3),
    (5, 1),
    (5, 3),
    (6, 2),
    (6, 3);
    
