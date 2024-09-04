package Receiver;

public class RECcoder
{
	public static String encode(String word) 
	{
		String encoded = "";
		for (Integer index = 0; index < word.length(); index++) 
		{
			int ascii = (int) word.charAt(index);
			encoded += "&#" + ascii + ";";
		}
		return encoded;
	}
	
	/*0-9 false.       A-Z true.       a-z true.        " " "+" "-" "." true.    
	true => replace with &#NCR; 
	false => keep as it is 
	*/
	
	
public static String decode(String word) 
{
	String decoded = "";
	for (Integer index = 0; index < word.length(); index++)
	{
		String charAt = "" + word.charAt(index);
		if (charAt.equals("&") && index < word.length() && ("" + word.charAt(index + 1)).equals("#"))
		{
			try 
			{
				Integer length = word.indexOf(";", index);
				String sub = word.substring(index + 2, length);
				int a = Integer.parseInt(sub);
				char b = (char) a;
				decoded += b;
				index = length;
			} 
			catch (Exception ex) 
			{
				decoded += charAt;
			}
		} 
		else 
		{
			decoded += charAt;
		}
	}
	return decoded;
}


}