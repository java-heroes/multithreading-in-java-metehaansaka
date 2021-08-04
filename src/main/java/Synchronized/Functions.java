package Synchronized;

public class Functions {
    int count = 0;

    public synchronized void getCount(){
        System.out.println(count++);
    }
}
