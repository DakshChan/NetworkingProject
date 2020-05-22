import java.io.*;
import java.util.Scanner;

public class Decoder {
	public static void main(String[] args) throws IOException {
		BufferedInputStream compressed = null;
		BufferedOutputStream decompressed = null;
		String huffmanString = "";
		byte padding = 0;
		try {
			Scanner user = new Scanner(System.in);
			System.out.println("Enter path: ");
			String filePath = user.nextLine();
			user.close();
			compressed = new BufferedInputStream(new FileInputStream(filePath));
			String outPath = "";
			char c = 0;
			while (c != '\n') {
				c = (char) compressed.read();
				outPath += c;
			}
			outPath = outPath.substring(0, outPath.length()-1);
			decompressed = new BufferedOutputStream(new FileOutputStream(outPath));
			c = 0;
			while (c != '\n') {
				c = (char) compressed.read();
				huffmanString += c;
			}
			huffmanString = huffmanString.substring(0, huffmanString.length()-1);
			String paddingString = "";
			c = 0;
			while (c != '\n') {
				c = (char) compressed.read();
				paddingString += c;
			}
			paddingString = paddingString.substring(0, paddingString.length()-1);
			padding = Integer.valueOf(paddingString).byteValue();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(e.hashCode());
		}

		System.out.println(huffmanString);
		HuffmanTree huffman = new HuffmanTree(huffmanString);
		System.out.println(huffman);

		int buffer = 0;
		int temp = 0;
		byte bufferSize = 0;
		HuffmanTree.Node priorNode = huffman.head;
		while ((temp = compressed.read()) != -1) {
			byte c = (byte) temp;
			//System.out.println("in");
			for (byte i = 0; i < 8; i++) {
				if (c >= 0) {
					//System.out.println("0");
					priorNode = huffman.traverse(priorNode, (byte) 0);
				} else {
					//System.out.println("1");
					priorNode = huffman.traverse(priorNode, (byte) 1);
				}
				c = (byte) (c << 1);
				if (priorNode.isLeaf) {
					//System.out.println(priorNode.rep);
					decompressed.write(priorNode.rep);
					priorNode = huffman.head;
				}
			}
		}

		compressed.close();
		decompressed.close();
	}

	static class HuffmanTree {

		Node head;

		class Node {
			byte rep;
			boolean isLeaf;
			Node left;
			Node right;
			Node(byte rep, boolean isLeaf) {
				this.rep = rep;
				this.isLeaf = isLeaf;
				this.left = null;
				this.right = null;
			}
		}

		HuffmanTree(String huffmanString) {
			head = new Node((byte) -1, false);
			recursiveBuildHuffman(huffmanString, head);
		}

		//(((32 87) (72 69)) (76 (79 (82 68))))
		private void recursiveBuildHuffman(String huffmanString, Node parent) {
			huffmanString = huffmanString.substring(1, huffmanString.length()-1);
			String leftString = "";
			String rightString = "";
			boolean parseLeft = true;
			int bracketCount = 0;
			while (parseLeft) {
				char c = huffmanString.charAt(0);
				huffmanString = huffmanString.substring(1);
				if (c == ' ' && bracketCount == 0) {
					parseLeft = false;
				} else if (c == '(') {
					bracketCount++;
				} else if (c == ')') {
					bracketCount--;
				}
				leftString += c;
			}
			leftString = leftString.substring(0, leftString.length()-1);
			rightString = huffmanString;
			if (leftString.contains("(")) {
				parent.left = new Node((byte) -1, false);
				recursiveBuildHuffman(leftString, parent.left);
			} else {
				parent.left = new Node(Integer.valueOf(leftString).byteValue(), true);
			}
			if (rightString.contains("(")) {
				parent.right = new Node((byte) -1, false);
				recursiveBuildHuffman(rightString, parent.right);
			} else {
				parent.right = new Node(Integer.valueOf(rightString).byteValue(), true);
			}
		}

		public Node traverse(Node e, byte b) {
			if (b == 0) {
				return e.left;
			} else {
				return e.right;
			}
		}

		@Override
		public String toString() {
			return recString(head);
		}

		private String recString(Node e) {
			if (e.isLeaf) {
				return "" + e.rep;
			} else {
				return "(" + recString(e.left) + " " +  recString(e.right) + ")";
			}
		}
	}
}
