package Sender;

import java.io.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import java.util.Base64;

import java.io.BufferedReader; 
import java.util.ArrayList;

import java.util.HashMap; 
import java.util.List;


public class Sender_Main
{		

	public static void main(String[] args) throws NoSuchAlgorithmException, Exception 
	{
		//Sender Begins
		System.out.print("*****Sender begins*****\n\n");
		long start = System.currentTimeMillis();
		
			//Numbers to Eng alphabet. input is the name of HashMap
		HashMap<Character, Character>input = new HashMap<>();
		input.put('0','A');		input.put('1','B');		input.put('2','C');		input.put('3','D');		input.put('4','E');
		input.put('5','F');		input.put('6','G');		input.put('7','H');		input.put('8','I');		input.put('9','J');
			
			//HTML NCR Eng-Hin
		HashMap<Integer, Integer>input1 = new HashMap<>();
		input1.put(  65, 2309 ); input1.put(  66, 2349 ); input1.put(  67, 2330 ); input1.put(  68, 2342 );
		input1.put(  69, 2311 ); input1.put(  70, 2347 ); input1.put(  71, 2327 ); input1.put(  72, 2361 );
		input1.put(  73, 2312 ); input1.put(  74, 2332 ); input1.put(  75, 2325 ); input1.put(  76, 2354 );
		input1.put(  77, 2350 ); input1.put(  78, 2344 ); input1.put(  79, 2379 ); input1.put(  80, 2346 );
		input1.put(  81, 2326 ); input1.put(  82, 2352 ); input1.put(  83, 2360 ); input1.put(  84, 2340 );
		input1.put(  85, 2313 ); input1.put(  86, 2357 ); input1.put(  87, 2348 ); input1.put(  88, 2331 );
		input1.put(  89, 2351 ); input1.put(  90, 2343 ); input1.put(  32,   32 ); input1.put(  46, 2404 );
		
			//HTML NCR Eng-Fre
		HashMap<Integer, Integer>input2 = new HashMap<>();
		input2.put( 2309, 192 ); input2.put(  2349,  66 ); input2.put( 2330, 264 ); input2.put( 2342,  68 );
		input2.put( 2311, 206 ); input2.put(  2347,  70 ); input2.put( 2327, 284 ); input2.put( 2361,  72 );
		input2.put( 2312,  73 ); input2.put(  2332,  74 ); input2.put( 2325,  67 ); input2.put( 2354,  76 );
		input2.put( 2350,  77 ); input2.put(  2344,  78 ); input2.put( 2379,  79 ); input2.put( 2346,  80 );
		input2.put( 2326,  75 ); input2.put(  2352,  82 ); input2.put( 2360, 199 ); input2.put( 2340,  84 );
		input2.put( 2313,  85 ); input2.put(  2357,  86 ); input2.put( 2348,  87 ); input2.put( 2331, 201 );
		input2.put( 2351,  89 ); input2.put(  2343, 270 ); input2.put(   32,  32 ); input2.put( 2404,  46 );
		
		
		//*****input format: <card number>.<exp date in MM/YY>.<First Name>.<Last Name>.<CVV>.<misc info>*****

		
			//Read Card data to be encrypted
		BufferedReader br = new BufferedReader(new FileReader("C:/test/Sender/f1_InputPlainText.txt"));
		String lineStr = br.readLine();
		br.close();
		System.out.println("Input data is: " + lineStr + "\n");
					
		
		//Character Encoding Begins
			
				//Converts all numbers in input data into English letters (Masked data)
			String mask[] = lineStr.split(Pattern.quote("."));
			String cardNum = ""; 
			String CVV = "";
			String PIN = "";
			for (int index = 0; index < mask[0].length(); index++) 
			{
				char a = mask[0].charAt(index);
				cardNum += input.get(a);
				if(index < 4)
				{
					a = mask[1].charAt(index);
					PIN += input.get(a);
					if(index < 3)
					{
						a = mask[4].charAt(index);
						CVV += input.get(a);
					}
				}
			}
			int cutoff = mask[0].length() + 1 + mask[1].length() + 1 + mask[2].length() + 1 + mask[3].length() + 1 + mask[4].length() + 1; 
			String masked = cardNum + "." + PIN + "." + mask[2] + "." + mask[3] +"." + CVV + "." + lineStr.substring(cutoff);
			
			System.out.println("Masked Text data is: " + masked + '\n');
			
				
				//Converts masked data into HTML NCR String (ASCII type)
			String encoded = SENDcoder.encode(masked); 	
			System.out.println("UTF-8 Encoded data is: " + encoded + '\n');
			
			
				//string list with &# ; removed
			String[] s = encoded.split(";");
					
				//Generates String lists with English to Hindi to French converted NCR
			
			
			List<String> listEng = new ArrayList<String>();
			List<String> listHin = new ArrayList<String>();
			List<String> listFre = new ArrayList<String>();
			
			String hindicoded = "";
			String frenchcoded = "";
			String encout = "";
			for (int i = 0; i < s.length; i++) 
			{
				listEng.add(s[i].substring(2));
				if( input1.containsKey(Integer.parseInt(listEng.get(i)) ) ) 
				{
					listHin.add(Integer.toString(input1.get(Integer.parseInt(listEng.get(i)))));
					listFre.add(Integer.toString(input2.get(Integer.parseInt(listHin.get(i)))));
				}
				else
				{
					listHin.add(listEng.get(i));
					listFre.add(listEng.get(i));
				}
				hindicoded += "&#" + listHin.get(i) + ";";
				frenchcoded += "&#" + listFre.get(i) + ";";
				encout += (char)(Integer.parseInt(listFre.get(i)));
			}
			
			
			System.out.println("Corresponding Hindi text UTF-8 value: " + hindicoded + '\n');
	
			System.out.println("Corresponding French text UTF-8 value: " + frenchcoded + '\n');	  
			  
		//Character Encoding Ends		
		
		
		
		System.out.println("AES Input: " + encout + '\n');
			
		// AES Encryption Begins
		
				//Encrypts Final French characters with AES - 128 Galois Counter Mode
			byte[] AEScipher = Sender.AES_GCM_Encrypt.main(encout);
			
			System.out.print("AES Output: " + Base64.getEncoder().encodeToString(AEScipher) + "\n\n");
			
	    		//Reading AES keys to pad with AES Cipher text
			br = new BufferedReader(new FileReader("C:/test/Sender/f2_keyAES.txt"));
			lineStr = br.readLine();
			br.close();
	        
			
				//Pads AES Key and GCM IV to AES Cipher Text
			String finalstring = "1" + lineStr;
			int i = 0;
			while( i < AEScipher.length)
			{
				finalstring += String.format("%03d" ,((AEScipher[i]+256) % 256)); 
				i++;
			}
			
        // AES Ends
		
			
			
			
			
			
		System.out.println("RSA Input: " + finalstring + '\n');        
		
		//RSA Encryption Begins
			
			
				//Requesting Receiver to generate Keys for RSA
	        Receiver.RSA.keygen();
			
	        
				//Reading Public Key sent by Receiver
			br = new BufferedReader(new FileReader("C:/test/Sender/f3_publicKeyRSA.txt"));
			lineStr = br.readLine();
			br.close();
			
			String[] s1 = lineStr.split(Pattern.quote("."));
			BigInteger e = new BigInteger(s1[0]);
			BigInteger modn = new BigInteger(s1[1]);
			
				
				//Converting AES output for RSA (String to Integer)
			
			BigInteger finalline = new BigInteger(finalstring);
			
			
				//RSA Encryption command
			BigInteger c = finalline.modPow(e, modn);
			
			String RSAcipher = c.toString();
			
			System.out.println("RSA Cipher Text: " + RSAcipher + '\n');			

		//RSA ENDS
		
			//Writing Final RSA cipher text at Receiver's place. High Security RSA 2048 cipher text generated. 
		File f5 = new File(("C:/test/Receiver/f5_Input_cipher_text.txt")); 
		FileOutputStream fos = new FileOutputStream(f5); 
		OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); 
		BufferedWriter w = new BufferedWriter(osw);
		w.write(RSAcipher);
		w.close();
		
			//Finish
		long stop = System.currentTimeMillis();
		System.out.print("Total time taken by Sender: " + (stop - start) + " milliseconds" + "\n\n");
		System.out.print("*****Sender Ends*****" + "\n\n\n");
		
		
			//Wait before calling Receiver
		Thread.sleep(3000);
		
			//Prompt the Receiver
		Receiver.Receiver_Main.main(args);
	}	
}