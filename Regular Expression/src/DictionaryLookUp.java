import java.util.*;
import java.io.*;

public class DictionaryLookUp {

	static FileWriter fw;
	static PrintWriter out;
	static int[] firstcand;
	static ArrayList<String> candidateList[];
	static int counter = -1;
	
	//Removes unnecessary punctuation
	public static String modifyPuzzle(String str)
	{
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
		//out.println(sb.toString());
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
		//out.println(str);
		return sb.toString();
	}
	
	//Convert any string to it's canonical Form
	public static String convertToCanonicalForm(String str)
	{
		HashMap<Character,Character> hMap = new HashMap<>();
		StringBuffer ans = new StringBuffer();
		char start='A';
		for(int i=0;i<str.length();i++)
		{
			if(!hMap.containsKey(str.charAt(i)))
			{
				hMap.put(str.charAt(i), start);
				start++;
			}
			ans.append(hMap.get(str.charAt(i)));
		}
		return ans.toString();
	}
	
	
	//Checking both mapping set is same or not
	public static boolean isSame(HashSet<Character>[] map,HashSet<Character>[] newMap)
	{
		for(int i=0;i<26;i++)
		{
			for(char z:map[i])
			{
				if(newMap[i].contains(z))
					continue;
				else
					return false;
			}
		}
		return true;
	}
	
	//Checking mapping is consistent with candidate word
	public static boolean isConsistent(HashSet<Character>[] map,String cipherText,String plainText)
	{
		for(int i=0;i<cipherText.length();i++)
		{
			if(!map[cipherText.charAt(i)-'A'].contains(plainText.charAt(i)))
				return false;
		}
		return true;
	}
	
	//Checking Mapping
	public static void printMappings(HashSet<Character>[] map)
	{
		for(int i=0;i<26;i++)
		{
			char c = 'A';
			System.out.print((char)(c+i)+" -->  ");
			for(char z:map[i])
				System.out.print(z+" ");
			System.out.println();
		}
	}
	
	
	//Self-Intersection Algorithm to reduce number of candidate for cipherText
	@SuppressWarnings({ "unchecked" })
	public static HashSet<Character>[] selfIntersection(HashSet<Character>[] map, String[] str,HashMap<String,ArrayList<String>> hMap)
	{
		//Input:- MCDMRCNSFX MSCNPPRX
		int t = str.length;
		while(t--!=0)
		{
			String cipherWord = str[str.length-t-1];
			HashSet<Character>[] newMap = new HashSet[26];
			for(int i=0;i<26;i++)
				newMap[i] = new HashSet<>();
			
			ArrayList<String> candidateWords = candidateList[str.length-t-1];
			boolean[] cipherLetter = new boolean[26];

			for(int i=firstcand[str.length-t-1];candidateWords!=null && i<candidateWords.size();i++)
			{
				String sb = new String(candidateWords.get(i)).toLowerCase();
				//condition that checks mapping is consistent with ancestor nodes
				if(isConsistent(map, cipherWord, sb))
				{
					//System.out.println(sb);
					for(int j=0;j<sb.length();j++)
					{
						cipherLetter[cipherWord.charAt(j)-'A']=true;
						newMap[cipherWord.charAt(j)-'A'].add(sb.charAt(j));
					}
					continue;
				}
				else {
					Collections.swap(candidateWords, firstcand[str.length-t-1], i);
					firstcand[str.length-t-1]++;
				}
			}
			
			//Intersection between newMap and map
			for(int k=0;k<26;k++)
			{
				if(cipherLetter[k])
					map[k].retainAll(newMap[k]);
			}
		}
		return map;
	}
	
	//Checks each cipher letter has found its mapping or not
	public static boolean allCipherTextKnown(HashSet<Character>[] map)
	{
		for(int i=0;i<26;++i)
		{
			if(map[i].size()!=1 && map[i].size()!=0)
				return false;
		}
		return true;
	}
	
	//Converts cipher text into plain text and prints plain text using given mapping
	public static void printSolution(String str,HashSet<Character>[] map)
	{
		for(int i=0;i<str.length();i++)
		{
			char c = str.charAt(i);
			if(c>='A' && c<='Z')
			{
				for(char z:map[c-'A'])
					out.print(z);
			}
			else
				out.print(c);
		}
		out.println();
	}
	
	@SuppressWarnings("unchecked")
	public static void solveRecursive(String input, HashSet<Character>[] map, String[] str,HashMap<String,ArrayList<String>> hMap,int selectedWord)
	{
		//Printing Full Solution
		if(allCipherTextKnown(map))
		{
			printSolution(input, map);
			return;
		}
		ArrayList<String> candidateWords = new ArrayList<>();
		
		//Self-Intersection Algorithm
		HashSet<Character>[] tmp = new HashSet[26];
		do {
			for(int i=0;i<26;i++)
				tmp[i] = (HashSet<Character>)map[i].clone();
			selfIntersection(map, str, hMap);
		}while(!isSame(tmp,map));
		
		int[] firstcandtmp = new int[firstcand.length];
		for(int j=0;j<firstcand.length;j++)
			firstcandtmp[j] = firstcand[j];
		
		//Adding Mapping for current Node
		//int selectedWord = planner(map);
		for(int i=firstcand[selectedWord];i<candidateList[selectedWord].size();i++)
			candidateWords.add(candidateList[selectedWord].get(i));
		boolean hasChild = false;
		for(int i=0;candidateWords!=null && i<candidateWords.size();i++)
		{
			String plainWord = candidateWords.get(i).toLowerCase();
			String cipherWord = str[selectedWord];
			if(isConsistent(map, cipherWord, plainWord))
			{
				for(int k=0;k<26;k++)
					tmp[k] = (HashSet<Character>)map[k].clone();
				boolean[] checker = new boolean[26];
				for(int j=0;j<cipherWord.length();j++)
				{
					if(!checker[cipherWord.charAt(j)-'A'])
					{
						checker[cipherWord.charAt(j)-'A']=true;
						HashSet<Character> hSet = new HashSet<>();
						hSet.add(plainWord.charAt(j));
						tmp[cipherWord.charAt(j)-'A'] = hSet;
					}
				}
				solveRecursive(input, tmp, str, hMap,selectedWord+1);
				hasChild = true;
			}

			//Popping firstcand off from stack
			for(int j=0;j<firstcand.length;j++)
				firstcand[j] = firstcandtmp[j];
		}
		//Reporting Partial Solutions
		if(!hasChild)
			System.out.println(firstcand[selectedWord]+" "+candidateList[selectedWord].size()+"Partial Solution");
	}
	
	public static int planner(HashSet<Character>[] map)
	{
		return ++counter;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		
		// TODO Auto-generated method stub
		
		long start = System.currentTimeMillis();
		File iFile = new File("/home/blackpearl/Desktop/Internship/Words/usa.txt");
		fw = new FileWriter("/home/blackpearl/git/Substitution_Cipher_Solver/Regular Expression/src/Output.out");
		out = new PrintWriter(fw);
		Scanner sc = new Scanner(new FileReader(iFile));
		HashMap<String,ArrayList<String>> hMap = new HashMap<>();
		while(sc.hasNextLine())
		{
			String str = sc.nextLine();
			String canonical = convertToCanonicalForm(str);
			if(hMap.containsKey(canonical))
				hMap.get(canonical).add(str);
			else
			{
				ArrayList<String> tmp = new ArrayList<>();
				tmp.add(str);
				hMap.put(canonical,tmp);
			}
		}
		sc.close();
		long end = System.currentTimeMillis();
		out.println("Time Taken for Preprocessing is: "+(end-start)+" ms");
		

		System.out.println("Pre-processing is completed.");
		System.out.println("Enter your ciphertext:");
		
		//CipherText Input
		Scanner in = new Scanner(System.in);
		String input = in.nextLine().toUpperCase();
		String str[] = modifyPuzzle(input).split(" ");
		
		candidateList = new ArrayList[str.length];
		
		for(int i=0;i<str.length;++i)
		{
			//System.out.println(str[i]);
			candidateList[i] = (ArrayList<String>) hMap.get(convertToCanonicalForm(str[i])).clone();
		}
//		Searching Time Complexity(Experimental)
//		start = System.currentTimeMillis();
//		String[] cipherText = "CSOVATTJVGSTH ZBAL VPUTHJ CAJO TAG BJVUYGBJ GPJ YUOG SG XAOJO GPJ ZEGEBJ".split(" ");
//		for(int i=0;i<cipherText.length;i++)
//		{
//			int count = 0;
//			for(int j=0;j<hMap.get(convertToCanonicalForm(cipherText[i])).size();j++)
//				count++;
//			out.println(cipherText[i]+" "+count+" "+hMap.get(convertToCanonicalForm(cipherText[i])).size());
//		}
//		end  = System.currentTimeMillis();
//		out.println("Time Taken for searching is: "+(end-start)+" ms");
//		out.flush();

		//Initialization of Mapping
		HashSet<Character>[] map = new HashSet[26];
		System.out.println("Enter number of clues");
		int clues = Integer.parseInt(in.nextLine());
		for(int i=0;i<26;i++)
			map[i] = new HashSet<>();
		while(clues!=0)
		{
			clues--;
			String[] tmp = in.nextLine().split(" ");
			map[tmp[0].charAt(0)-'A'].add(tmp[1].charAt(0));
		}
		for(int i=0;i<26;i++)
		{
			char c = 'a';
			if(map[i].size()==0 && input.contains((char)('A'+i)+""))
			{
				for(int j=0;j<26;j++)
				{
					map[i].add((char) (c+j));
				}
			}
		}
		
		firstcand = new int[str.length];
		Arrays.fill(firstcand, 0);
		
		solveRecursive(input, map, str, hMap,0);
		
		for(int i=0;i<str.length;++i)
			System.out.println(str[i]+" "+(candidateList[i].size()-firstcand[i]));
		
		//printMappings(map);
		//Self-Interaction algorithm loop
//		HashSet<Character>[] tmp = new HashSet[26];
//		do {
//		for(int i=0;i<26;i++)
//			tmp[i] = (HashSet<Character>)map[i].clone();
//			selfIntersection(map, str, hMap);
//		}while(!isSame(tmp,map));

//		printMappings(map);
//		out.print(allCipherTextKnown(map));
//		printSolution(input, map);
//		out.println(Arrays.toString(firstcand));
//		printMappings(tmp);
		
		//To print plain text
		/*for(int i=0;i<str.length;i++)
		{
			for(int j=0;j<str[i].length();j++)
			{CSOVATTJVGSTH
				int tmp = str[i].charAt(j)-'A';
				Object[] tmpChar = map[tmp].toArray();
				if(tmpChar.length==1)
					out.print((char)tmpChar[0]);
			}
			out.print(" ");
		}*/
		in.close();
		out.close();
	}
}


/* Worst Input:- WKH TXLFN EURZQ IRA MXPSV RYHU WKH ODCB GRJ 
 * Output:- The quick brown fox jumped over the lazy dog*/
/*Original Input Puzzle:- CSOVATTJVGSTH ZBAL VPUTHJ CAJO TAG BJVUYGEBJ GPJ YUOG. SG XAOJO GPJ ZEGEBJ.
 * Output:- Disconnecting from change does not recapture the past it loses the future*/
/* Input:- LT CDDCAYVGLYQ XCOUG'Y ZGCSZ, PVLFX W XCCA 
 * Output:- If opportunity doesn't knock, build a door*/
/*  
 * VJEVMJ OJJU NE AJ LJXQOUJU XELJ EPNJO NSFO NSJR OJJU NE AJ QOGNLHZNJU.
 * people need to be reminded more often than they need to be instructed.
 * 
 * UBTY LZM VZ DY XZQ J KZG DYRTQADTU, D RBDYN J VZZS RBDYV RZ JEN DE DX RBTL TATQ OQTEE PBJQVTE. 
 * WHEN YOU GO IN FOR A JOB INTERVIEW, I THINK A GOOD THING TO ASK IS IF THEY EVER PRESS CHARGES.
 * 
 * ZYFPQOUSB EZO SEP PHHPLS AH PJULUSUKW SZJPKSO GEULE UK DQAODPQACO LUQLCTOSZKLPO GACJY EZFP JZUK YAQTZKS.
 * ADVERSITY HAS THE EFFECT OF ELICITING TALENTS WHICH IN PROSPEROUS CIRCUMSTANCES WOULD HAVE LAIN DORMANT.
 * 
 * FEET WKGEC VP EDU EM BWU NUPB HCBVAOUP EM TCUPP EDU AHD YUHC VD PEAVUBI.
 * GOOD HUMOR IS ONE OF THE BEST ARTICLES OF DRESS ONE CAN WEAR IN SOCIETY.
 * 
 * KT KRAY GZWZCI VJ IJVHAVJRBA HRNZ KZ DGBWCZKI HRNZ KZ PBGO HRNZ KZ JEZ KBIJ VWIJGMIZ XGTDJBHGVK BG JEZ KBIJ RAJGRXVJZ VAVCTIRI VAY R VK RA KT BPA DGBDZG VJKBIDEZGZ.
 * MY MIND REBELS AT STAGNATION GIVE ME PROBLEMS GIVE ME WORK GIVE ME THE MOST ABSTRUSE CRYPTOGRAM OR THE MOST INTRICATE ANALYSIS AND I AM IN MY OWN PROPER ATMOSPHERE
 * 
 * U YAK’S QPZJJB OPP SEP ECQYJPO. U OPKOP SEPT JUMP Z TPTAQB.
 * I DON’T REALLY SEE THE HURDLES. I SENSE THEM LIKE A MEMORY.
 * 
 * ANLE GVG JXM NCOOVWLXM KLD EJ ENM JENMO NCOOVWLXM ? "V NLZM QD MDM JX DJC."
 * WHAT DID ONE HURRICANE SAY TO THE OTHER HURRICANE ? "I HAVE MY EYE ON YOU."
 * 
 * ML MP CFSS LA OF IN OFVADF BEGODFEQ, VAD PIXT TEOMLP XAYLDMOILF LA TFESLT, CFESLT EYB CMPBAJ.
 * IT IS WELL TO BE UP BEFORE DAYBREAK, FOR SUCH HABITS CONTRIBUTE TO HEALTH, WEALTH AND WISDOM.
 * 
 * AQDAGCABUA CT TKFAHVCBP DAKDZA BKGFWZZJ KYHWCB WLHAG CH CT HKK ZWHA HK YA KL WBJ YABALCH HK HVAF
 * EXPERIENCE IS SOMETHING PEOPLE NORMALLY OBTAIN AFTER IT IS TOO LATE TO BE OF ANY BENEFIT TO THEM
 * 
 * IDYLY AX BOCBMX BU YBXM XZOKJAZU IZ YGYLM DKFBU JLZWOYF-UYBI, JOBKXAWOY,BUT CLZUE.
 * THERE IS ALWAYS AN EASY SOLUTION TO EVERY HUMAN PROBLEM—NEAT, PLAUSIBLE, AND WRONG.
 * 
 * */