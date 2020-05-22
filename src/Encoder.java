import java.io.*;
import java.util.Scanner;

public class Encoder {
	public static void main(String[] args) throws IOException {
		BufferedInputStream original = null;
		BufferedOutputStream compressed = null;
		String filePath = null;
		try {
			Scanner userIn = new Scanner(System.in);
			System.out.println("Enter a filePath to encode: ");
			filePath = userIn.next();
			userIn.close();
			//filePath = "src/original.jpg";
			original = new BufferedInputStream(new FileInputStream(filePath));
			compressed = new BufferedOutputStream(new FileOutputStream(filePath.substring(0, filePath.lastIndexOf("\\")+1)+ "compressed.mzip"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(e.hashCode());
		}

		writeString(compressed, (filePath.substring(filePath.lastIndexOf("\\") + 1)));
		//writeString(compressed, "src/done.jpg");
		writeString(compressed, "\n");

		AllInOneButTree allButTree = new AllInOneButTree();

		int rep;
		while ((rep = original.read()) != -1) {
			allButTree.incrementFreq((byte) rep);
		}

		System.out.println(allButTree);
		allButTree.sort();
		System.out.println(allButTree);

		Tree huffman = new Tree(allButTree);
		System.out.println(huffman);
		System.out.println(allButTree);

		writeString(compressed, huffman.toString());
		writeString(compressed, "\n");

		if (huffman.getPadding() == 0) {
			System.out.println(0);
			writeString(compressed, "" + (0));
		} else {
			System.out.println(8 - huffman.getPadding());
			writeString(compressed, "" + (8 - huffman.getPadding()));
		}

		writeString(compressed, "\n");

		original.close();

		original = new BufferedInputStream(new FileInputStream(filePath));

		byte writeBuffer = 0x0;
		byte writebufferLen = 0;
		while ((rep = original.read()) != -1) {
			int huffRep = allButTree.get((byte) rep);
			byte remainingBytes = 31;
			while (huffRep > 0) {
				remainingBytes --;
				huffRep = huffRep << 1;
			}
			for (byte i = 0; i < remainingBytes; i++) {
				huffRep = huffRep << 1;
				if (huffRep < 0) {
					writeBuffer = (byte) ((writeBuffer << 1) + 1);
				} else {
					writeBuffer = (byte) (writeBuffer << 1);
				}
				writebufferLen++;
				if (writebufferLen == 8) {
					compressed.write(writeBuffer);
					writeBuffer = 0x0;
					writebufferLen = 0;
				}
			}
		}
		if (writebufferLen != 0) {
			writeBuffer = (byte) (writeBuffer << (8 - writebufferLen));
			compressed.write(writeBuffer);
		}

		compressed.close();
	}

	public static void writeString(BufferedOutputStream out, String string) throws IOException {
		char[] write = string.toCharArray();
		for (char c: write) {
			out.write((byte) c);
		}
	}
}
class Node {
	private byte rep;
	private int data;
	private Node left;
	private Node right;
	private Node next;
	private boolean leaf;

	Node (byte rep, int data) {
		this.rep = rep;
		this.data = data;
		this.left = null;
		this.right = null;
		this.next = null;
		this.leaf = true;
	}

	public byte getRep() {
		return rep;
	}

	public void setRep(byte rep) {
		this.rep = rep;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public boolean isLeaf() {
		return this.leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
}

class AllInOneButTree {
	//Uses Node Next for linking
	Node head;
	Node tail;
	int size;

	public int getSize() {
		return size;
	}

	AllInOneButTree() {
		head = null;
		tail = null;
		size = 0;
	}

	//Modified Sentinel search to either increment the freq or add a new Node with freq of 1
	public void incrementFreq(byte rep) {
		size++;
		if (head == null) {
			head = new Node(rep, 1);
			tail = head;
			return;
		}
		Node curNode = head;
		Node sentinel = new Node(rep, 1);
		tail.setNext(sentinel);
		while (curNode.getRep() != rep) {
			curNode = curNode.getNext();
		}
		if (curNode == sentinel) {
			tail = sentinel;
			return;
		}
		curNode.setData(curNode.getData() + 1);
		tail.setNext(null);
		size--;
	}

	public int get(byte rep) {
		if (head == null) {
			return -1;
		}
		Node curNode = head;
		Node sentinel = new Node(rep, -1);
		tail.setNext(sentinel);
		while (curNode.getRep() != rep) {
			curNode = curNode.getNext();
		}
		tail.setNext(null);
		return curNode.getData();
	}

	//Sort least data to greatest using merge sort
	public void sort() {
		if (this.size >= 2) {
			sortRecurse(this);
		}
	}

	private void sortRecurse(AllInOneButTree list) {
		if (list.getSize() == 2) {
			if (list.tail.getData() < list.head.getData()) {
				Node tail = list.tail;
				list.tail = list.head;
				list.head = tail;
				list.tail.setNext(null);
				list.head.setNext(list.tail);
			}
			return;
		} else if (list.getSize() == 1) {
			return;
		}
		int middle = list.getSize()/2;
		AllInOneButTree one = new AllInOneButTree();
		AllInOneButTree two = new AllInOneButTree();
		Node curNode = list.head;
		for (int i = 0; i < middle - 1; i++) {
			curNode = curNode.getNext();
		}
		one.head = list.head;
		one.tail = curNode;
		one.size = middle;
		two.head = curNode.getNext();
		two.tail = list.tail;
		two.size = list.getSize()-one.size;

		one.tail.setNext(null);

		if (one.size >= 2) {
			sortRecurse(one);
		}
		if (two.size >= 2) {
			sortRecurse(two);
		}
		if (one.head.getData() < two.head.getData()) {
			list.head = one.head;
			one.head = one.head.getNext();
			list.head.setNext(null);
			list.tail = list.head;
			one.size--;
		} else {
			list.head = two.head;
			two.head = two.head.getNext();
			list.head.setNext(null);
			list.tail = list.head;
			two.size--;
		}
		while (one.size > 0 && two.size > 0) {
			if (one.head.getData() < two.head.getData()) {
				list.tail.setNext(one.head);
				list.tail = one.head;
				one.head = one.head.getNext();
				list.tail.setNext(null);
				one.size--;
			} else {
				list.tail.setNext(two.head);
				list.tail = two.head;
				two.head = two.head.getNext();
				list.tail.setNext(null);
				two.size--;
			}
		}
		if (one.size > 0) {
			list.tail.setNext(one.head);
			list.tail = one.tail;
		} else if (two.size > 0) {
			list.tail.setNext(two.head);
			list.tail = two.tail;
		}
	}

	@Override
	public String toString() {
		Node curNode = head;
		String out = "";
		while (curNode != null) {
			out += (char) curNode.getRep() + " " + curNode.getData() + ", ";
			curNode = curNode.getNext();
		}
		return out;
	}

	public void enqueue(Node node) {
		if (size == 0) {
			tail = node;
			head = tail;
			size ++;
			return;
		}

		tail.setNext(node);

		tail = node;
		size++;
	}

	//dequeue min first
	public Node dequeue() {
		if (size == 0) {
			return null;
		}
		if (size == 1) {
			size = 0;
			Node ret = head;
			head = null;
			tail = null;
			return ret;
		}
		Node minNode = head;
		Node curNode = head;
		Node priorToMinNode = null;
		while (curNode.getNext() != null) {
			if (curNode.getNext().getData() < minNode.getData()) {
				priorToMinNode = curNode;
				minNode = curNode.getNext();
			}
			curNode = curNode.getNext();
		}
		if (priorToMinNode != null) {
			if (minNode.getNext() == null) {
				tail = priorToMinNode;
				priorToMinNode.setNext(null);
			} else {
				priorToMinNode.setNext(minNode.getNext());
			}
		} else {
			head = minNode.getNext();
		}
		minNode.setNext(null);
		size--;
		return minNode;
	}
}

class Tree {

	Node head;
	int padding;
	int freq;

	Tree (AllInOneButTree queue){
		huffmanBuilder(queue);
		//data is 0x001XXXX where x is huffman rep
		assignBinaryAndBackToQueue(queue, head, 0x1);
	}

	private void huffmanBuilder(AllInOneButTree queue) {
		while (queue.size > 1) {
			Node left = queue.dequeue();
			Node right = queue.dequeue();
			int data = 0;
			if (left != null) {
				data += left.getData();
			}
			if (right != null) {
				data += right.getData();
			}
			Node parent = new Node((byte) -1 , data);
			parent.setLeaf(false);
			parent.setLeft(left);
			parent.setRight(right);
			queue.enqueue(parent);
		}
		this.head = queue.head;
		queue.dequeue();
	}

	private void assignBinaryAndBackToQueue(AllInOneButTree queue, Node e, int encoded) {
		if (e == null) {
			return;
		}
		if (!e.isLeaf()){
			assignBinaryAndBackToQueue(queue, e.getLeft(), encoded<<1);
			assignBinaryAndBackToQueue(queue, e.getRight(),(encoded<<1) | 0x1);
		} else {
			padding += (e.getData() * (32 - Integer.numberOfLeadingZeros(encoded) - 1)) % 8;
			e.setData(encoded);
			queue.enqueue(e);
		}
	}

	public int getPadding() {
		padding = padding % 8;
		return padding;
	}

	@Override
	public String toString() {
		return recursivePrintNode(head);
	}

	private String recursivePrintNode(Node e) {
		if (e == null) {
			return "";
		}
		if (!e.isLeaf()){
			return "(" + recursivePrintNode(e.getLeft()) + " " + recursivePrintNode(e.getRight()) + ")";
		}
		return ""+(e.getRep());
	}
}