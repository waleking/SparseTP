package util;

import java.io.*;
import java.util.ArrayList;

/**
 * 封装文件的操作,需要重构
 * 将InputStreamReader强制设置成UTF-8的
 * @author Administrator
 *
 */
public class MyFile {
    private String fileName = null;

    public String getFileName() {
        return fileName;
    }

    private File file = null;
    private String flag = "";// "r"或"w"，代表读或者写
    private BufferedReader br = null;
    private PrintWriter pw = null;

    public PrintWriter getPw() {
        return pw;
    }

    public MyFile(String fileName, String flag) {

        if (flag.equals("r")) {
            this.flag = flag;
            this.fileName = fileName;
            try {
                file = new File(fileName);
                //this.br = new BufferedReader(new FileReader(file));
                this.br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (flag.equals("w")) {
            this.flag = flag;
            this.fileName = fileName;
            try {
                file = new File(fileName);
                this.pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写字符串到文件 to do:有个问题要解决，什么时候关闭文件？
     *
     * @param toPrint
     */
    public void println(String toPrint) {
        if (flag.equals("w")) {
            this.pw.println(toPrint);
        } else {
            System.out.println("this is a file for reading, cannot be written");
        }
    }

    /**
     * 从文件中按照行来读字符串 to do:有个问题要解决，什么时候关闭文件？
     *
     * @return 一行字符串，如果有异常返回null
     */
    public String readln() {
        if (flag.equals("r")) {
            try {
                return this.br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("this is a file for reading, cannot be written");
        }
        return null;
    }

    /**
     * 关闭文件
     */
    public void close() {
        if (this.flag.equals("r")) {
            try {
                this.br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (this.flag.equals("w")) {
            try {
                this.pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写字符串到文件 to do:有个问题要解决，什么时候关闭文件？
     *
     * @param toPrint
     */
    public void print(String toPrint) {
        if (flag.equals("w")) {
            this.pw.print(toPrint);
        } else {
            System.out.println("this is a file for reading, cannot be written");
        }
    }

    public static void combineTwoFileIntoOne(String sourceA,String sourceB, String target){
        String content="";

        MyFile fileA=new MyFile(sourceA,"r");
        ArrayList<String> listA=fileA.readAll();
        fileA.close();

        MyFile fileB=new MyFile(sourceB,"r");
        ArrayList<String> listB=fileB.readAll();
        fileB.close();

        content=content.trim();//filter out extra \n in tail
//          System.out.println(content);

        MyFile fileC=new MyFile(target,"w");
        for(int i=0;i<listA.size();i++){
            fileC.print(listA.get(i));
            if(i!=(listA.size()-1))
                fileC.print("\n");
        }
        for(int i=0;i<listB.size();i++){
            fileC.print(listB.get(i));
            if(i!=(listB.size()-1))
                fileC.print("\n");
        }
        fileC.close();
    }

    /**
     * 读取文件中全部内容
     */
    public ArrayList<String> readAll(){
        ArrayList<String> listLines=new ArrayList<String>();
        try{
            String line=br.readLine();
            while(line!=null){
                //content=content+line+"\n";
                listLines.add(line);
                line=br.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<listLines.size();i++){
            listLines.get(i);
        }

        return listLines;
    }


    /**
     * create an empty file on hard disk
     * @param fileName
     */
    public static void initPhysicalEmptyFile(String fileName){
        MyFile fileEmpty=new MyFile(fileName,"w");
        fileEmpty.print("");
        fileEmpty.close();
    }

    /**
     * count lines in the file
     * @throws Exception
     */
    public int countLines() throws Exception {
        int lineNum = 0;
        if (this.flag.equals("r")) {
            String str = this.readln();
            while (str != null) {
                lineNum++;
                str = this.readln();
            }
            this.close();
            try {
                //reopen again
                this.br = new BufferedReader(new FileReader(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            throw new Exception("The file is not readable, please make sure the flag is r");
        }
        return lineNum;
    }

    public static void main(String args[]) {
        // MyFile file=new MyFile("toOutput","w");
        // file.println("hahhah");
        // file.close();
        MyFile reader = new MyFile("pku_test_gold.txt", "r");
        // String line=reader.readln();
        // while(line!=null){
        // System.out.println(line);
        // line=reader.readln();
        // }
        try{
            System.out.println(reader.countLines());
            System.out.println(reader.countLines());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}