package com.hash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.text.html.HTMLDocument.Iterator;

public class SysRun {

	String filename;
	public String[] hashmethods = {"RSHash", "APHash", "DJBHash","PJWHash"};
	//public String[] hashmethods = {"APHash"};
	public List<HashTable> tlist = new ArrayList<HashTable>();
	public Set<String> checked = new HashSet<String>();
	
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
		ExecutorService exs = Executors.newCachedThreadPool(); 
		try{
			reader = new BufferedReader(new FileReader(input));
			String line = null;
			while((line = reader.readLine()) != null){
					String[] str = line.split("\t");
					if(str.length < 2)
						continue;
					String doc_id = str[0];
					//如果该doc 已经与其他的比较过，则放弃
					if(checked.contains(doc_id))
						continue;
					String tokens = str[1];
					List<Future<Set<String>>> result = new ArrayList<Future<Set<String>>>();
					
					CountDownLatch countSignal = new CountDownLatch(hashmethods.length);
					for(int i=0; i<this.hashmethods.length; i++){
						try {
							Future<Set<String>> f = exs.submit(new QueryThread(countSignal,tlist.get(i),hashmethods[i],tokens));
							result.add(f);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
//					try {
//						countSignal.await();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					List<String> final_result = new ArrayList<String>();
					for(Future<Set<String>> fs: result){
						Set<String> tmp = fs.get();
						final_result.addAll(tmp);
					}
					//result 去重复 4个hash函数中 有两个判断一样就返回
					Map<String,Integer> map = new HashMap<String,Integer>();
					for (String temp : final_result) {  
				           Integer count = map.get(temp);  
				           map.put(temp, (count == null) ? 1 : count + 1); 
				     }  
					java.util.Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
					while(it.hasNext()){
						Entry<String, Integer> mapentry = it.next(); 
						if(mapentry.getValue() < 2)
							it.remove();
						
					}
					map.remove(doc_id);
					if(!map.isEmpty())
						System.out.println(doc_id + "\t" + map.keySet());
					checked.add(doc_id);
					checked.addAll(map.keySet());
					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		exs.shutdown();
		
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
		test.query("docs");
		
	}

}
