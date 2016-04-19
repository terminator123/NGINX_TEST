package com.hash;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SimHash {

    private String tokens;
    private BigInteger intSimHash;
    public String strSimHash;
    private int hashbits = 32;
    private String hashname;
    public SimHash(String tokens) {
        this.tokens = tokens;
        this.strSimHash = this.simHash();
    }
    public SimHash(String tokens, int hashbits,String hashname) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.hashname = hashname;
        this.strSimHash = this.simHash();
      
    }
    public String simHash() {
        int[] v = new int[this.hashbits];
        StringTokenizer stringTokens = new StringTokenizer(this.tokens);
        while (stringTokens.hasMoreTokens()) {
            String temp = stringTokens.nextToken();
            BigInteger t = BigInteger.valueOf(HashUtils.getHashCode(temp,hashname));
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                 if (t.and(bitmask).signum() != 0) {
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            }else{
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
        this.intSimHash = fingerprint;
        //return fingerprint;
        return this.strSimHash;
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(
                    new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }
    
    /**
     * 取两个二进制的异或，统计为1的个数，就是海明距离
     * @param other
     * @return
     */

    public int hammingDistance(SimHash other) {
    	
        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;
        
        //统计x中二进制位数为1的个数
        //我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，对吧，然后，n&(n-1)就相当于把后面的数字清0，
        //我们看n能做多少次这样的操作就OK了。
        
         while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    /** 
     * calculate Hamming Distance between two strings 
     *  二进制怕有错，当成字符串，作一个，比较下结果
     * @author  
     * @param str1 the 1st string 
     * @param str2 the 2nd string 
     * @return Hamming Distance between str1 and str2 
     */  
    public static int getDistance(String str1, String str2) {  
        int distance;  
        if (str1.length() != str2.length()) {  
            distance = -1;  
        } else {  
            distance = 0;  
            for (int i = 0; i < str1.length(); i++) {  
                if (str1.charAt(i) != str2.charAt(i)) {  
                    distance++;  
                }  
            }  
        }  
        return distance;  
    }
    
    /**
     * 如果海明距离取3，则分成四块，并得到每一块的bigInteger值 ，作为索引值使用
     * @param simHash
     * @param distance
     * @return
     */
    public List<String> subByDistance(SimHash simHash, int distance){
    	int numEach = this.hashbits/(distance+1);
    	List<String> characters = new ArrayList();
    	StringBuffer buffer = new StringBuffer();
    	for( int i = 0; i < this.hashbits; i++){
        	boolean sr = simHash.intSimHash.testBit(i);
        	if(sr){
        		buffer.append("1");
        	}	
        	else{
        		buffer.append("0");
        	}
        	if( (i+1)%numEach == 0 ){
            	buffer.delete(0, buffer.length());
            	characters.add(buffer.toString());
        	}
        }
    	return characters;
    }

    public static void main(String[] args) {
    	String s = "淘宝 促销 近期";
        SimHash hash1 = new SimHash(s, 32,"RSHash");
        hash1.subByDistance(hash1, 3);

    }
}