package kz.edu.nu.cs.se.hw;
public class Index implements Indexable{
    private String entry;
    private int linenumber;
    public Index(String e, int ln){
        entry = e;
        linenumber = ln;
    }
    public String getEntry(){
        return entry;
    }
    public int getLineNumber(){
        return linenumber;
    }
}