/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package filehandling;

import java.io.*;
import static java.lang.System.exit;
import java.util.*;
public class Filehandling {
    File fp = new File("D:\\webtech\\contact.txt");
    FileReader fr;
    FileWriter fw;
    public void create() throws Exception {
        if(fp.exists()) 
            System.out.println("File exists");
        else {
            fp.createNewFile();
            System.out.println("File created successfully...");
        }
        fw = new FileWriter(fp);
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter contact name: ");
        line = br.readLine();
        fw.write(line+"-");
        System.out.print("Enter contact number: ");
        line = br.readLine();
        fw.write(line);
        fw.close();
        System.out.println("Contact added Successfully...");
    }
    public void read() throws Exception {
        fr = new FileReader(fp);
        BufferedReader br = new BufferedReader(fr);
        String str;
        if(!fp.exists()) 
            System.out.println("File is empty or doesn't exist. Try creating a new file.");
        else {
            while((str=br.readLine())!=null) {
                System.out.println(str);
            }
        }
        br.close();
        fr.close();
    }
    public void update() throws Exception {
        fr = new FileReader(fp);
        BufferedReader br = new BufferedReader(fr);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String str;
        System.out.println(fp.exists());
        if(!fp.exists()) {
            System.out.println("File is empty or doesn't exist. Try creating a new file.");
        }
        else {
            System.out.print("Enter contact name to be updated: ");
            String name = input.readLine();
            int f=0;
            System.out.print("Enter contact number: ");
            String number = input.readLine();
            while((str=br.readLine())!=null) {
                System.out.println("Yes");
                String[] linesplit = str.split("-");
                if(linesplit[0].equals(name)) {
                    f=1;
                    linesplit[1] = number;
                    fw = new FileWriter(fp);
                    fw.write(linesplit[0]+"-"+linesplit[1]);
                    fw.close();
                }
            }
            if(f==0)
                System.out.println("Contact name doesn't exists");
            else
                System.out.println("Contact updated successfully...");
        }
        fr.close();
        br.close();
    }
    public void delete() throws Exception {
        if(fp.delete())
            System.out.println("File deleted successfully...");
        else
            System.out.println("File deletion failed");
    }
    public static void main(String[] args) throws Exception {
        Filehandling obj = new Filehandling();
        int ch;
        System.out.println("\t\t\tFILE HANDLING");
        System.out.println("\t\t(keep records of your contact number)");
        do {
            System.out.println("***MENU***");
            System.out.println("1.CREATE\n2.READ\n3.UPDATE\n4.DELETE\n5.EXIT");
            System.out.print("Enter your choice: ");
            Scanner sc = new Scanner(System.in);
            ch = sc.nextInt();
            switch(ch) 
            {
                case 1:
                    obj.create();
                    break;
                case 2:
                    obj.read();
                    break;
                case 3:
                    obj.update();
                    break;
                case 4:
                    obj.delete();
                    break;
                case 5:
                    System.out.println("BYE BYE");
                    exit(0);
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        } while(ch!=-1);
    }
    
}
