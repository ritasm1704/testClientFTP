package org.example.client;

import org.example.model.Student;
import org.example.model.Students;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MyJSONParser {

    List<String> listOfStrings = new ArrayList<String>();
    Students students = new Students();

    public MyJSONParser(String filename) {

        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));

            String line = bf.readLine();
            while (line != null) {
                listOfStrings.add(line);
                line = bf.readLine();
            }
            bf.close();

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    public void toStudents() {

        Student student = null;

        for (int i = 0; i < listOfStrings.size(); i++) {
            String[] str = listOfStrings.get(i).split("\"");
            if (str.length == 3) {
                if (str[1].equals("id")) {
                    if (student != null) {
                        System.out.println("error in the file");
                    } else {
                        String number = "";
                        char[] chars = str[2].toCharArray();
                        for (int j = 0; j < str[2].length(); j++) {
                            if (Character.isDigit(chars[j])) {
                                number += chars[j];
                            }
                        }
                        student = new Student(Integer.parseInt(number), "");
                    }
                }
            } else if (str.length == 4) {
                if (str[1].equals("name")) {
                    if (student == null) {
                        System.out.println("error in the file");
                    } else if (student.name != "") {
                        System.out.println("error in the file");
                    } else {
                        student.name = str[3];
                        students.addStudent(student);
                        student = null;
                    }
                }
            }

            /*for (String s : str) {
                System.out.print(s + " || ");
            }
            System.out.println();*/
        }
    }

    public void toFile(String fileName) {
        listOfStrings = students.toListString(listOfStrings);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

            for (int i = 0; i < listOfStrings.size(); i++) {

                bw.write(listOfStrings.get(i));
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Students getStudents() {
        return students;
    }
}
