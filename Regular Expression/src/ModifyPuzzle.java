import java.util.*;
import java.io.*;

public class ModifyPuzzle {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		PrintWriter out = new PrintWriter(System.out);
		String str = sc.nextLine().toUpperCase();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<str.length();++i)
		{
			char c = str.charAt(i);
			if(c=='-')
				sb.append(' ');
			else if(c==' ')
				sb.append(" ");
			else if(c>='A' && c<='Z')
				sb.append(c);
		}
		out.println(sb.toString());
		String[] s = sb.toString().split(" ");
		sb = new StringBuffer();
		HashSet<String> hSet = new HashSet<>();
		for(int i=0;i<s.length;++i)
		{	
			if(hSet.contains(s[i]))
				continue;
			else
			{
				hSet.add(s[i]);
				sb.append(s[i]+" ");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		out.println(sb.toString());
		sc.close();
		out.close();
	}

}
