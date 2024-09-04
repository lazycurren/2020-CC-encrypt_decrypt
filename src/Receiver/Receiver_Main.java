package Receiver;

import java.io.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.util.Base64;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;


public class Receiver_Main
{		
	public static void main(String[] args) throws NoSuchAlgorithmException, Exception 
	{
		System.out.print("*****Receiver begins*****\n\n");
		long start = System.currentTimeMillis();
		
			// Numbers to Eng alphabet. input is the name of HashMap
		Map<Character, Character>input = new HashMap<>();
		input.put('0','A');		input.put('1','B');		input.put('2','C');		input.put('3','D');		input.put('4','E');
		input.put('5','F');		input.put('6','G');		input.put('7','H');		input.put('8','I');		input.put('9','J');
		
		Map<Character, Character> inputinverse = input.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		
			//HTML NCR Eng-Hin
		Map<Integer,Integer>input1 = new HashMap<>();
		input1.put(  65, 2309 ); input1.put(  66, 2349 ); input1.put(  67, 2330 ); input1.put(  68, 2342 );
		input1.put(  69, 2311 ); input1.put(  70, 2347 ); input1.put(  71, 2327 ); input1.put(  72, 2361 );
		input1.put(  73, 2312 ); input1.put(  74, 2332 ); input1.put(  75, 2325 ); input1.put(  76, 2354 );
		input1.put(  77, 2350 ); input1.put(  78, 2344 ); input1.put(  79, 2379 ); input1.put(  80, 2346 );
		input1.put(  81, 2326 ); input1.put(  82, 2352 ); input1.put(  83, 2360 ); input1.put(  84, 2340 );
		input1.put(  85, 2313 ); input1.put(  86, 2357 ); input1.put(  87, 2348 ); input1.put(  88, 2331 );
		input1.put(  89, 2351 ); input1.put(  90, 2343 ); input1.put(  32,   32 ); input1.put(  46, 2404 );
		
		Map<Integer, Integer> input1inverse = input1.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		
			//HTML NCR Eng-Fre
		Map<Integer,Integer>input2 = new HashMap<>();
		input2.put( 2309, 192 ); input2.put(  2349,  66 ); input2.put( 2330, 264 ); input2.put( 2342,  68 );
		input2.put( 2311, 206 ); input2.put(  2347,  70 ); input2.put( 2327, 284 ); input2.put( 2361,  72 );
		input2.put( 2312,  73 ); input2.put(  2332,  74 ); input2.put( 2325,  67 ); input2.put( 2354,  76 );
		input2.put( 2350,  77 ); input2.put(  2344,  78 ); input2.put( 2379,  79 ); input2.put( 2346,  80 );
		input2.put( 2326,  75 ); input2.put(  2352,  82 ); input2.put( 2360, 199 ); input2.put( 2340,  84 );
		input2.put( 2313,  85 ); input2.put(  2357,  86 ); input2.put( 2348,  87 ); input2.put( 2331, 201 );
		input2.put( 2351,  89 ); input2.put(  2343, 270 ); input2.put(   32,  32 ); input2.put( 2404,  46 );
		
		Map<Integer, Integer> input2inverse = input2.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		

			//Reading Data Received from Sender
		BufferedReader br = new BufferedReader(new FileReader("C:/test/Receiver/f5_Input_cipher_text.txt"));
		String lineStr = br.readLine();
		br.close();
		
		System.out.println("Received RSA Cipher Text: " + lineStr + "\n");
		
		
		BigInteger c = new BigInteger(lineStr);
		
		//RSA Decryption Begins
				
				//Reading previously generated RSA private keys
			br = new BufferedReader(new FileReader("C:/test/Receiver/f4_privateKeyRSA.txt"));
			lineStr = br.readLine();
			br.close();
			
			String s[] = lineStr.split(Pattern.quote("."));
			BigInteger d = new BigInteger(s[0]);
			BigInteger modn = new BigInteger(s[1]);			
			
			
				//RSA Decryption command
			BigInteger m = c.modPow(d, modn);
			
			String RSAoutput = m.toString();
			
		//RSA Ends
			
		System.out.println("RSA Decrypted Text: " + RSAoutput + '\n');
		

		
			//Obtains AES Main key from RSA Decrypted Text
		RSAoutput = RSAoutput.substring(1);
		
		int i = 0;
		int num;
		byte[] mainkey = new byte[16];
		while(i < 16)
		{
			num = Integer.parseInt(RSAoutput.substring(0, 3));
			mainkey[i] = (byte)(num % 256);
			RSAoutput = RSAoutput.substring(3);
			i++;
		}
		
		
			//Obtains AES GCM IV key from RSA Decrypted Text
		i = 0;
		byte[] IV = new byte[12];
		while(i < 12)
		{
			num = Integer.parseInt(RSAoutput.substring(0, 3));
			IV[i] = (byte)(num % 256);
			RSAoutput = RSAoutput.substring(3);
			i++;
		}
		
			//Obtains AES Cipher Text from RSA Decrypted Text
		int numbytes = (RSAoutput.length() / 3 );
		byte[] AESinput = new byte[numbytes];		
		i = 0;
		while(i < numbytes)
		{
			num = Integer.parseInt(RSAoutput.substring(0, 3));
			AESinput[i] = (byte)(num % 256);
			RSAoutput = RSAoutput.substring(3);
			i++;
		}
		
		
		
		System.out.println("AES Input Text: " + Base64.getEncoder().encodeToString(AESinput) + '\n');
		
		//AES Decryption Begins
		
				//AES Decryption command
		String AESoutput = AES_GCM_Decrypt.main(mainkey, IV, AESinput);
			
		//AES Decryption Ends
		
		
		
		System.out.println("AES Decrypted Text: " + AESoutput + '\n');
		
		//Character Decoding Begins
		
			//Convert AES Decrypted Data to French to Hindi to English characters HTML NCR format
		
		List<String> listFre = new ArrayList<String>();			
		List<String> listHin = new ArrayList<String>();
		List<String> listEng = new ArrayList<String>();
		
		String frenchcoded = "";
		String hindicoded = "";
		String encoded = "";
		for (int j = 0; j < AESoutput.length(); j++) 
		{
			listFre.add(Integer.toString((int)(AESoutput.charAt(j))));
			if( input2inverse.containsKey(((int)(AESoutput.charAt(j))))) 
			{
				listHin.add(Integer.toString(input2inverse.get(Integer.parseInt(listFre.get(j)))));
				listEng.add(Integer.toString(input1inverse.get(Integer.parseInt(listHin.get(j)))));
			}
			else
			{
				listHin.add(Integer.toString((int)(AESoutput.charAt(j))));
				listEng.add(Integer.toString((int)(AESoutput.charAt(j))));
			}
			frenchcoded += "&#" + listFre.get(j) + ";";
			hindicoded += "&#" + listHin.get(j) + ";";
			encoded += "&#" + listEng.get(j) + ";";
		}
		
		System.out.println("Corresponding French text UTF-8 value: " + frenchcoded + '\n');	  
		
		System.out.println("Corresponding Hindi text UTF-8 value: " + hindicoded + '\n');
				
		System.out.println("UTF-8 Encoded data is: " + encoded + '\n');
		
		
			//Convert English NCR String to Characters
		String masked = RECcoder.decode(encoded);
		
		System.out.println("Masked Text data is: " + masked + '\n');
		
			//Get Final decoded data
		String[] finalstring = masked.split(Pattern.quote("."));
		
		String cardNum = ""; 
		String CVV = "";
		String PIN = "";
		
		for (int index = 0; index < finalstring[0].length(); index++) 
		{
			char a = finalstring[0].charAt(index);
			cardNum += inputinverse.get(a);
			if(index < 4)
			{
				a = finalstring[1].charAt(index);
				PIN += inputinverse.get(a);
				if(index < 3)
				{
					a = finalstring[4].charAt(index);
					CVV += inputinverse.get(a);
				}
			}
		}
		
		int cutoff = finalstring[0].length() + 1 + finalstring[1].length() + 1 + finalstring[2].length() + 1 + finalstring[3].length() + 1 + finalstring[4].length() + 1; 
		String Plaintext = cardNum + "." + PIN + "." + finalstring[2] + "." + finalstring[3] +"." + CVV + "." + masked.substring(cutoff);
		
		System.out.println("Plaintext Data is: " + Plaintext + '\n');
		
		
			//Write Plaintext data to file
		File f6 = new File(("C:/test/Receiver/f6_Output_Data.txt")); 
		FileOutputStream fos = new FileOutputStream(f6); 
		OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); 
		BufferedWriter w = new BufferedWriter(osw);
		w.write("Card Number: " + cardNum + '\n'); w.newLine();
		w.write("PIN: " + PIN + '\n'); w.newLine();
		w.write("CVV: " + CVV + '\n'); w.newLine();
		w.write("First Name: " + finalstring[2] + '\n'); w.newLine();
		w.write("Last Name: " + finalstring[3] + '\n'); w.newLine();
		w.write("Remarks: " + masked.substring(cutoff) + '\n');
		w.close();
		osw.close();
		fos.close();
		
			//Finish
		long stop = System.currentTimeMillis();
		System.out.print("Total time taken by Receiver: " + (stop - start) + " milliseconds" + "\n\n");
		System.out.print("*****Receiver Ends*****" + '\n');
	}
}