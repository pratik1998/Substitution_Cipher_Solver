import java.util.*;
import java.io.*;

public class ChangeDictionary {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File iFile = new File("/home/blackpearl/Desktop/Internship/Words/words.txt");
		FileWriter fw = new FileWriter("/home/blackpearl/eclipse-workspace/Google/src/output.out");
		Scanner sc = new Scanner(new FileReader(iFile));
		PrintWriter out = new PrintWriter(fw);
		while(sc.hasNextLine())
		{
			String str = sc.nextLine();
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<str.length();i++)
			{
				char c = str.charAt(i);
				if(c=='\'')
					continue;
				sb.append(c);
			}
			out.println(sb.toString());
		}
		sc.close();
		out.close();
	}

}
