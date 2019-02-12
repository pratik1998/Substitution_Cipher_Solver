import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;


public class RegexDictionarySearch {

	public static int canonicalWords(String searchSpace, String cipherText) throws FileNotFoundException
	{
		StringBuffer pattern = new StringBuffer("\\b");
		final String anyChar = "(\\w)";
		int[] map = new int[26];
		Arrays.fill(map, -1);
		int count = 1;
		for(int i=0;i<cipherText.length();i++)
		{
			int c = cipherText.charAt(i)-'A';
			if(map[c]==-1)
			{
				if(count!=1)
					pattern.append("(?!");
				for(int j=1;j<count;j++)
					pattern.append("\\"+j+"|");
				if(count!=1)
				{
					pattern.deleteCharAt(pattern.length()-1);
					pattern.append(")");
				}
				pattern.append(anyChar);
				map[c] = count++;
			}
			else
				pattern.append("\\"+map[c]);
		}
		pattern.append("\\b");
		System.out.println(pattern.toString());
		Pattern p = Pattern.compile(pattern.toString());
		Matcher m = p.matcher(searchSpace.toString());
		m.find();
		int candidateKeys = 0;
		while(m.find())
			candidateKeys++;
		return candidateKeys;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		File iFile = new File("/home/blackpearl/Desktop/Internship/Words/words.txt");
		FileWriter fw = new FileWriter("/home/blackpearl/eclipse-workspace/Google/src/output.out");
		Scanner sc = new Scanner(iFile);
		StringBuffer searchSpace = new StringBuffer();
		PrintWriter out = new PrintWriter(fw);
		while(sc.hasNextLine())
		{
			String str = sc.nextLine();
			searchSpace.append(str+"\n");
			//out.println(str);
		}
		sc.close();
		long end = System.currentTimeMillis();
//		String tmp = "Disconnecting abc";
		out.println("Time Taken for pre-processing is: "+(end-start)+" ms");
		start = System.currentTimeMillis();
		String[] cipherText = "ABCCD".split(" "); //CSOVATTJVGSTH ZBAL VPUTHJ CAJO TAG BJVUYGEBJ GPJ YUOG SG XAOJO GPJ ZEGEBJ".split(" ");
//		Pattern p = Pattern.compile("\\b(\\w)(?!\\1)(\\w)(?!\\1|\\2)(\\w)(?!\\1|\\2|\\3)(\\w)(?!\\1|\\2|\\3|\\4)(\\w)(?!\\1|\\2|\\3|\\4|\\5)(\\w)\\6(?!\\1|\\2|\\3|\\4|\\5|\\6)(\\w)\\4(?!\\1|\\2|\\3|\\4|\\5|\\6|\\7)(\\w)\\2\\6(?!\\1|\\2|\\3|\\4|\\5|\\6|\\7|\\8)(\\w)\\b");
//		out.println("^(\\w)(?!\\1)(\\w)(?!\\1|\\2)(\\w)(?!\\1|\\2|\\3)(\\w)(?!\\1|\\2|\\3|\\4)(\\w)(?!\\1|\\2|\\3|\\4|\\5)(\\w)\\6(?!\\1|\\2|\\3|\\4|\\5|\\6)(\\w)\\4(?!\\1|\\2|\\3|\\4|\\5|\\6|\\7)(\\w)\\2\\6(?!\\1|\\2|\\3|\\4|\\5|\\6|\\7|\\8)(\\w)$");
//		out.println(Pattern.matches("\\b(\\w)(?!\\1)(\\w)(?!\\1|\\2)(\\w)(?!\\1|\\2|\\3)(\\w)(?!\\1|\\2|\\3|\\4)(\\w)(?!\\1|\\2|\\3|\\4|\\5)(\\w)\\6(?!\\1|\\2|\\3|\\4|\\5|\\6)(\\w)\\4(?!\\1|\\2|\\3|\\4|\\5|\\6|\\7)(\\w)\\2\\6(?!\\1|\\2|\\3|\\4|\\5|\\6|\\7|\\8)(\\w)\\b", tmp));
		//out.println(cipherText[0]+" "+canonicalWords(searchSpace.toString(), cipherText[0]));
		for(int i=0;i<cipherText.length;i++)
			out.println(cipherText[i]+" "+canonicalWords(searchSpace.toString(), cipherText[i]));
		end  = System.currentTimeMillis();
		out.println("Time Taken for searching is: "+(end-start)+" ms");
		out.close();
	}
}
