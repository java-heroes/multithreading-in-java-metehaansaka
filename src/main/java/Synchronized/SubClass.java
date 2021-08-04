package Synchronized;

public class SubClass extends Thread{
    Functions functions = new Functions();

    @Override
    public void run(){
        for (int i = 0; i < 1000; i++){
            functions.getCount();
        }
    }

}
