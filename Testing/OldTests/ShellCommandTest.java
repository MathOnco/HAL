package Testing.OldTests;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellCommandTest {
    public static void main(String[] args) {
        try {
            Process p=Runtime.getRuntime().exec("python ~/Desktop/Pythons/test.py");
            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println(reader.readLine()+"!");
            System.out.println("success");

        } catch (Exception e) {
            System.out.println("failed");
        }
    }
}
