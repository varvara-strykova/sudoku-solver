import java.io.File;
import java.util.Scanner;

public class Board{
	
	/*The Sudoku Board is made of 9x9 cells for a total of 81 cells.
	 * In this program I will be representing the Board using a 2D Array of cells. 
	 */

private Cell[][] board = new Cell[9][9];
	//The variable "level" records the level of the puzzle being solved.
	private String level = "";

	//This initializes every cell on the board with a generic cell and assigns the boxIDs to the cell. 
	public Board()
	{
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				board[x][y] = new Cell();
				board[x][y].setBoxID( 3*(x/3) + (y)/3+1);
			}
	}
	
	/*This method takes a single String as a parameter.  The String must be either "easy", "medium" or "hard"
	 * If it is none of these, the method will set the String to "easy".  The method will set each of the 9x9 grid
	 * of cells by accessing either "easyPuzzle.txt", "mediumPuzzle.txt" or "hardPuzzle.txt" and setting the Cell.number to
	 * the number given in the file.
	 */
	public void loadPuzzle(String level) throws Exception
	{
		this.level = level;
		String fileName = "easyPuzzle.txt";
		if(level.contentEquals("medium"))
			fileName = "mediumPuzzle.txt";
		else if(level.contentEquals("hard"))
			fileName = "hardPuzzle.txt";
   		else if(level.contentEquals("oni"))
			fileName = "oni.txt";
		
		Scanner input = new Scanner (new File(fileName));
		
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				int number = input.nextInt();
				if(number != 0)
					solve(x, y, number);
			}
						
		input.close();
		   
	}
	
	/*This method scans the board and returns TRUE if every cell has been solved.  Otherwise it returns FALSE
	 */
	public boolean isSolved()
	{
		for(int x = 0; x<9; x++) {
			for(int y = 0; y<9; y++) {
				if(board[x][y].getNumber()==0)
					return false;
			
			}
		}
		return true;
	}
	
	/*This method displays the board neatly to the screen.
	 */
	public void display()
	{
		//getBoxId
		for(int x = 0; x<9; x++) {
			for(int y = 0; y<9; y++) {
				System.out.print(board[x][y].getNumber()+ " ");
				}
				System.out.println();
			}
		}
	
	/*This method solves a single cell at x,y for number.  It also adjusts the potentials of the remaining cells in the same row,
	 * column, and box.
	 */
	public void solve(int x, int y, int number)
	{
		board[x][y].setNumber(number);
		
		for(int j = 0; j < 9; j++)
			if(j != y)
				board[x][j].cantBe(number);
		
		for(int i = 0; i <9; i++)
			if(i!=x)
				board[i][y].cantBe(number);
		
		
		//box
		for(int k =0; k<9; k++) {
			for(int n = 0; n <9; n++) {
				if(board[k][n].getBoxID()==board[x][y].getBoxID())
					board[k][n].cantBe(number);
			}
		}
		
		
	}
	
	
	//logicCycles() continuously cycles through the different logic algorithms until no more changes are being made.
	public void logicCycles()throws Exception
	{
		Board[] saved = new Board[81];
	    int guessInd = 0;
	    while (!isSolved()) {
	        int changesMade = 0;
	       
	        do {
	            changesMade = 0;
	            changesMade += logic1();
	            changesMade += logic2();
	            changesMade += logic3();
	            changesMade += logic4();
	        } while (changesMade > 0 && !errorFound());
	       
	        if (errorFound()) {
	            if (guessInd == 0) {
	                System.out.println("unable to solve the puzzle");
	                return;
	            }
	           
	            Board lastSaved = saved[--guessInd];
	            for (int x = 0; x < 9; x++) {
	                for (int y = 0; y < 9; y++) {
	                    board[x][y] = new Cell();
	                    board[x][y].setBoxID(lastSaved.board[x][y].getBoxID());
	                    int num = lastSaved.board[x][y].getNumber();
	                    if (num != 0) {
	                        solve(x, y, num);
	                    } else {
	                        for (int i = 1; i <= 9; i++) {
	                            if (!lastSaved.board[x][y].canBe(i)) {
	                                board[x][y].cantBe(i);
	                            }
	                        }
	                    }
	                }
	            }
	           
	            for (int x = 0; x < 9; x++) {
	                for (int y = 0; y < 9; y++) {
	                    if (board[x][y].getNumber() == 0 && board[x][y].numberOfPotentials() > 0) {
	                        board[x][y].cantBe(board[x][y].getFirstPotential());
	                        break;
	                    }
	                   
	                }
	                break;
	            }
	            continue;
	        }
	      
	        if (!isSolved()) {
	           
	            Board copy = new Board();
	            for (int x = 0; x < 9; x++) {
	                for (int y = 0; y < 9; y++) {
	                    copy.board[x][y].setBoxID(board[x][y].getBoxID());
	                    int num = board[x][y].getNumber();
	                    if (num != 0) {
	                        copy.solve(x, y, num);
	                    } else {
	                        for (int i = 1; i <= 9; i++) {
	                            if (!board[x][y].canBe(i)) {
	                                copy.board[x][y].cantBe(i);
	                            }
	                        }
	                    }
	                }
	            }
	            saved[guessInd++] = copy;
	           
	           
	            for (int x = 0; x < 9; x++) {
	                for (int y = 0; y < 9; y++) {
	                    if (board[x][y].getNumber() == 0 && board[x][y].numberOfPotentials() > 0) {
	                        solve(x, y, board[x][y].getFirstPotential());
	                        break;
	                    }
	                }
	                 break;
	            }
	        }
	      
	    }
		
	}
	
	/*This method searches each row of the puzzle and looks for cells that only have one potential.  If it finds a cell like this, it solves the cell
	 * for that number. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic1()
	{
		int changesMade = 0;

		for(int x =0; x < 9; x++) {
			for(int y = 0; y<9; y++) {
				if(board[x][y].numberOfPotentials()==1 && board[x][y].getNumber()==0) {
					solve(x, y, board[x][y].getFirstPotential());
					
					changesMade++;
					
				}
					
					
			}
		}
		
		return changesMade;
					
	}
	
	/*This method searches each row for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell.  It then does the same thing for the columns. This also tracks the number of cells that
	 * it solved as it traversed the board and returns that number.
	 */
	public int logic2()
	{
		int changesMade = 0;
		
		for(int x = 0; x < 9; x++) {
			
			for(int i = 0; i <=9; i++) {
				int count = 0;
				
				for(int j = 0; j < 9; j++) {
					if(board[x][j].canBe(i) && board[x][j].getNumber()==0) {
						count++;
					}
						
				}
				
				if(count==1) {
					for(int j = 0; j<9; j++) {
						if(board[x][j].canBe(i) && board[x][j].getNumber()==0) {
							solve(x, j, i);
							changesMade++;
						}
						
							
					}
				}
			}
		}
		
			for(int x = 0; x < 9; x++) {
				
				for(int i = 0; i <= 9; i++) {
					int count = 0;
					
					for(int j = 0; j < 9; j++) {
						if(board[j][x].canBe(i) && board[j][x].getNumber()==0) {
							count++;
						}
							
					}
					
					if(count==1) {
						for(int j = 0; j<9; j++) {
							if(board[j][x].canBe(i) && board[j][x].getNumber()==0) {
								solve(j, x, i);
								changesMade++;
							}
								
						}
					}
				}
			}	
	
		return changesMade;
	}
	
	/*This method searches each box for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic3()
	{
		int changesMade = 0;
		
		for(int x =0; x < 9; x++) {
			for(int y = 0; y<9; y++) {
				if(board[x][y].numberOfPotentials()==1 && board[x][y].getNumber()==0) {
					solve(x, y, board[x][y].getFirstPotential());
					if(y+1<9 &&   board[x][y].getBoxID()==board[x][y+1].getBoxID())
						board[x][y].cantBe(board[x][y].getFirstPotential());
					changesMade++;
				}
			}
		}
		return changesMade;
	}
	
	
		/*This method searches each row for the following conditions:
		 * 1. There are two unsolved cells that only have two potential numbers that they can be
		 * 2. These two cells have the same two potentials (They can't be anything else)
		 * This also tracks the number of cells that it solved as it traversed the board and returns that number.
		 */
	
	public int logic4()
	{
		int changesMade = 0;
		for(int x = 0; x < 9; x ++) {
			for(int y = 0; y<8; y++) {
				Cell cell1 = board[x][y];
						if(cell1.getNumber()!=0 || cell1.numberOfPotentials()!=2)
							continue;
						for(int i = y+1; i<9; i++) {
							Cell cell2 = board[x][i];
							if(cell2.getNumber()!=0 || cell2.numberOfPotentials()!=2)
								continue;
							if(cell1.getFirstPotential()==cell2.getFirstPotential() && cell1.getSecondPotential()==cell2.getSecondPotential()) {
								int pot1 = cell1.getFirstPotential();
								int pot2 = cell1.getSecondPotential();
								for(int n = 0; n<9; n++) {
									if(n==y || n==i)
										continue;
									Cell cell3 = board[x][n];
									if(cell3.canBe(pot1)) {
										cell3.cantBe(pot1);
										changesMade++;
									}
									if(cell3.canBe(pot2)) {
										cell3.cantBe(pot2);
										changesMade++;
									}
								}
							}
								
						}
					}
				}
		for(int y = 0; y < 9; y ++) {
			for(int x = 0; x<8; x++) {
				Cell cell1 = board[x][y];
						if(cell1.getNumber()!=0 || cell1.numberOfPotentials()!=2)
							continue;
						for(int i = x+1; i<9; i++) {
							Cell cell2 = board[i][y];
							if(cell2.getNumber()!=0 || cell2.numberOfPotentials()!=2)
								continue;
							if(cell1.getFirstPotential()==cell2.getFirstPotential() && cell1.getSecondPotential()==cell2.getSecondPotential()) {
								int pot1 = cell1.getFirstPotential();
								int pot2 = cell1.getSecondPotential();
								for(int n = 0; n<9; n++) {
									if(n==x || n==i)
										continue;
									Cell cell3 = board[n][y];
									if(cell3.canBe(pot1)) {
										cell3.cantBe(pot1);
										changesMade++;
									}
									if(cell3.canBe(pot2)) {
										cell3.cantBe(pot2);
										changesMade++;
									}
								}
							}
								
						}
					}
				}
		
		return changesMade;
	}
	
	
	/*This method scans the board to see if any logical errors have been made.  It can detect this by looking for a cell that no longer has the potential to be
	 * any number.
	 */
	public boolean errorFound()
	{
		for(int x = 0; x<9; x++) {
			for(int y = 0; y<9; y++) {
				if(board[x][y].getNumber()==0 && board[x][y].numberOfPotentials()==0)
					return true;
			}
		}
				
		return false;
	}
	
	
	
	

}

