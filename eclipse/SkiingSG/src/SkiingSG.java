/**
 * @author Michael Gorriz
 *
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class SkiingSG {

	final static int DIMENSION = 1000;

	enum Direction {East, North, West, South};

	//    private static int hereX=0;
	//    private static int hereY=0;

	private int maxSteepness = 0;
	private int maxLength = 0;

	Tile firstTileOfSlope;
	Tile endOfSlope = new Tile();

	Spot resultStart = new Spot(0,0);
	Spot resultEnd = new Spot (0,0);
	//   Spot here = new Spot(0,0);

	class Spot {
		int x,y;   	
		Spot (int x, int y){
			this.x=x;
			this.y=y;
		}

		Spot (Direction d, Spot s){
			switch (d){
			case East:
				this.x=s.x;
				this.y=s.y+1;
				break;
			case West:
				this.x = s.x;
				this.y = s.y-1;
				break;
			case North:
				this.x =  s.x-1;
				this.y = s.y;
				break;
			case South:
				this.x=s.x+1;
				this.y=s.y;
				break;
			}

		}

		private void printstr() {
			System.out.print("("+this.x+","+this.y+")");
		}

		public void setSpot(Spot s) {
			this.x=s.x;
			this.y=s.y;
		}
	}

	private class Tile {

		Spot s;
		int distanceFromRoot;
		int level;
		int diffLevel;
		Tile fromTile;
		Tile east, north, west, south;

		private void printstr(){
			System.out.println("Tile at ("+this.s.x+","+this.s.y+") @"+level+
					"Diff from Top:" + diffLevel +
					", length of Slope:"+(this.distanceFromRoot+1));
		}

		Tile(Spot s, int dfr, Tile t, Spot startOfSlope) {
			this.s=new Spot(s.x,s.y);
			this.distanceFromRoot = dfr;
			level = landscape[s.x][s.y];
			diffLevel = landscape[startOfSlope.x][startOfSlope.y]-level;
			east = null;
			north = null;
			west = null;
			south = null;
			fromTile = t;
		}
		Tile() {
			s = new Spot(0,0);
			distanceFromRoot = 0;
			level = 0;
			east = null;
			north = null;
			west = null;
			south = null;
			fromTile = null;
		}
	}

	private int[][] landscape=new int[DIMENSION][DIMENSION];

	public int[][] getLandscape() {
		return landscape;
	}

	public void setLandscape(int[][] landscape) {
		this.landscape = landscape;
	}

	private boolean westSmaller (Spot s) {
		return westSmaller (s.x,s.y);
	}

	private boolean westSmaller(int i, int j) {
		if (j==0) {
			return false;
		} else if (landscape[i][j]>landscape[i][j-1]){
			return true;
		}
		return false;
	}

	private boolean eastSmaller (Spot s) {
		return eastSmaller (s.x,s.y);
	}

	private boolean eastSmaller(int i, int j) {
		if (j==DIMENSION-1) {
			return false;
		} else if (landscape[i][j]>landscape[i][j+1]){
			return true;
		}
		return false;
	}

	private boolean northSmaller (Spot s) {
		return northSmaller (s.x,s.y);
	}

	private boolean northSmaller(int i, int j) {
		if (i==0) {
			return false;
		} else if (landscape[i][j]>landscape[i-1][j]){
			return true;
		}
		return false;
	}

	private boolean southSmaller (Spot s) {
		return southSmaller (s.x,s.y);
	}

	private  boolean southSmaller(int i, int j) {
		if (i==DIMENSION-1) {
			return false;
		} else if (landscape[i][j]>landscape[i+1][j]){
			return true;
		}
		return false;
	}

	private  boolean readFileToLandscape(String filename) {
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
			// read the first line which should contain twice "<DIMENSION>"
			while ( st.hasMoreTokens() ) {
				word = st.nextToken();
				if (Integer.parseInt(word)!=DIMENSION) {
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
						this.landscape[i][j]=Integer.parseInt(word);
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

	private void findBestSlope(int lowestLevel) {
		for (int i = 0; i < DIMENSION; i++){
			for (int j = 0; j < DIMENSION; j++){
				firstTileOfSlope = buildTreeFromHere(new Spot(i,j), 0, null, new Spot(i,j));
				findLongestAndSteepestSlope (firstTileOfSlope);
			}
		}
	}

	private void findLongestAndSteepestSlope(Tile t) {
		/* Browse through the tree and find the maximum length and
		 * maximum steepness. The maxLength starts at 0 and not 1 as in colloquial language
		 */
		/* we found a leaf, let us check whether this is the maximum
		 */
		if ((t.east==null) && (t.north==null) && (t.west==null) && (t.south==null)) {
			if ((t.distanceFromRoot > maxLength) ||
					((t.distanceFromRoot == maxLength) && (t.diffLevel > maxSteepness))) {
				maxLength = t.distanceFromRoot;
				maxSteepness = t.diffLevel;
				resultEnd = t.s;
				resultStart = firstTileOfSlope.s;
			}
		} else {
			if (t.east!=null) {
				findLongestAndSteepestSlope(t.east);
			}
			if (t.north!=null) {
				findLongestAndSteepestSlope(t.north);
			}
			if (t.west!=null) {
				findLongestAndSteepestSlope(t.west);
			}
			if (t.south!=null) {
				findLongestAndSteepestSlope(t.south);
			}
		}
	}

	private Tile buildTreeFromHere(Spot s, int levelFromRoot, Tile upT, Spot startOfSlope) {
		/*
		 * This will build a spanning tree from the spot s.
		 * It will simply add a node to every side where we can go downhill
		 * startOfSlope is the beginning of the Slope. The hierarchy Length and Level difference is calculated from there
		 * with the Tile constructor
		 * In order to be able to find the way up easily I put a reference in the tile 
		 * to lead the way back to the top.
		 */
		Tile t = new Tile (s, levelFromRoot, upT, startOfSlope);
		//	System.out.print("make tile at ");s.printstr();System.out.println();
		//	t.printstr();
		if (eastSmaller(t.s)){
			t.east=buildTreeFromHere(eastOf(t.s), t.distanceFromRoot+1, t, startOfSlope);
		}
		if (northSmaller(t.s)){
			t.north=buildTreeFromHere(northOf(t.s), t.distanceFromRoot+1, t, startOfSlope);
		}
		if (westSmaller(t.s)){
			t.west=buildTreeFromHere(westOf(t.s), t.distanceFromRoot+1, t, startOfSlope);
		}
		if (southSmaller(t.s)){
			t.south=buildTreeFromHere(southOf(t.s), t.distanceFromRoot+1, t, startOfSlope);
		}
		//	System.out.print("return value");
		//	t.printstr();
		return t;
	}

	private Spot northOf(Spot from) {
		Spot here = new Spot( Direction.North, from);
		return here;
	}

	private Spot southOf(Spot from) {
		Spot here = new Spot( Direction.South, from);
		return here;
	}

	private Spot eastOf(Spot from) {
		Spot here = new Spot ( Direction.East, from);
		return here;
	}

	private Spot westOf(Spot from) {
		Spot here = new Spot ( Direction.West, from);
		return here;
	}


	private void printBestSlopeToConsole() {
		System.out.println("Longest Path is "+ (maxLength+1) + " long and " + maxSteepness + " steep.");
		System.out.print("It starts at (" + resultStart.x + "," + resultStart.y + ") and ends at (");
		System.out.print(resultEnd.x + "," + resultEnd.y + ").");
	}

	/**
	 * @param arguments[0] is an absolute filename where I should 
	 * find a 1000x1000 matrix of integer values. The first two numbers should be 1000
	 */
	public static void main(String[] args) {
		SkiingSG myArena = new SkiingSG();
		if (args.length != 1) {
			System.out.println("Set filename as argument.");
		} else 
			if (!myArena.readFileToLandscape (args[0])) {
				System.out.println("File not found, wrong dimension or too short!");
			} else {
				myArena.findBestSlope(0);
				myArena.printBestSlopeToConsole();
			}
	}
}