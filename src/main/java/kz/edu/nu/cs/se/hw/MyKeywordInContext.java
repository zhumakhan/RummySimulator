package kz.edu.nu.cs.se.hw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MyKeywordInContext implements KeywordInContext {
	public String path;
	public String name;
	public HashSet<String> stopwords = new HashSet<String>(Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"));
    public List<Indexable> listofwords  = new ArrayList<Indexable> ();
    public List<String> lines = new ArrayList<String>();
    
	public MyKeywordInContext(String name, String pathstring) {
    	this.path = pathstring;
    	this.name = name;
    	BufferedReader objReader = null;
    	try {
			objReader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		}finally{
			if(objReader != null)
				try {
					objReader.close();
				} catch (IOException e) {
					System.out.println(e.toString());
				}
		}
    }
    public int find(String word) {
    	word = word.toUpperCase();
    	int index = Collections.binarySearch(listofwords,new Index(word,0),new Comparator<Indexable>(){
    		public int compare(Indexable i1, Indexable i2){
    			return i1.getEntry().toUpperCase().compareTo(i2.getEntry().toUpperCase());
    		}
    	});
    	if(index < 0)return -1;
    	while(index > -1 && listofwords.get(index).getEntry().toUpperCase().compareTo(word) == 0)index--;    	
    	return listofwords.get(index+1).getLineNumber();
    }
    public Indexable get(int i) {
    	if(i == -1)return null;
    	for(int j = 0; j < listofwords.size(); j++){
    		if(listofwords.get(j).getLineNumber() == i){
    			return listofwords.get(j);
    		}
    	}
    	return null;
    	
    }

    public void txt2html() {
    	File htmlfile = new File(name+".html");
    	FileWriter writer = null;     
    	BufferedReader objReader = null;
    	try {
    		writer = new FileWriter(htmlfile);
    		writer.write("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body><div>\n");
    		String strCurrentLine;
    		objReader = new BufferedReader(new FileReader(path));
    		Integer i = 0;
    		lines.add("Zeros line");
    		while ((strCurrentLine = objReader.readLine()) != null) {
    			i++;
    			writer.write(strCurrentLine+"<span id='line_" + i.toString()+"'>&nbsp&nbsp["+i.toString()+"]</span><br>\n");
    			lines.add(strCurrentLine);
    			String[] words = strCurrentLine.split("\\s+");
    			for(int j = 0; j < words.length; j++){
    				words[j] = words[j].replace(".","").replace(",", "").replace(";","").replace(":","").replace("-","");
    				listofwords.add(new Index(words[j],i));
    			}
    		}
    		writer.write("</div></body></html>");
    		
    		Collections.sort(listofwords, new Comparator<Indexable>() {
    		    public int compare(Indexable ind1, Indexable ind2) {
    		        int temp = ind1.getEntry().toUpperCase().compareTo(ind2.getEntry().toUpperCase());
    		        if(temp == 0){
    		        	return (ind1.getLineNumber() > ind2.getLineNumber()) == true ? 1 : -1;
    		        }
    		        return temp;
    		    }
    		});
    	} catch (IOException e) {
    		System.out.println(e.toString() + path);
    	} finally {
    		try {
    			if (objReader != null)
    				objReader.close();
    			if (writer != null)
    				writer.close();
    	   }catch (IOException ex) {
    		   System.out.println(ex.toString() + " close text2html");
    	   }
    	}
    }

    public void indexLines() {
    	writeIndexToFile();
    }

    public void writeIndexToFile() {
    	FileWriter writer = null;
    	try{
    		writer = new FileWriter(new File("kwic"+name+".html"));
    		writer.write("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body><div style='text-align:center;line-height:1.6'>\n");
    		Indexable ind = null;
    		for(int i = 0; i < listofwords.size(); i++){
    			ind = listofwords.get(i);
    			if(stopwords.contains(ind.getEntry().toLowerCase())){
    				continue;
    			}
    			String line = lines.get(ind.getLineNumber());
    			writer.write(line.replace(ind.getEntry(), " <a href='" + name + ".html#line_"+ String.valueOf(ind.getLineNumber())+ "'>" +ind.getEntry().toUpperCase()+"</a>") + "<br>\n");
    		}
    	}catch(FileNotFoundException ex){
    		System.out.println(ex.toString()+ " write indextofile");
    	}catch(IOException ex){
    		System.out.println(ex.toString()+ " write indextofile");
    	}finally{
    		try {
				writer.close();
			} catch (IOException ex) {
				System.out.println(ex.toString());
			}
    	}
    }
}