/**
 * This file contains three method of encoding each with their own advantages and disadvantages.
 * Method num 3 is the most preferred  method since it works fairly well on all files.
 * The other two methods are commented out. Some stuff has to be changed in order to use the other two methods
 */

import java.io.*;
import java.util.Scanner;

public class HuffmanDecoder {

	/**
	 * Checks if a string could be parsed as a number or not. Used to find the base case in readTree
	 * @param str The string to be checked
	 * @return true if it is a string representation of number, false otherwise
	 */
	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException e){
			return false;
		}
	}

	/**
	 * Determines the number of occurrences of each character in a string. Used to find the base case in readTree.
	 * @param str the string in which we are looking for the character
	 * @param tar the character that we are counting the number of occurrences of
	 * @return the number of times tar is in str
	 */
	public static int occurrences(String str, String tar) {
		return str.length() - str.replace(tar, "").length();
	}

	/**
	 * Parses the tree and creates an ordered map for sets of bits and their respective value to be save at
	 * @param tree the string representation of the tree, found on the second line of the .mzip file
	 * @param path the path to a particular place in a tree.
	 * @param data The map in which all the information is saved.
	 */
	public static void readTree(String tree, String path, Map data) {

		tree = tree.trim();

		// if it is just a number, base case has been reached
		if (isNumeric(tree)) {
			data.put(path, (byte) Integer.parseInt(tree));
			return;
		}

		// if it is of format (num_1 num_2), base case has been reached
		if (occurrences(tree, "(") == 1 && occurrences(tree, ")") == 1) {
			int space = tree.indexOf(" ");
			data.put(path + "0", (byte) Integer.parseInt(tree.substring(1, space)));
			data.put(path + "1", (byte) Integer.parseInt(tree.substring(space+1, tree.length()-1)));
			return;
		}

		char[] treeArray = tree.toCharArray();
		int first = 0;
		int open = 0;
		int close = 0;
		int index = 1;
		while (open != close || open == 0) { // loop through the string until the number of '(' matched the number of ')'
			if (first == 0 && treeArray[index] == '(') first = index;
			if (treeArray[index] == '(') open++;
			if (treeArray[index] == ')') close++;
			index++;
		}

		// substring the determined part and do the same function on these smaller parts
		if (first == 1) {
			readTree(tree.substring(first, index), path + "0", data);
			readTree(tree.substring(index, tree.length() - 1), path + "1", data);
		} else {
			readTree(tree.substring(1, first), path +  "0", data);
			readTree(tree.substring(first, index), path + "1", data);
		}


	}

	/**
	 * Recreates the Huffman tree
	 * @param data the map containing the information about the sets of bits and their values
	 * @return a tree built based on the info in data
	 */
	public static Tree buildTree(Map data) {
		Tree tree = new Tree();
		for(int i = 0; i < data.size(); i++) {
			tree.add(data.getKey(i), data.getValue(i));
		}
		return tree;
	}

	public static String[] decode(String in) {

		final int BYTE_SIZE = 8;
		String[] data = in.split("\n");
		
		String name = data[0];
		String treeRep = data[1];
		
		Map mapData = new Map();
		readTree(treeRep, "", mapData);
		Tree tree = buildTree(mapData);
		
		int bits = Integer.parseInt(data[2]);
		
		String decrypt = "";
		
		int singleCharInt;

		for(int dakshWusHere = 0; dakshWusHere < data[3].length()-1; dakshWusHere++) {
			singleCharInt = data[3].charAt(dakshWusHere);
			for (int i = BYTE_SIZE - 1; i >= 0; i--) {
				// read 1 bit at a time of the compressed file and traverse the tree
				Byte value = tree.traverse((singleCharInt >>> i) & 1);
				if (value != null) {
					// write the information in the file
					decrypt += "" + (char) value.byteValue();
				}
			}
		}
		singleCharInt = data[3].charAt(data[3].length()-1);
		for (int i = BYTE_SIZE - 1; i >= bits; i--) { // traverse each bit until you get to the excess bits
			Byte value = tree.traverse((singleCharInt >>> i) & 1);
			if (value != null) { // if the ptr in the tree has a value, write it to the file
				decrypt += "" + (char) value.byteValue();
			}
		}
		
		return new String[]{name, decrypt};
	}
}

/**
 * A binary tree class used to create the Huffman tree
 */
class Tree {
	
	Node root;
	
	// a simple pointer that points to the node that was lastly traversed to
	public Node ptr;
	
	Tree() {
		root = new Node();
		ptr = root;
	}
	
	/**
	 * Adds a new node to the tree
	 * @param path the path that the node should follow
	 * @param value the value of the new node
	 */
	public void add(String path, byte value) {
		char[] directions = path.toCharArray();
		Node currentNode = root;
		for(char direction : directions) {
			if (direction == '0') {
				if (currentNode.left == null)
					currentNode.addLeft();
				currentNode = currentNode.left;
			} else if (direction == '1') {
				if (currentNode.right == null)
					currentNode.addRight();
				currentNode = currentNode.right;
			}
		}
		currentNode.value = value;
	}
	
	/**
	 * Traverses the tree one level at a time
	 * @param bit determines which direction to take
	 * @return object of type Byte which could either be null or the value of ptr
	 */
	public Byte traverse(int bit) {
		if (bit == 0) {
			ptr = ptr.left;
		} else if (bit == 1) {
			ptr = ptr.right;
		}
		byte temp = 0;
		if(ptr.value != null) { // if the ptr reaches a node which has a value, it means we have reached the end of this section of the Huffman tree
			temp = ptr.value;
			ptr = root; // reset the pointer
			return temp;
		}
		
		// if the ptr doesn't have any value, simply return null
		return null;
	}
	
	
	
	private class Node {
		public Byte value;
		public Node left;
		public Node right;
		
		Node () {
			this.right = null;
			this.left = null;
		}
		
		public void addLeft() {
			this.left = new Node();
		}
		
		public void addRight() {
			this.right = new Node();
		}
	}
}

/**
 * A custom ordered map class.
 */
class Map {
	
	private Node head;
	private int size;
	
	public Map() {
		this.head = null;
		this.size = 0;
	}
	
	public boolean put(String key, byte value) {
		if (head == null) {
			head = new Node(key, value, null);
			size++;
			return true;
		}
		Node currentNode = this.head;
		while(currentNode.getNext() != null) {
			currentNode = currentNode.getNext();
		}
		currentNode.setNext(key, value);
		size++;
		return true;
	}
	
	public String getKey(int index) {
		Node currentNode = this.head;
		for (int i = 0; i < index; i++) {
			currentNode = currentNode.getNext();
		}
		return currentNode.key;
	}
	
	public byte getValue(int index) {
		Node currentNode = this.head;
		for (int i = 0; i < index; i++) {
			currentNode = currentNode.getNext();
		}
		return currentNode.value;
	}
	
	public int size() {
		return this.size;
	}
	
	public String toString() {
		if (this.head == null) {return "";}
		Node currentNode = this.head;
		String str = "{ " + currentNode.key + ": " + currentNode.value;
		while(currentNode.getNext() != null) {
			currentNode = currentNode.getNext();
			str += ", " + currentNode.key + ": " + currentNode.value;
		}
		return str += " }";
	}
	
	private class Node {
		// since in our map, the key and value won't be overwritten, we can declare them final
		final String key;
		final byte value;
		Node next;
		
		Node(String key, byte value, Node next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		public Node getNext() {
			return this.next;
		}
		
		public void setNext(String key, byte value) {
			this.next = new Node(key, value, null);
		}
		
	}
	
}
