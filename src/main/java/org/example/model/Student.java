package org.example.model;

public class Student {

    public int id;
    public String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void print() {
        System.out.println("id: " + id);
        System.out.println("name: " + name + "\n");
    }

    @Override
    public String toString() {
        return id + " " + name;
    }
}
