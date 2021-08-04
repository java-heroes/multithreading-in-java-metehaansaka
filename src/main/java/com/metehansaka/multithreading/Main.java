package com.metehansaka.multithreading;

public class Main {
    public static void main(String[] args) {
        SubClass subClass = new SubClass();

        Thread thread1 = new Thread(subClass);
        Thread thread2 = new Thread(subClass);

        thread1.start();
        thread2.start();

    }
}
