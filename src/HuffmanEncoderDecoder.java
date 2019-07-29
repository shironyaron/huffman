
/**
 * Assignment 1
 * Submitted by: 
 * Student 1.Shir Yaron 	ID# 311323828
 * Student 2.Meni Beladev 	ID# 307836684
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import base.Compressor;

public class HuffmanEncoderDecoder implements Compressor
{
	protected HuffmanNode root;
	protected PriorityQueue<HuffmanNode> q;

	public HuffmanEncoderDecoder()
	{
		root=new HuffmanNode();
	}

	class NewComparator implements Comparator<HuffmanNode> { 
		//compare func for the priority queue
		public int compare(HuffmanNode one, HuffmanNode two) 
		{ 
			return one.data - two.data; 
		} 
	} 

	@Override
	public void Compress(String[] input_names, String[] output_names)
	{
		Map <String,Integer>character=countTheChar(input_names);
		HuffmanNode root=buildTree(character);
		Map <String,String> charToCodeMap=charToCodeFunc(root, character);
		//write the code to the output file
		try(FileInputStream in1=new FileInputStream(input_names[0]);
				FileOutputStream out=new FileOutputStream(output_names[0]);
				DataOutputStream dos = new DataOutputStream(out);){
			int c;
			writeTree(root,out,dos);

			while((c=in1.read())!=-1) {
				String s=""+(char)c;
				String str=charToCodeMap.get(s);
				for(int i=0;i<str.length();i++) {
					int bit=Character.getNumericValue(str.charAt(i));
					writebit(bit,out);
				}
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	char acc; // Accumulator of bit waiting to be written
	int bitcount;      // How many bits are aready present in the accumulator

	// write a single bit (0/1)
	public void writebit(int bit,FileOutputStream out)
	{
		acc |= (bit << bitcount);
		if (++bitcount == 8)
		{
			try {
				out.write(acc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			acc = 0;
			bitcount = 0;
		}
	}
	private void writeTree(HuffmanNode x,FileOutputStream out,DataOutputStream dos) {
		if (x.left==null&&x.right==null) {
			try {
				dos.writeBoolean(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				dos.writeChars(x.ch);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		else {
			try {
				dos.writeBoolean(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writeTree(x.left,out,dos);
			writeTree(x.right,out,dos);
		}
	}

	protected Map <String,Integer> countTheChar(String []input_names) {
		//build a map that contains all the characters and their frequencies
		Map <String,Integer> character=new TreeMap<String,Integer>();
		try(FileInputStream in=new FileInputStream(input_names[0])){
			int c;
			while((c=in.read())!=-1) {
				String str=""+(char)c;
				if(character.containsKey(str))
					character.put(str, character.get(str)+1);
				else
					character.put(str,1);	
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return character;
	}

	private HuffmanNode buildTree(Map <String,Integer> character) {
		//build priority queue that help us to build the Huffman tree
		q = new PriorityQueue<HuffmanNode>(character.size(), new NewComparator()); 
		for (Map.Entry<String,Integer> entry : character.entrySet()) {
			HuffmanNode node = new HuffmanNode(); 
			node.ch=entry.getKey();
			node.data = entry.getValue();
			node.right=null;
			node.left=null;
			q.add(node); 
		} 
		PriorityQueue<HuffmanNode> q1=q;

		//build the tree
		while (q1.size() > 1) { 
			HuffmanNode first = q1.peek(); 
			q1.poll(); 
			HuffmanNode sec = q1.peek(); 
			q1.poll(); 
			HuffmanNode newNode = new HuffmanNode(); 
			newNode.data = first.data + sec.data; 
			newNode.ch = null;
			newNode.left = first; 
			newNode.right = sec; 
			root = newNode; 
			q1.add(newNode); 
		} 
		return root;
	}

	protected Map <String,String> charToCodeFunc(HuffmanNode root6, Map <String,Integer>character){
		//build a treeMap of the characters and their Huffman code
		Map <String,String> charToCode=new TreeMap<String,String>();
		for (Map.Entry<String,Integer> entry : character.entrySet()) {
			String c=entry.getKey();
			String code=findInTree(c,root6,"",null);
			charToCode.put(c, code);
		}
		return charToCode;
	}

	protected String findInTree(String c, HuffmanNode root1, String str,Character side) {
		//find the code of the character in the tree
		if(root1==null)
			return null;
		if (root1.left == null&& root1.right == null&& root1.ch==c) { 
			str=""+side;
			return str;
		}
		if(root1.left!=null) {
			str=findInTree(c,root1.left, str, '0');
			if (str !=null) {
				if(side!=null)
					str=side+str;
				return str;
			}
		}
		if(root1.right!=null) {
			str=findInTree(c,root1.right, str, '1');
			if(str!=null) {
				if(side!=null)
					str=side+str;
				return str;
			}
		}
		return null;
	}


	@Override
	public void Decompress(String[] input_names, String[] output_names)
	{
		try(FileInputStream in=new FileInputStream(input_names[0]);
				DataInputStream dis = new DataInputStream(in);
				FileOutputStream out=new FileOutputStream(output_names[0]);){
			HuffmanNode root = readTree(dis,in);
			String findCode="";
			int read=0;
			while(read!=-1) {
				read=readbit(in);
				if(read!=-1)
					findCode=findCode+read;
				String findString=findInTree(root,findCode);
				if(findString!=null) {
					try {
						out.write(findString.charAt(0));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(findString.length()>1)
						try {
							out.write(findString.charAt(1));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					findCode="";
				}
			}

		}catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static HuffmanNode readTree(DataInputStream dis,FileInputStream in) {
		boolean isLeaf=true;
		try {
			isLeaf = dis.readBoolean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isLeaf==true) {
			Character temp=null;
			try {
				temp = dis.readChar();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return new HuffmanNode(""+temp, 0, null, null);
		}

		else {
			return new HuffmanNode(null, 0, readTree(dis,in), readTree(dis,in));
		}
	}
	int acc1;   // bits waiting to be extracted
	int bitcount1;        // how many bits are still available in acc1

	int readbit(InputStream in)
	{
		//func that helps read bytes to the file
		if (bitcount1 == 0)
		{
			bitcount1 = 8;
			try {
				acc1 =in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(acc1==-1) {
			return -1;
		}
		--bitcount1;
		return (acc1 >> (7 - bitcount1)) & 1;
	}
	public static String findInTree(HuffmanNode root, String s) 
	{ 
		if(s==null) {
			if(root==null)
				return null;
			if (root.ch==null)
				return null;
			else
				return root.ch;
		}
		if(s.charAt(0)=='1') {
			if(s.length()==1)
				return findInTree(root.right,null);
			return findInTree(root.right,s.substring(1));
		}
		else {
			if(s.length()==1) {
				return findInTree(root.left,null);
			}
			return findInTree(root.left,s.substring(1));
		}
	} 

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names)
	{
		// TODO Auto-generated method stub
		return null;
	}
}