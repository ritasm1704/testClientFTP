package org.example.client;

import org.example.model.Student;
import org.example.model.Students;

import java.io.*;
import java.net.*;
import java.util.*;

public class MyFTPClient {

    public static BufferedReader controlReader = null;
    public static BufferedWriter controlWriter = null;
    public static BufferedReader dataReader = null;
    public static Socket controlSocket = null;
    public static Socket dataSocket = null;
    public static ServerSocket activeSocket = null;
    public static final String line_sep = System.getProperties().getProperty("line.separator");

    public String instruction1 = "modes:\n-->changeToPASV\n-->changeToPORT port\n";
    public String instruction2 = "commands:\n-->getByName name\n-->getByID id\n-->add name\n-->delete id\n-->quit";

    MyJSONParser myJSONParser = null;
    Students students = null;

    public static void main(String[] args)
    {
        String username = "anonymous";
        String password = "anonymous";
        String ip = "127.0.0.1";

        System.out.println("input username:");
        Scanner inLog = new Scanner(System.in);
        username = inLog.nextLine();

        System.out.println("input password:");
        inLog = new Scanner(System.in);
        password = inLog.nextLine();

        System.out.println("input ip:");
        inLog = new Scanner(System.in);
        ip = inLog.nextLine();

        String fileName = "test.json";

        MyFTPClient c = new MyFTPClient();
        c.doConnection(username, password, ip, 21);

        boolean modeIsSelected = false;
        boolean actv = false;
        System.out.println();
        System.out.println(c.instruction1);
        System.out.println(c.instruction2);

        while (true) {
            Scanner in = new Scanner(System.in);
            String[] str = in.nextLine().split(" ");

            if (!modeIsSelected) {
                System.out.println("please select the mode first:");
                if (str[0].equals("changeToPASV")) {
                    c.pasv();
                    c.download(fileName);

                } else if (str[0].equals("changeToPORT")) {
                    c.actv(Integer.parseInt(str[1]));
                    c.download(fileName);
                    actv = true;
                }
                modeIsSelected = true;
            } else {
                if (str[0].equals("quit")) {
                    break;

                } else if (str[0].equals("changeToPASV")) {
                    c.pasv();
                    c.download(fileName);
                    actv = false;

                } else if (str[0].equals("changeToPORT")) {
                    c.actv(Integer.parseInt(str[1]));
                    c.download(fileName);
                    actv = true;

                } else if (str[0].equals("getByName")) {
                    ArrayList<Student> students = c.students.getStudentByName(str[1]);
                    if (students == null) {
                        System.out.println("student not found");
                    } else {
                        for (int i = 0; i < students.size(); i++) {
                            students.get(i).print();
                        }
                    }
                } else if (str[0].equals("getByID")) {
                    Student student = c.students.getStudentByID(Integer.parseInt(str[1]));
                    if (student == null) {
                        System.out.println("student not found");
                    } else {
                        student.print();
                    }
                } else if (str[0].equals("add")) {
                    String strRes = "";
                    for (int i = 1; i < str.length; i++) {
                        if (i != str.length - 1) {
                            strRes += str[i] + " ";
                        } else {
                            strRes += str[i];
                        }
                    }
                    c.students.addNewStudent(strRes);

                } else if (str[0].equals("delete")) {
                    boolean flag = c.students.deleteStudent(Integer.parseInt(str[1]));
                    System.out.println("deletion status: " + flag);
                }
            }
        }
        if (actv) {
            c.actv(5025);
        } else {
            c.pasv();
        }
        c.store(fileName);
        c.quit();
        System.out.println("done");
    }

    public void doConnection(String username, String password, String ip, int port)
    {
        try {
            controlSocket = new Socket(ip, port);
            controlReader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
            controlWriter = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));

            response();
            request("USER " + username + "\r\n");
            response();
            request("PASS " + password + "\r\n");
            response();

        } catch (Exception e) {
            System.out.println("failed connection to " + ip + " " + port);
            System.exit(1);
        }
    }

    public void request(String message) {
        try {
            System.out.println("--> " + message);
            controlWriter.write(message);
            controlWriter.flush();
        } catch (IOException e) {
            System.out.println("IOException bad request");
            System.exit(1);
        }
    }

    public String response() {
        ArrayList<String> response = new ArrayList<String>();
        String line = null;
        do {
            try {
                line = controlReader.readLine();
            } catch (IOException e) {
                System.out.println("IOException bad response");
                e.printStackTrace();
                System.exit(1);
            }
            response.add(line);
        } while (!(line.matches("\\d\\d\\d\\s.*")));

        String responseRes = response.get(0);
        System.out.println("<-- "+ responseRes);

        return responseRes;
    }

    public void pasv() {
        request("PASV" + "\r\n");
        String response = response();
        System.out.println(response);

        int s = response.indexOf("(");
        int f = response.indexOf(")");
        if (s != -1 && f != -1) {
            String IPWithPort = response.substring(s + 1, f);
            String[] octatList = IPWithPort.split(",");
            String ip = "";
            int port = 0;
            for (int i = 0; i < octatList.length; i++) {
                if (i < 3) {
                    ip += octatList[i] + ".";
                } else if (i == 3) {
                    ip += octatList[i];
                } else if (i == 4) {
                    port += 256 * Integer.parseInt(octatList[i]);
                } else if (i == 5) {
                    port += Integer.parseInt(octatList[i]);
                }
            }
            try {
                dataSocket = new Socket(ip, port);
                dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            } catch (Exception e) {
                System.out.println("failed connection to " + ip + " " + port);
            }
        } else {
            System.out.println("failed connection, bad response");
        }
    }

    public void download(String fileName) {
        request("TYPE A" + "\r\n");
        response();
        request("RETR " + fileName + "\r\n");
        response();

        String userInput;
        try {
            FileWriter outFile = new FileWriter(new File(fileName).getName());

            while ((userInput = dataReader.readLine()) != null) {
                outFile.write(userInput + line_sep);
            }

            outFile.close();
            dataReader.close();
            System.out.println("file downloaded successfully");
        } catch (IOException e) {
            System.out.println("failed download file in passive mod");
        }
        response();

        myJSONParser = new MyJSONParser(fileName);
        myJSONParser.toStudents();
        students = myJSONParser.getStudents();
    }

    public void actv(int port) {

        try {
            activeSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("failed active mod, port " + port);
        }
        int x = port / 256;
        int y = port - x * 256;
        request("PORT 127,0,0,1," + x + "," + y + "\r\n");
        response();

        try {
            dataSocket = activeSocket.accept();
            dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("failed active mod, port " + port);
        }
    }

    public void store(String fileName) {
        myJSONParser.toFile(fileName);

        request("TYPE A" + "\r\n");
        response();
        request("STOR " + fileName + "\r\n");
        response();

        BufferedReader rin = null;
        PrintWriter rout = null;

        try {
            rin = new BufferedReader(new FileReader(fileName));
            rout = new PrintWriter(dataSocket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Could not create file streams");
        }

        String s;

        try {
            while ((s = rin.readLine()) != null) {
                rout.println(s);
            }
        } catch (IOException e) {
            System.out.println("Could not read from or write to file streams");
            e.printStackTrace();
        }

        try {
            rout.close();
            rin.close();
        } catch (IOException e) {
            System.out.println("Could not close file streams");
            e.printStackTrace();
        }
        response();
    }

    public void quit() {
        request("QUIT" + "\r\n");
        response();
    }
}
