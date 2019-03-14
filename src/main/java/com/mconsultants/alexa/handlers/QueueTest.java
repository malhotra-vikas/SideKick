package main.java.com.mconsultants.alexa.handlers;

import javax.swing.plaf.synth.SynthDesktopIconUI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QueueTest {
    LinkedList queue = new LinkedList();

    public void addToQueue(int i) {
        queue.add(0, i);
    }

    public void popFromQueue() {
        queue.pop();
    }

    public void palindromeCheck(String input) {
        char[] inputArray = input.toCharArray();
        int len = inputArray.length;
        char[] outputArray = new char[len];
        String output;

        for (int i=len; i>0; i--) {
            outputArray[i] = inputArray[i];
        }

        output = new String(outputArray);
        if (input.equalsIgnoreCase(output)) {
            System.out.print("Palindrome");
        }
    }

    public static void main(String args[]) {
        QueueTest queueTest = new QueueTest();
        queueTest.palindromeCheck("radar");
    }
}
