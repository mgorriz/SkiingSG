
/**
 * 
 */

/**
 * @author Michael Gorriz
 *
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class SkiingSG {
    
    final static int DIMENSION = 1000;

	private static final int EAST = 0;
	private static final int NORTH = 1;
	private static final int WEST = 2;
	private static final int SOUTH = 3;
	
    
    private static int hereX=0;
    private static int hereY=0;
    
    private ArrayList<Path> allPaths;
    
    SkiingSG (){
    	ArrayList<Path> allPaths = new ArrayList<Path>();
    }
    
    class Spot {
    	int x,y;   	
    	Spot(int x, int y){
    		this.x=x;
    		this.y=y;
    	}
    }
    
    private static int[][] landscape=new int[DIMENSION][DIMENSION];

    public int[][] getLandscape() {
    	return landscape;
    }

    public void setLandscape(int[][] landscape) {
    	this.landscape = landscape;
    }
    
    private boolean findStartAboveLevel (int minLevel) {
    	for (int i=0;i<DIMENSION;i++){
    		for (int j=0;j<DIMENSION;j++) {
    			if ((isLocalMax(i,j)) && (landscape[i][j]>minLevel)) {
    				hereX=i;
    				hereY=j;
    				return true;
    			}
    		}
    	}
    	return false;
    }

    
    class Path {
    	int totalSlope;
    	ArrayList<Spot> slope;
   	
    	// Constructor to start a new path at Spot s
    	Path (Spot s) {
    		totalSlope=0;
        	slope = new ArrayList<Spot>(); 		
    		slope.add(s);
    	}
    	
    	// Constructor to create a new Path, copy the existing p and
    	// append a Spot s
    	Path (Path p, Spot s){
        	slope = new ArrayList<Spot>(); 	
    		for (Spot item : p.slope) {
    			this.slope.add(item);
    		}
     		this.slope.add(s);
     		totalSlope=landscape[slope.get(0).x][slope.get(0).y]-
     				landscape[(slope.get(slope.size()-1)).x][(slope.get(slope.size()-1)).y];
    	}
    } //end class Path
    
    private static boolean isLocalMax(int i, int j) {
    	if (eastSmaller(i,j) && westSmaller(i,j) && southSmaller(i,j) && northSmaller(i,j)) {
    		return true;
    	} 		
		return false;
	}

	private static boolean northSmaller(int i, int j) {
		if (j==0) {
			return true;
		} else if (landscape[i][j]>landscape[i][j-1]){
			return true;
		}
		return false;
	}

	private static boolean southSmaller(int i, int j) {
		if (j==DIMENSION) {
			return true;
		} else if (landscape[i][j]>landscape[i][j+1]){
			return true;
		}
		return false;
	}

	private static boolean westSmaller(int i, int j) {
		if (i==0) {
			return true;
		} else if (landscape[i][j]>landscape[i-1][j]){
			return true;
		}
		return false;
	}

	private static boolean eastSmaller(int i, int j) {
		if (i==DIMENSION) {
			return true;
		} else if (landscape[i][j]>landscape[i+1][j]){
			return true;
		}
		return false;
	}

	private static boolean readFileToLandscape(String filename) {
		FileReader afile;
		try {
			afile = new FileReader(filename);
		} catch (FileNotFoundException e) {
			return false;
		};
		String line;
		String word;
		BufferedReader br = new BufferedReader (afile);
		try {
			line = br.readLine();
			StringTokenizer st = new StringTokenizer(line);
			// read the first line which should contain twice "1000"
			while ( st.hasMoreTokens() ) {
				word = st.nextToken();
				if (Integer.parseInt(word)!=1000) {
					br.close();
					afile.close();
					return false;
				};
			}
			for (int i=0;i<DIMENSION;i++){
				line = br.readLine();
				st = new StringTokenizer(line);
				for (int j=0;j<DIMENSION;j++) {
					if (st.hasMoreTokens()) {
						word = st.nextToken();
						// extract integers from a text file, then do the caculation.
						landscape[i][j]=Integer.parseInt(word);
					}
				}			  
			}
			br.close();
			afile.close();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	private int possibilities (Spot s) {
		int k=0;
		if (northSmaller (s.x, s.y)) {
			k++;
		}
		if (southSmaller (s.x, s.y)) {
			k++;
		}
		if (westSmaller (s.x, s.y)) {
			k++;
		}
		if (eastSmaller (s.x, s.y)) {
			k++;
		}
		return k;
	}
	
	private int directionSwitch (Spot s) {
		/* this creates an integer between 0 and 15 which 
		 * represents all possibilities what could be done at one point
		 * 0 means "the path ends here"
		 * 15 means "you can go in all directions"
		 * 1,2,4,8 means "you can go only east, north, west or south respectively"
		 * 3 means "you can go east and north"
		 * 5 means "you can go east and west"
		 * 9 means "you can go east and south"
		 * 6 means "you can go north and west"
		 * 10 means "you can go north and south"
		 * 12 means "you can go west and south"
		 * 7 means "you can go east, north and west"
		 * 11 means "you can go east, north and south"
		 * 13 means "you can go east, west and south"
		 * 14 means "you can go north, west and south"
		 */
		int result = 0;
		if (eastSmaller(s.x,s.y)) {
			result +=1;
		}
		if (northSmaller(s.x,s.y)) {
			result +=2;
		}
		if (westSmaller(s.x,s.y)) {
			result +=4;
		}
		if (southSmaller(s.x,s.y)) {
			result +=8;
		}
		return result;
	}
	
	private void findBestSlope(int lowestLevel) {
		/*
		 * Picks a local maximum which is higher than the best slope so far
		 */
		findStartAboveLevel(lowestLevel);
		Spot here = new Spot(hereX, hereY);
	}

	private void goDirection(int direction, Path p, Spot s) {
		Spot nextSpot = new Spot(s.x,s.y);
		switch (direction) {
		case EAST:
			nextSpot.x+=1;
			break;
		case NORTH:
			nextSpot.y+=1;
			break;
		case WEST:
			nextSpot.x-=1;
			break;
		case SOUTH:
			nextSpot.y-=1;
			break;
		default:
			break; }
		if (p!=null) {
			Path splitPath = new Path(p,nextSpot);
			allPaths.add(splitPath);		
		} else {
			p.slope.add(nextSpot);
		}
	}
	
	
	private void findSlopeFromHere (Path p, Spot s) {
		switch (directionSwitch(s)) {
		case 1: 
			goDirection(EAST,null,s);
			break;
		case 2:
			goDirection(NORTH,null,s);
			break;
		case 4:
			goDirection(WEST,null,s);
			break;
		case 8:
			goDirection(SOUTH,null,s);
			break;
		case 3: {
			// go east with a new path
			goDirection(EAST,p,s);
			// go north with the same path
			goDirection(NORTH,null,s);
			break;}
		case 5:{
			// go east with a new path
			nextSpot.x+=1;			
			Path splitPath = new Path(p,nextSpot);
			allPaths.add(splitPath);
			// go west with the same path
			nextSpot.x=s.x-1;
			nextSpot.y=s.y;
			p.slope.add(nextSpot);
			break;}
		case 9:{
			// go east with a new path
			nextSpot.x+=1;			
			Path splitPath = new Path(p,nextSpot);
			allPaths.add(splitPath);
			// go south with the same path
			nextSpot.x=s.x;
			nextSpot.y=s.y-1;
			p.slope.add(nextSpot);
			break;}
		case 6:{
			// go north with a new path
			nextSpot.y+=1;			
			Path splitPath = new Path(p,nextSpot);
			allPaths.add(splitPath);
			// go west with the same path
			nextSpot.x=s.x-1;
			nextSpot.y=s.y;
			p.slope.add(nextSpot);
			break;}
		case 10:{
			// go north with a new path
			nextSpot.y+=1;			
			Path splitPath = new Path(p,nextSpot);
			allPaths.add(splitPath);
			// go south with the same path
			nextSpot.x=s.x;
			nextSpot.y=s.y-1;
			p.slope.add(nextSpot);
			break;}
		case 12:{
			// go west with a new path
			nextSpot.x-=1;			
			Path splitPath = new Path(p,nextSpot);
			allPaths.add(splitPath);
			// go south with the same path
			nextSpot.x=s.x;
			nextSpot.y=s.y-1;
			p.slope.add(nextSpot);
			break;}
		case 7:{
			// go east with a new path
			nextSpot.x+=1;			
			Path splitPath = new Path(p,nextSpot);
			allPaths.add(splitPath);
			// go north with a new path
			nextSpot.y+=1;			
			Path splitPath2 = new Path(p,nextSpot);
			allPaths.add(splitPath2);
			// go west with the same path
			nextSpot.x=s.x-1;
			nextSpot.y=s.y;
			p.slope.add(nextSpot);
			break;}
//more to come

			
		default:
			break;
		}
		
	}
	
 	private void initiateNewPath(Path p, int hereX2, int hereY2) {
		
	}

	private static void printBestSlopeToConsole() {
		// TODO Auto-generated method stub
	}

	private static void printStartingPointToConsole() {
		System.out.println("Starting Point:"+hereX+" "+hereY);
	}

	/**
     * @param arguments is an absolute filename where I should 
     * find a 1000x1000 matrix of int. The first two numbers should be 1000
     */
    public static void main(String[] args) {
    	SkiingSG myArena = new SkiingSG();
    	String filename = args[0];
    	if (!readFileToLandscape (filename)) {
    		System.out.println("File not found, wrong dimension or too short!");
    	};
    	myArena.findBestSlope(0);
    	printStartingPointToConsole();
    	printBestSlopeToConsole();
 	
    }


}

