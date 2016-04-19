package com.hash;
import java.util.*;

import javax.swing.table.TableStringConverter;
public class HashTable {

	public String hashmethod;
	//<sub_codes,<hashcodes,idlist>>
	public List<Map<String,Map<String,List<String>>>> sub_tables = null;
	
	public HashTable(String hashmethod){
		this.hashmethod = hashmethod;
	}
	public void init(){
		sub_tables = new ArrayList<Map<String,Map<String,List<String>>>>();
		for(int i=0; i<4; i++){
			Map<String,Map<String,List<String>>> table = new HashMap<String,Map<String,List<String>>>();
			sub_tables.add(table);
		}
	}

	public boolean insert(String hashcode,String doc_id, List<String> indexes,String hashMethod){
		for(int i=0; i<4; i++){
			if(!sub_tables.get(i).containsKey(indexes.get(1))){ //哈希表里不存在索引
				List<String> list = new ArrayList<String>();
				Map<String,List<String>> map = new HashMap<String,List<String>>();
				list.add(doc_id);
				map.put(hashcode,list);
				sub_tables.get(i).put(indexes.get(i),map);
			}else{
				if(sub_tables.get(i).get(indexes.get(i)).containsKey(hashcode)){ //该索引下面有hashcode
					sub_tables.get(i).get(indexes.get(i)).get(hashcode).add(doc_id);
				}else{
					List<String> list = new ArrayList<String>();
					list.add(doc_id);
					sub_tables.get(i).get(indexes.get(i)).put(hashcode, list);
				}
			}
		}
		return true;
	}
	
	public Set<String> query(String hashcode,List<String> indexes){
		Set<String> matches = new HashSet<String>();
		for(int i=0; i<4; i++){
			Map<String,Map<String,List<String>>> table =  sub_tables.get(i);
			if(table.containsKey(indexes.get(i))){
				Map<String,List<String>> map = table.get(indexes.get(i));
				for (String key : map.keySet()) {  
				    if(SimHash.getDistance(hashcode, key) <= 3){  //海明距离小于3
				    	matches.addAll(map.get(key));
				    }
				}
			}
		}
		return matches;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashTable t = new HashTable("");
		t.init();
		List<String> list = new ArrayList<String>();
		list.add("01100100");
		list.add("00001101");
		list.add("00101111");
		list.add("01001110");
		String code = "01100100000011010010111101001110";
		t.insert(code, "2", list, "");
		Map<String,Integer>  result = new HashMap<String,Integer>();
		List<String> list1 = new ArrayList<String>();
		list1.add("00000100");
		list1.add("00001101");
		list1.add("00101111");
		list1.add("01001110");
		System.out.println(result.values());
		
	}

}


