package Sender;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.*;
import java.lang.String;
import java.nio.charset.StandardCharsets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.net.URL;
import java.net.URLConnection;



public class AES_GCM_Encrypt
{
	private static int Lat = 0;
	private static int Long = 0;

    public static byte[] main(String plainText) throws NoSuchAlgorithmException, Exception, IOException
    {
    		//Get Latitudes and Longitudes
    	latslongs.getLatsLongs();
    	
    		//Generates 128 bit AES key from Coordinates
    	byte[] keybytes = mainKey();
    	
    		//Converts 128 bit Block key into Paralellizable Galois/Counter IV
    	SecretKey key = new SecretKeySpec(keybytes, 0, 16, "AES");
    		
    		//GCM IV randomizer
        byte[] IV = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        
        	//Encrypts Plain Text in AES-GCM format
        byte[] cipherText = encrypt(plainText.getBytes(), key, IV);
    	
        
        	//Converts byte[] Main and IV key into Modulo 256 Integer String Format
    	String[] keystring = new String[2];
    	keystring[0] = "";
    	keystring[1] = "";
 
    	int i = 0;
        while(i < keybytes.length)
        {
        	keystring[0] += String.format("%03d"  ,((keybytes[i]+256) % 256));
        	if( i < IV.length)
        		keystring[1] += String.format("%03d"  ,((IV[i]+256) % 256));
        	i++;
        }
        
        	//Writes Main and IV key into a file for padding by Sender
        File f2 = new File("C:/test/Sender/f2_keyAES.txt");
        FileOutputStream fos = new FileOutputStream(f2);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        Writer w = new BufferedWriter(osw);
        w.write(keystring[0] + keystring[1]);
		w.close();
		osw.close();
		fos.close();
			
			//Return Cipher Text bytes of AES
        return cipherText;
    }

	public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] IV) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        
        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        
        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, IV);
        
        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
        
        // Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);
        
        return cipherText;
    }
        
	private static class latslongs
	{
	
		private static void getLatsLongs() throws IOException, ParseException, NullPointerException
		{
			URL ipapi = new URL("https://ipapi.co/json/");
			URLConnection c = ipapi.openConnection(); 
			c.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); 
			BufferedReader reader = new BufferedReader(	new InputStreamReader(c.getInputStream()));		
			String line; 
			String inline="";
			
			while ((line = reader.readLine()) != null)
			{
				inline += line;
			}
			
			reader.close();
			JSONParser parse = new JSONParser();
			JSONObject jobj = (JSONObject)parse.parse(inline); 
			Double lats = (Double)jobj.get("latitude");
			Double longs = (Double)jobj.get("longitude");
			
			
			String[] s1 = Double.toString(lats).split(Pattern.quote("."));
			String[] s2 = Double.toString(longs).split(Pattern.quote("."));
			int m1 = (int) Math.pow(  10 ,  s1[1].length()  );
			int m2 = (int) Math.pow(  10 ,  s2[1].length()  );
			lats = lats*(m1);
			longs = longs*(m2);
			lats = Math.abs(lats);
			longs = Math.abs(longs);
			Lat = lats.intValue();
			Long = longs.intValue();
			formatter( m1, m2 );
		}
	
			
		private static void formatter (int b1, int b2)
		{
			if( 0 <= Lat && Lat < 10008 )
			{
				int x = b1/10;
				int a3 = ThreadLocalRandom.current().nextInt( x*11 , x*900 + 1 );
				Lat = nearestPrime(a3);
			}
			else Lat = nearestPrime(Lat);
				
			if( 0 <= Long && Long < 10008 )
			{
				int x = b2/10;
				int a3 = ThreadLocalRandom.current().nextInt( x*11 , x*1800 + 1 );
				Long = nearestPrime(a3);
			}
			else Long = nearestPrime(Long);
		}
		
		
		private static int nearestPrime(int num) 
		{
			boolean vis[] = new boolean[num+1];
			for(int i=0; i<=num;i++) 
			{
				vis[i]=false;
			}
			for(int i=2; i<=num; i++)
			{
				int s=i;
				if(!vis[s])
				{
					for(int j=2; j<=num; j++)
					{
						int x= s*j;
						if( x > num)
						{
							break;
						}
						if(!vis[x])
						{
							vis[x] = true;
						}
					}
				}
			}
			int ans = 1;
			for(int i = num; i >= 0; i--) 
			{
				if(!vis[i]) 
				{
					ans= i;
					break;
				}
			}
			return ans;
		}

	}    
    	
	private static byte[] mainKey()
	{
		BigInteger mult = BigInteger.valueOf(Lat*Long);
		byte[] b;
		
		if (mult == BigInteger.ZERO)
		{
			b = new byte[16];
			ThreadLocalRandom.current().nextBytes(b);				
		}
		else
		{
			int fsize = (mult.toString()).length();
			while(fsize < 48)
			{
				mult = mult.multiply(mult);
				fsize = (mult.toString()).length();
			}
			
			String abc = mult.toString();
			b = new byte[16];
			int i = 0;
			while(i < 16)
			{
				int num = Integer.parseInt(abc.substring(0, 3));
				b[i] = (byte)(num % 256);
				abc = abc.substring(3);
				i++;
			}
		}
		return b;
	}

	
}