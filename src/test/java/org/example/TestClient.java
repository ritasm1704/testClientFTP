package org.example;

import org.example.client.MyJSONParser;
import org.example.model.Student;
import org.example.model.Students;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TestClient {

    @Test
    public void checkReadingWriting() {

        String fileName1 = "test.json";
        String fileName2 = "test2.json";
        MyJSONParser myJSONParser = new MyJSONParser(fileName1);
        myJSONParser.toStudents();
        myJSONParser.toFile(fileName2);

        MyJSONParser myJSONParser2 = new MyJSONParser(fileName2);
        myJSONParser2.toStudents();

        List<Student> students1 = myJSONParser.getStudents().getStudents();
        List<Student> students2 = myJSONParser2.getStudents().getStudents();

        Assert.assertEquals(students1.size(), students2.size(), "Different size");

        for (int i = 0; i < students1.size(); i++) {
            Assert.assertEquals(students1.get(i).toString(),students2.get(i).toString(), "Failed check Reading and Writing");
        }
    }

    @Test
    public void checkStudents() {

        Students students = new Students();

        students.addStudent(new Student(1, "Student1"));
        students.addNewStudent("Student1");
        students.addNewStudent("Student2");

        List<Student> students1 = students.getStudents();
        Assert.assertEquals(students1.size(), 3, "bad size");

        students1 = students.getStudentByName("Student1");
        Assert.assertEquals(students1.size(), 2, "bad size");

        students.deleteStudent(1);
        students1 = students.getStudents();
        Assert.assertEquals(students1.size(), 2, "bad size");

        Assert.assertNull(students.getStudentByID(1), "getStudentByID failed");
        Assert.assertEquals(students.getStudentByID(2).name, "Student1", "getStudentByID failed");
    }
}
