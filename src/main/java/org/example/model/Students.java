package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Students {

    private List<Student> students = new ArrayList<Student>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addNewStudent(String name) {
        int max = 0;

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).id > max) {
                max = students.get(i).id;
            }
        }

        Student student = new Student(max + 1, name);
        students.add(student);

        System.out.println("new student added");
    }

    public boolean deleteStudent(int id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).id == id) {
               students.remove(i);
               return true;
            }
        }
        return false;
    }

    public ArrayList<Student> getStudentByName(String name) {
        ArrayList<Student> res = new ArrayList<Student>();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).name.equals(name)) {
                res.add(students.get(i));
            }
        }
        if (res.size() > 0) {
            return res;
        } else {
            return null;
        }
    }

    public Student getStudentByID(int id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).id == id) {
                return students.get(i);
            }
        }
        return null;
    }

    public List<String> toListString(List<String> listOfStrings) {
        List<String> res = new ArrayList<String>();
        res.add(listOfStrings.get(0));
        res.add(listOfStrings.get(1));

        for (int i = 0; i < students.size(); i++) {

            res.add("    {");
            res.add("      \"id\": " + students.get(i).id +",");
            res.add("      \"name\": \"" + students.get(i).name +"\"");

            if (i == students.size() - 1) {
                res.add("    }");
            } else {
                res.add("    },");
            }
        }
        res.add(listOfStrings.get(listOfStrings.size() - 2));
        res.add(listOfStrings.get(listOfStrings.size() - 1));

        return res;
    }

    public List<Student> getStudents() {
        return students;
    }
}
