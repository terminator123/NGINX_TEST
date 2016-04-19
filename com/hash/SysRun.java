package com.hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SysRun {

	String filename;
	//public String[] hashmethods = {"RSHash", "JSHash", "ELFhash","PJWHash"};
	public String[] hashmethods = {"RSHash", "JSHash", "ELFhash","PJWHash"};
	public List<HashTable> tlist = new ArrayList<HashTable>();
	
	public SysRun(String filename){
		this.filename = filename;
	}
	
	
	public void buildTable(String filename){
		ExecutorService exs = Executors.newCachedThreadPool(); 
		CountDownLatch countSignal = new CountDownLatch(hashmethods.length);
		//System.out.println("length " + hashmethods.length);
		for(int i=0; i<this.hashmethods.length; i++){
			HashTable table = new HashTable(hashmethods[i]);
			table.init();
			tlist.add(table);
			exs.submit(new InsertThread(countSignal,tlist.get(i),hashmethods[i],filename));
		}
		try {
			countSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		exs.shutdown();
	}
	
	//每个文档输入与它海明距离<=3的文章id
	public void query(String filename){
		File input = new File(filename);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(input));
			String line = null;
			while((line = reader.readLine()) != null){
					String[] str = line.split("\t");
					String doc_id = str[0];
					String tokens = str[1];
					List<String> result = new ArrayList<String>();
					ExecutorService exs = Executors.newCachedThreadPool(); 
					CountDownLatch countSignal = new CountDownLatch(hashmethods.length);
					for(int i=0; i<this.hashmethods.length; i++){
						try {
							Future<Set<String>> f = exs.submit(new QueryThread(countSignal,tlist.get(i),hashmethods[i],tokens));
							result.addAll(f.get());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
					try {
						countSignal.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//result 去重复 4个hash函数中 有两个判断一样就返回
					Map<String,Integer> map = new HashMap<String,Integer>();
					for (String temp : result) {  
				           Integer count = map.get(temp);  
				           map.put(temp, (count == null) ? 1 : count + 1); 
				     }  
					for (Entry<String, Integer> entry : map.entrySet()) {  
						if(entry.getValue() < 2){
				        	   map.remove(entry.getKey());
				           }
					} 
					map.remove(doc_id);
					System.out.println(doc_id + "\t" + map.keySet());
					exs.shutdown();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		//读文件
//		if(args.length < 1){
//			System.out.println("please input filename");
//			return;
//		}
		String filename = "docs" ;
		SysRun test = new SysRun(filename);
	
		test.buildTable(filename);
		//System.out.println("build result " + test.tlist.get(0));
		test.query("docs");
		
	}

}