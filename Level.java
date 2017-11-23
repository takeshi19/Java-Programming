import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.Random;

/**
 * This Level class is responsible for managing all of the objects in your game
 * The GameEngine creates a new Level object for each level, and then calls 
 * that Level object's update() method repeatedly until it returns either 
 * "ADVANCE" (to proceed to the next level) or "QUIT" (to end the entire game).
 * <br/><br/>
 * This class should contain and make use of the following private fields:
 * <tt><ul>
 * <li>private Random rng</li>
 * <li>private Taxi taxi</li>
 * <li>private ArrayList<WarpStar> warpStars</li>
 * <li>private ArrayList<GasCloud> gasClouds</li>
 * <li>private ArrayList<Planet> planets</li>
 * <li>private int destinationPlanetIndex</li>
 * </ul></tt>
 */
public class Level{	

	private Taxi taxi; //Object field of the Taxi class
	private Random rng = new Random(); //random number generator 
	private ArrayList<WarpStar> warpStars; //an arraylist of warpStar objects
	private ArrayList<GasCloud> gasClouds; //an arraylist of gasCloud objects
	private ArrayList<Planet> planets; //an arraylist of planets objects	
	private static Integer fares;	//an Integer object to convert to a string for getHUDMessage() display
	private static float randomXCoordinates; //x-coordinates to randomly set the planets, gasclouds, and warpstars to
	private static float randomYCoordinates; // y-coordinates to randomly set the planets, gasclouds, and warpstars to 
	private static float gasCloudOrientation; //the position where the gas cloud graphic is initialized to before it rotates
	private int destinationPlanetIndex; //the amount of planet destinations our taxi made it to cf
	/**
	 * This constructor initializes a new level object, so that the GameEngine
	 * can begin calling its update() method to advance the game's play.  In
	 * the process of this initialization, all of the objects in the current
	 * level should be instantiated and initialized to their starting states.
	 * @param rng is the ONLY Random number generator that should be used by 
	 * throughout this level and by any of the objects within it.
	 * @param levelFilename is either null (when a random level should be 
	 * loaded) or a reference to the custom level file that should be loaded.
	 */
	
	public Level(Random rng, String levelFilename){ 	
		taxi = new Taxi(GameEngine.getWidth()/2f, GameEngine.getHeight()/2f);
		this.rng = rng;
		fares = 0;
		destinationPlanetIndex = 0;
		warpStars = new ArrayList<WarpStar>();
		gasClouds = new ArrayList<GasCloud>();
		planets = new ArrayList<Planet>();
		
		if (levelFilename == null) {
			loadRandomLevel(); //We load a randomLevel if either our loadCustomLevel() fails or our levelFilename is null.
			System.out.println("levelFilename is null");
		}
	
		else { //Else the levelFilename is not null, then call the loadCustomLevel() method
			
			if (!loadCustomLevel(levelFilename)) {
				loadRandomLevel();
				System.out.println("loadRandomLevel() is in use");
			}
			else { //Tests which level method is being used.
				System.out.println("loadCustomLevel() is in use");
			}
		}
			 
	}
	 
	/**
	 * The GameEngine calls this method repeatedly to update all of the objects
	 * within your game, and to enforce all of your game's rules.
	 * @param time is the time in milliseconds that have elapsed since the last
	 * time this method was called (or your constructor was called). This can 
	 * be used to help control the speed of moving objects within your game.
	 * @return "CONTINUE", "ADVANCE", or "QUIT".  When this method returns
	 * "CONTINUE" the GameEngine will continue to play your game by repeatedly
	 * calling it's update() method.  Returning "ADVANCE" instructs the 
	 * GameEngine to end the current level, create a new level, and to start
	 * updating that new level object instead of the current one. Returning 
	 * "QUIT" instructs the GameEngine to end the entire game.  In the case of
	 * either "QUIT" or "ADVANCE" being returned, the GameEngine presents a
	 * short pause and transition message to help the player notice the change.
	 */
	
	public String update(int time) {	

		for (int i = 0; i < warpStars.size(); i++) {
			warpStars.get(i).update(); 
			warpStars.get(i).handleNavigation(taxi); //For each of the 6 warpStars, check if the taxi has speed up by them.
		}

		for (int i = 0; i < gasClouds.size(); i++) {
			if (gasClouds.get(i).shouldRemove()) {
				gasClouds.remove(i);//Remove the gasclouds after they refuel your taxi after they touch.
				i--; //Decrement i after the gasCloud object has been removed so that there are less gasCloud objects to iterate through.
			}
		}
		
		for (int i = 0; i < gasClouds.size(); i++) {
			gasClouds.get(i).update(time); 
			gasClouds.get(i).handleFueling(taxi); //refuels the taxi when it touches the gascloud.
		}
	
		for (int i = 0; i < planets.size(); i++) {
			if (i == destinationPlanetIndex) {//All 6 planets can be set as destinations, one after the other.
				planets.get(i).setDestination(true);
			}
			//The blue destination appearance vanishes when we land on the planet with thrusters, 
			//and the next planet gets set as the destination.		
			if (planets.get(i).handleLanding(taxi)) { 
				planets.get(i).setDestination(false); //turn off the visual destination graphic when taxi properly reaches the planet
				destinationPlanetIndex++;//whenever a taxi reaches its destination, its index increments 
				fares++;//whenever a taxi reaches its destination, its fare increases by 1
			}
			
			if (destinationPlanetIndex == planets.size()) {
				return "ADVANCE"; //Given that the player safely lands on all 6 planets, they unlock the next level.
			}
			planets.get(i).update(time);
		}
		
		taxi.update(time); 
		
		if (taxi.update(time)) {
			return "QUIT"; //If the taxi has ran out of fuel or collided w/ a planet at warpspeed, they lose the game.
		}
		return "CONTINUE"; 
	}	
	
	/**
	 * This method returns a string of text that will be displayed in the upper
	 * left hand corner of the game window.  Ultimately this text should convey
	 * the taxi's fuel level, and their progress through the destinations.
	 * However, this may also be useful for temporarily displaying messages
	 * that help you to debug your game.
	 * @return a string of text to be displayed in the upper-left hand corner
	 * of the screen by the GameEngine.
	 */
	public String getHUDMessage() { 

		String currentFuel = ""; //stores the converted float objects fuel levels to a printable string
		Float fuel = taxi.getFuel(); //receives the current fuel levels of the taxi
		String currentFares = ""; //the amount of destinations reached by taxi as a string instead of an int
		String outOfFuelMssg = ""; //message displayed when the taxi is out of fuel-losing the game
		String crashMssg = ""; //message displayed when taxi has crashed-losing the game
		
		currentFuel = "Fuel: " + fuel.toString();
		currentFares = "\nFares: " + fares.toString() + "/6";
		outOfFuelMssg = "You've run out of fuel!\nPress the SPACEBAR to end this game.";
		crashMssg = "You've crashed into a planet!\nPress the SPACEBAR to end this game.";
		
		if (taxi.getFuel() <= 0) {
			return outOfFuelMssg; //lost game and message is printed if they are out of fuel
		}
		if (taxi.hasCrashed()) {
			return crashMssg; //if hasCrashed() has returned from the Taxi class then this means they crashed at warpspeed and they lost.
		}
		return currentFuel + currentFares; //the \n character allows for the 2 fuel and fares amounts to be displayed as string ontop of eachother.
	}
	
	/**
	 * This method initializes the current level to contain a single taxi in 
	 * the center of the screen, along with 6 randomly positioned objects of 
	 * each of the following types: warp stars, gasClouds, and planets.
	 */
	private void loadRandomLevel() 
	{ 
		/* Chosen as the "standard" number of objects. Can be any
		 * user-wanted value. */
		int numWarpStars = 6;
		/* Chosen as the "standard" number of objects. Can be any
		 * user-wanted value. */
		int numGasClouds = 6;
		/* Chosen as the "standard" number of objects. Can be any
		 * user-wanted value. */
		int numPlanets = 6;
		warpStars =  new ArrayList<WarpStar>(numWarpStars);
		gasClouds = new ArrayList<GasCloud>(numGasClouds);
		planets = new ArrayList<Planet>(numPlanets);
		taxi = new Taxi(GameEngine.getWidth()/2f, GameEngine.getHeight()/2f);
		
		for (int i = 0; i < warpStars.size(); i++) {
			 randomXCoordinates = rng.nextFloat() * GameEngine.getWidth();
			 randomYCoordinates = rng.nextFloat() * GameEngine.getHeight();
			 warpStars.add(new WarpStar(randomXCoordinates, randomYCoordinates));
		}
		
		for (int i = 0; i < gasClouds.size(); i++) {
			 randomXCoordinates = rng.nextFloat() * GameEngine.getWidth();
			 randomYCoordinates = rng.nextFloat() * GameEngine.getHeight();
			 gasCloudOrientation = (float)Math.PI * rng.nextFloat();
			 gasClouds.add(new GasCloud(randomXCoordinates, randomYCoordinates, gasCloudOrientation));
		}
		
		for (int i = 0; i < planets.size(); i++) {
			planets.add(new Planet(rng, planets)); //call the new overloaded constructor
			planets.get(0).setDestination(true);
		}
	}

	/**
	 * This method initializes the current level to contain each of the objects
	 * described in the lines of text from the specified file.  Each line in
	 * this file contains the type of an object followed by the position that
	 * it should be initialized to start the level.
	 * @param levelFilename is the name of the file (relative to the current
	 * working directory) that these object types and positions are loaded from
	 * @return true after the specified file's contents are successfully loaded
	 * and false whenever any problems are encountered related to this loading
	 */
	private boolean loadCustomLevel(String levelFilename) 
	{ 
		boolean testFileRead = false;
		int numWarpStars = 0;
		int numGasClouds = 0; 
		int numPlanets = 0;
		warpStars =  new ArrayList<WarpStar>(numWarpStars);
		gasClouds = new ArrayList<GasCloud>(numGasClouds);
		planets = new ArrayList<Planet>(numPlanets);
		
		File inputFile = new File(levelFilename); 

		try {
			
			Scanner fileRead = new Scanner(inputFile);

			while (fileRead.hasNext()) { 
				String fileAsString = fileRead.next();
				
				if (fileAsString.equals("TAXI")) { //Set the taxi object to the position specified in the level file.
					fileRead.next(); //Reads/skips the space between the "TAXI" and the first float.
					String firstFloatToken = fileRead.next();
					/* Since the last character in this part of the string is always a
					 * comma, it needs to be cut off --> "76.0, 123.0" --> "76.0," (length()-1) --> "76.0" */
					firstFloatToken = firstFloatToken.substring(0, firstFloatToken.length()-1);
					String secondFloatToken = fileRead.next();
					float x = Float.parseFloat(firstFloatToken); //converts the string values of the floats to Float objects
					float y = Float.parseFloat(secondFloatToken);
					
					taxi = new Taxi(x, y); //then sets these float objects as coordinates for the taxi to be set to.
				} 
				
				else if(fileAsString.equals("GAS")){
					fileRead.next();
					String firstFloatToken = fileRead.next();
					firstFloatToken = firstFloatToken.substring(0, firstFloatToken.length()-1);
					String secondFloatToken = fileRead.next();
					float x = Float.parseFloat(firstFloatToken); //converts the string values of the floats to Float objects
					float y = Float.parseFloat(secondFloatToken);
					
					gasCloudOrientation = (float)Math.PI * rng.nextFloat();
					gasClouds.add(new GasCloud(x, y, gasCloudOrientation));					
					numGasClouds++; //then we increment the values to add into the arraylists per object type
				}
				
				else if(fileAsString.equals("PLANET")){ //if the line has PLANET, set the coordinates according to the planet objects					
					fileRead.next();
					String firstFloatToken = fileRead.next();
					firstFloatToken = firstFloatToken.substring(0, firstFloatToken.length()-1);
					String secondFloatToken = fileRead.next();
					float x = Float.parseFloat(firstFloatToken); //converts the string values of the floats to Float objects
					float y = Float.parseFloat(secondFloatToken);
					
					planets.add(new Planet(x, y));
					planets.get(0).setDestination(true);
					numPlanets++;
				}
				
				else if(fileAsString.equals("WARP_STAR")){
					fileRead.next();
					String firstFloatToken = fileRead.next();
					firstFloatToken = firstFloatToken.substring(0, firstFloatToken.length()-1);
					String secondFloatToken = fileRead.next();
					float x = Float.parseFloat(firstFloatToken); //converts the string values of the floats to Float objects
					float y = Float.parseFloat(secondFloatToken);
					
					warpStars.add(new WarpStar(x, y));
					numWarpStars++;
				}
			}
			
			fileRead.close();
			testFileRead = true;		
		}catch(Exception e){
			testFileRead = false;
		}
			
		return testFileRead;
	}
	 

	/**
	 * This method creates and runs a new GameEngine with its first Level. Any
	 * command line arguments passed into this program are treated as a list of
	 * custom level filenames that should be played in order by the player.
	 * @param args is the sequence of custome level filenames to play through
	 */
	public static void main(String[] args)
	{
		GameEngine.start(null,args);
	} 
}
