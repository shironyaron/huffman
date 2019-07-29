

public class HuffmanNode {
	//node class for the tree
	String ch=null; 
	int data=0; 

	HuffmanNode left=null;
	HuffmanNode right=null; 
	
	public HuffmanNode() {
	}
	
	public HuffmanNode(String ch, int data, HuffmanNode left, HuffmanNode right) {
		this.ch=ch;
		this.data=data;
		this.left=left;
		this.right=right;
	}

	public boolean isLeaf() {
		if(left==null&&right==null)
			return true;
		return false;
	}
}