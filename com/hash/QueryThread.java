package com.hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class QueryThread implements Callable<Set<String>>{
	
	private HashTable table;
	private String hashMethod;
	private String tokens;
	private CountDownLatch signal;
	
	public QueryThread(CountDownLatch signal,HashTable table, String hashMethod,String tokens){
		this.table = table;
		this.hashMethod = hashMethod;
		this.tokens = tokens;
		this.signal = signal;
	}
	public Set<String> call(){
		Set<String> result = new HashSet<String>();  //每个doc在这个表中相似的id
		try{
					SimHash hash = new SimHash(this.tokens, 32, this.hashMethod);
					List<String> indexes = hash.subByDistance(hash,3);
					result =  table.query(hash.strSimHash, indexes);
				
		}catch(Exception e){
			e.printStackTrace();
		}
		signal.countDown();
		return result;
	}

}
