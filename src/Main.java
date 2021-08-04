import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
	/*
	 * to be continued: 
	 * 		make it can run in other size (5x5, 6x6)
	 * 		mainly problem in move() and combine()
	 */
	
	/*
	 * | 0 | 1 | 2 | 3 |
	 * | 4 | 5 | 6 | 7 |
	 * | 8 | 9 | 10| 11|
	 * | 12| 13| 14| 15|
	 */
	static String pattern;
	static int size;
	static int initBlock; //how many block given at the start
	static Block[] blocks;
	
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter the size of frame (N x N): ");
		size = input.nextInt();
		System.out.print("Enter the number of blocks given at the start: ");
		initBlock = input.nextInt();
		blocks = new Block[size*size];
		input.nextLine();
		
		generatePattern();
		initBlocks();
		increase(initBlock);
		boolean end = false;
		String direction;
		while (!end) {
			print();
			do {
				System.out.print("\nEnter next move [L/R/U/D] ");
				direction = input.nextLine();
			}
			while (!move(direction.toUpperCase()));
			end = !increase(1);
		}
		
		input.close();
		boolean win = false;
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].e >= 2048) {
				win = true;
			}
		}
		if (win)
			System.out.println("Congratulation");
		else
			System.out.println("Game Over");
	}

	private static void generatePattern() {
		String temp = "";
		String Line1 = "", Line2_4 = "", Line3 = "";
		for (int i = 0; i < size; i++) {
			Line1 += "+-----";
			Line2_4 += "|     ";
			Line3 += "|%5s";
		}
		Line1 += "+\n";
		Line2_4 += "|\n";
		Line3 += "|\n";
		for (int i = 0; i < size*size; i+=size) {
			temp = temp.concat(Line1);
			temp = temp.concat(Line2_4);
			temp = temp.concat(Line3);
			temp = temp.concat(Line2_4);
		}
		temp = temp.concat(Line1);
		pattern = temp;
	}
	
	private static void print() {
		List<Object> args = new ArrayList<>();
		for (Block b : blocks) {
			if (b.e == 0) {
				args.add(" ");
				continue;
			}
			switch (b.e) {
			case 128:
				args.add("128 ");
				break;
			case 256:
				args.add("256 ");
				break;
			case 512:
				args.add("512 ");
				break;
			case 1024:
				args.add("1024");
				break;
			case 2048:
				args.add("2048 ");
				break;
			default:
				args.add(b.e + "  ");
				break;
			}
		}
		System.out.println(String.format(pattern, args.toArray()));
	}
	
	private static void initBlocks() {
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = new Block();
		}
	}

	private static boolean increase(int num) {
		boolean full = true;
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].e == 0) {
				full = false;
				break;
			}
		}
		if (full) 
			return false;
		
		Random rand = new Random();
		for (int i = 0; i < num; i++) {
			int index;
			do {
				index = rand.nextInt(size*size);
			}
			while(blocks[index].e != 0);
			if (Math.round(Math.random()) == 0)
				blocks[index].e = 2;
			else
				blocks[index].e = 4;
		}
		return true;
	}
	
	private static boolean move(String direction) {
		/*
		 * | 0 | 1 | 2 | 3 |
		 * | 4 | 5 | 6 | 7 |
		 * | 8 | 9 | 10| 11|
		 * | 12| 13| 14| 15|
		 */
		boolean moved = false;
		boolean combined = false;
		if (direction.contains("L")) {
			combined = combine("L");
			for (int i = 0; i < blocks.length; i++) {
				//move the block
				if (blocks[i].e != 0) {
					/*
					 * size = 4,
					 * i = 0,1,2,3 -> j = 0
					 * i = 4,5,6,7 -> j = 4
					 * i = 8,9,10,11 -> j = 8
					 * i = 12,13,14,15 -> j = 12
					 */
					/*
					 * size = 4, j = 0/4/8/12; j < 4/8/12/16; j++
					 * size = 5, j = 0/5/10/15/20; j < 5/10/15/20/25; j--
					 */
					for (int j = ((0+i/size)*size); j < ((1+i/size)*size) && j < i; j++) {
						if (blocks[j].e == 0) {
							blocks[j].e = blocks[i].e;
							blocks[i].e = 0;
							moved = true;
							break;
						}
					}
				}
			}
		}
		else if (direction.contains("R")) {
			combined = combine("R");
			for (int i = size-1; i < blocks.length; i--) {
				//move the block
				if (blocks[i].e != 0) {
					/*
					 * i = 0,1,2,3 -> j = 3
					 * i = 4,5,6,7 -> j = 7
					 * ...
					 * size = 4, j = 3/7//11/15; j >= 0/4/8/12; j++
					 * size = 5, j = 4/9/14/19/24; j < 0/5/10/15/20; j--
					 */
					for (int j = ((1+i/size)*size)-1; j >= ((0+i/size)*size) && j > i; j--) {
						if (blocks[j].e == 0) {
							blocks[j].e = blocks[i].e;
							blocks[i].e = 0;
							moved = true;
							break;
						}
					}
				}
				
				//skip after the left most, i = 0,4,8,12, after moving
				if (i % size == 0) { 
					i += size*2 -1; //go to next line last element
					i++; ///as of i++
					continue;
				}
			}
		}
		else if (direction.contains("U")) {
			combined = combine("U");
			//start = 0,1,2,3 if size = 4 (4 columns)
			int start = 0;
			for (int i = start; start < size; i+=size) {
				// avoid index out of bound
				if (i >= size*size) {
					start++;
					i = start;
					i -= size; //as of i += size 
					continue;
				}
				
				//move the block
				if (blocks[i].e != 0) {
					/*
					 * i = 0,4,8,12 -> j = 0
					 * i = 1,5,9,13 -> j = 1
					 * ...
					 * size = 4, j = 0/1/2/3; j < 12/13/14/15; j+=4
					 * size = 5, j = 0/1/2/3/4; j < 20/21/22/23/24; j-=5
					 */
					for (int j = (i%size); j < (size*(size-1)+(i%size)) && j < i; j+=size) {
						if (blocks[j].e == 0) {
							blocks[j].e = blocks[i].e;
							blocks[i].e = 0;
							moved = true;
							break;
						}
					}
				}
			}
		}
		else if (direction.contains("D")) {
			combined = combine("D");
			/*
			 * size = 4, start = 12 (4*3)
			 * size = 5, start = 20 (5*4)
			 */
			int start = size*(size-1);
			for (int i = start; start < size*size; i-=size) {
				// avoid index out of bound
				if (i < 0) {
					start++;
					i = start;
					i += size; //as of i -= size
					continue;
				}
				//move the block
				if (blocks[i].e != 0) {
					/*
					 * i = 0,4,8,12 -> j = 12
					 * i = 1,5,9,13 -> j = 13
					 * ...
					 * size = 4, j = 12/13/14/15; j >= 0/1/2/3; j-=4
					 * size = 5, j = 20/21/22/23/24; j >= 0/1/2/3/4; j-=5
					 */
					for (int j = (size*(size-1)+(i%size)); j >= (i%size) && j > i; j-=size) {
						if (blocks[j].e == 0) {
							blocks[j].e = blocks[i].e;
							blocks[i].e = 0;
							moved = true;
							break;
						}
					}
				}
			}
		}
		else
			return false;
		
		//pass the move if full (or will trap in infinity loop)
		boolean full = true;
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].e == 0) {
				full = false;
				break;
			}
		}
		if (full) 
			return true;
		
		return (moved || combined);
	}
	
	private static boolean combine(String direction) {
		boolean combined = false;
		/*
		 * the 2 index that will crash together
		 * index1 is the 'i'
		 * index2 is the block closest to the block 'i'
		 * logic is similar to move loop but modified
		 */
		int index1, index2;
		boolean has_index2;
		
		/*
		 * | 0 | 1 | 2 | 3 |
		 * | 4 | 5 | 6 | 7 |
		 * | 8 | 9 | 10| 11|
		 * | 12| 13| 14| 15|
		 */
		if (direction.contains("L")) {
			for (int i = 0; i < blocks.length-1; i++) {
				//skip the right most, i = 3,7,11,15, because nothing to compare
				if ((i+1) % size == 0) 
					continue;
				
				if (blocks[i].e == 0)
					continue;
				
				//find index 2
				has_index2 = false;
				index1 = i;
				index2 = -1;
				for (int m = index1+1; m < ((1+i/size)*size); m++) {
					if (blocks[m].e != 0) {
						index2 = m;
						has_index2 = true;
						break;
					}
				}
				
				/*
				 * combine the block
				 * [0] equals [1], combine to the smaller index(left side) if true
				 * [1] equals [2] at the next loop
				 */
				if (has_index2) {
					if (blocks[index1].equals(blocks[index2])) {
						blocks[index1].e*=2;
						blocks[index2].e = 0;
						combined = true;
					}
				}
			}
		}
		else if (direction.contains("R")) {
			for (int i = size-1; i < blocks.length; i--) {
				//skip the left most, i = 0,4,8,12, because nothing to compare
				if (i % size == 0) { 
					i += size*2 -1; //go to next line last element
					i++; ///as of i++
					continue;
				}
				
				if (blocks[i].e == 0)
					continue;
				
				//find index 2
				has_index2 = false;
				index1 = i;				
				index2 = -1;
				for (int m = index1-1; m >= ((0+i/size)*size); m--) {
					if (blocks[m].e != 0) {
						index2 = m;
						has_index2 = true;
						break;
					}
				}
				
				/*
				 * combine the block
				 * [3] equals [2], combine to the larger index(right side) if true
				 * [2] equals [1] at the next loop
				 */
				if (has_index2) {
					if (blocks[index1].equals(blocks[index2])) {
						blocks[index1].e*=2;
						blocks[index2].e = 0;
						combined = true;
					}
				}
			}
		}
		else if (direction.contains("U")) {
			//start = 0,1,2,3 if size = 4 (4 columns)
			int start = 0;
			for (int i = start; i < blocks.length && start < size; i+=size) {
				/*
				 * skip the down most, i = 12,13,14,15, because start from 0 and compare vertically
				 * size = 4, i >= 12 (4*3)
				 * size = 5, i >= 20 (5*4)
				 */
				if (i >= size*(size-1)) {
					start++;
					i = start;
					i -= size; //as of i += size 
					continue;
				}
				
				if (blocks[i].e == 0)
					continue;
				
				//find index 2
				has_index2 = false;
				index1 = i;								
				index2 = -1;
				for (int m = index1+size; m <= (size*(size-1)+(i%size)); m+=size) {
					if (blocks[m].e != 0) {
						index2 = m;
						has_index2 = true;
						break;
					}
				}
				
				/*
				 * combine the block
				 * [0] equals [4], combine to the smaller index(upside) if true
				 * [4] equals [8] at the next loop
				 */
				if (has_index2) {
					if (blocks[index1].equals(blocks[index2])) {
						blocks[index1].e*=2;
						blocks[index2].e = 0;
						combined = true;
					}
				}
			}
		}
		else {
			/*
			 * size = 4, start = 12 (4*3)
			 * size = 5, start = 20 (5*4)
			 */
			int start = size*(size-1);
			for (int i = start; i < blocks.length && start < size*size; i-=size) {
				//skip the up most, i =0,1,2,3, because start from downside and compare vertically
				if (i < size) {
					start++;
					i = start;
					i += size; //as of i -= size
					continue;
				}
				
				if (blocks[i].e == 0)
					continue;
				
				//find index 2
				has_index2 = false;
				index1 = i;				
				index2 = -1;
				for (int m = index1-size; m >= (i%size); m-=size) {
					if (blocks[m].e != 0) {
						index2 = m;
						has_index2 = true;
						break;
					}					
				}
				
				/*
				 * combine the block
				 * [12] equals [8], combine to the larger index(downside) if true
				 * [8] equals [4] at the next loop
				 */
				if (has_index2) {
					if (blocks[index1].equals(blocks[index2])) {
						blocks[index1].e*=2;
						blocks[index2].e = 0;
						combined = true;
					}
				}
			}
		}
		
		return combined;
	}
	
}
