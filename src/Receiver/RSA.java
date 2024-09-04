package Receiver;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class RSA
{
	
	static int m1 = 0;
	static int m2 = 0;
	static final BigInteger ONE = BigInteger.ONE;
	static final BigInteger TWO = BigInteger.valueOf(2);
	static BigInteger p = BigInteger.ONE;
	static BigInteger q = BigInteger.ONE; 
	static BigInteger modn = BigInteger.ONE;
	static BigInteger lambda = BigInteger.ONE;
	
		// First 100 Primes for Sieve Of Eratosthenes Test
	static int first_primes_list[] = {	   3,   5,   7,  11,  13,  17,  19,  23,  29, 
            31,  37,  41,  43,  47,  53,  59,  61,  67,  71,
            73,  79,  83,  89,  97, 101, 103, 107, 109, 113,
           127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
           179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
           233, 239, 241, 251, 257, 263, 269, 271, 277, 281,
           283, 293, 307, 311, 313, 317, 331, 337, 347, 349,
           353, 359, 367, 373, 379, 383, 389, 397, 401, 409,
           419, 421, 431, 433, 439, 443, 449, 457, 461, 463,
           467, 479, 487, 491, 499, 503, 509, 521, 523, 541};

	
	public static void keygen() throws FileNotFoundException, IOException
	{
			//Generates Two 1024 bit Prime numbers
		primeGEN();
			
			//Generates Modulus and Carmichael Function (lambda) using Euler's Totient Function
		modn = p.multiply(q);
		BigInteger gcd = (p.subtract(ONE)).gcd((q.subtract(ONE)));
		lambda = ((p.subtract(ONE)).multiply(q.subtract(ONE))).divide(gcd);
				
		
			//Generates encryption and decryption exponent
		BigInteger e = BigInteger.valueOf(65537);
		BigInteger d = e.modInverse(lambda);
		
			//Converts variables to string for writing 
		String encrypt = e.toString();
		String decrypt = d.toString();
		String modulus = modn.toString();
		String lambdaFunc = lambda.toString();
		
			//Writes Public Key to Sender's location
		File f3 = new File ("C:/test/Sender/f3_publicKeyRSA.txt");
		FileOutputStream fos = new FileOutputStream(f3);
		OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
		Writer w = new BufferedWriter(osw);
		w.write(encrypt + "." + modulus);
		w.close();

			//Writes Private Key to Receiver's location
		File f4 = new File("C:/test/Receiver/f4_privateKeyRSA.txt");
		fos = new FileOutputStream(f4);
		osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
		w = new BufferedWriter(osw);
		w.write(decrypt + "." + modulus + "." + lambdaFunc);
		w.close();
		osw.close();
		fos.close();
	}
	
	
	public static void primeGEN()
	{
		String center = "134826985114673693079697889309176855021348273420672992955072560868299506854125722349531357991805652015840085409903545018244092326610812466869635572979605593283325920068649113957226664700934570589589812214063754326628613011756847161105434832905620427872512883013439723679960434453859787228626517247218168102911";
		p = new BigInteger(center);
		q = new BigInteger(center);
		
		boolean toss = ThreadLocalRandom.current().nextBoolean();
		if (toss == true)
		{
			p = p.subtract(BigRandGEN());
			q = q.add(BigRandGEN());		
		}
		else 
		{
			p = p.add(BigRandGEN());
			q = q.subtract(BigRandGEN());
		}
		
		boolean passP = false;
		boolean passQ = false;
		do
		{
			if (!passP) 
			{
				if ( p.mod(TWO) == BigInteger.ZERO ) p = p.subtract(ONE);
				 
				if(lowIsPrime(p))
				{
					passP = p.isProbablePrime(64);
				}
				if(!passP)
				{
					if(toss) p = p.add(TWO);
					else p = p.subtract(TWO);
				}
			}
			
			if (!passQ) 
			{
				if ( q.mod(TWO) == BigInteger.ZERO) q = q.subtract(ONE);
				
				if(lowIsPrime(q))
				{
					passQ = q.isProbablePrime(64);
				}
				if(!passQ)
				{
					if(!toss) q = q.add(TWO);
					else q = q.subtract(TWO);
				}
			}
		}
		while ((passP && passQ) == false);
	}
	
	// 64 iterations of Miller-Rabin Probabilistic test + 1 Lucas Deterministic Prime test
	// Combined Primality Probability >> 99.9999999999999999999999999999999999997 %
	// RSA 2048
	
	private static BigInteger BigRandGEN()
	{
		
		byte[] b = new byte[127];
	    ThreadLocalRandom.current().nextBytes(b);
	    BigInteger bigInt = new BigInteger(b);
	    bigInt = bigInt.abs();
	    return bigInt;
	}
	
	// Large Random Number Generator
	
	private static boolean lowIsPrime (BigInteger abc)
	{

		for( int i=0; i < first_primes_list.length; i++ )
		{
			BigInteger j = BigInteger.valueOf(first_primes_list[i]);
			if( abc.mod(j) == BigInteger.ZERO )
			{
				return (false);
			}
		}
		return (true);
	}
	
	// Sieve of Atkin Test
	
}