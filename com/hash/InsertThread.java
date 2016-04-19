package com.hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

//public class InsertThread implements Callable<HashTable>{
public class InsertThread extends Thread{
	
	private HashTable table;
	private String hashMethod;
	private String filename;
	CountDownLatch signal;
	public InsertThread(CountDownLatch signal,HashTable table, String hashMethod,String filename){
		this.table = table;
		this.hashMethod = hashMethod;
		this.filename = filename;
		this.signal = signal;
	}
	public void run(){
		File input = new File(filename);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(input));
			String line = null;
			while((line = reader.readLine()) != null){
					String[] str = line.split("\t");
					String doc_id = str[0];
					String tokens = str[1];
					SimHash hash = new SimHash(tokens, 32, this.hashMethod);
					List<String> indexes = hash.subByDistance(hash,3);
					table.insert(hash.strSimHash, doc_id, indexes, hashMethod);
				}
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader != null){
				try{
					reader.close();
					
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
		}
		signal.countDown();

	}

}
