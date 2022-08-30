package org.example;

import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class MyTest {

    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        List<String> tests = new ArrayList<String>();
        tests.add(System.getProperty("user.dir") + "\\testng.xml");
        testNG.setTestSuites(tests);
        testNG.run();
    }
}
