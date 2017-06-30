import java.util.ArrayList;
import java.util.Random;

public class Planet {
	private Graphic graphic;
	private boolean collision;
	private boolean isDestination;

	public Planet(float x, float y){
		graphic = new Graphic("PLANET");
		graphic.setPosition(x, y);
	    collision = false;
	    isDestination = false;
	}
	
	/**
	 * Overloaded method Planet() is called from the Level class: necessary bc it allows for random positions to be set and 
	 * checks if the various planets are colliding with eachother. Does this using the arraylist of planets as a parameter. 
	 * @param rng
	 * @param planets
	 */
	public Planet(Random rng, ArrayList<Planet> planets){
		graphic = new Graphic("PLANET");

		do{ 
			collision = false; //The existing planet uses its random position when the do-while loop breaks.
			graphic.setPosition(rng.nextFloat() * GameEngine.getWidth(), rng.nextFloat() * GameEngine.getHeight());
			for(int i = 0; i < planets.size(); i++){
				if(graphic.isCollidingWith(planets.get(i).graphic))
					collision = true; //If the planets did collide, then this loop keeps running to get new random positions to place them at.
			}
		}while(collision);
		
	}
	
	public void update(int time){
		graphic.draw(); //draws the planets to the screen
		return;
	}
	
	/**
	 * Sets the destination according on if the taxi has either successfully reached a prior destination and a new one is set,
	 * or if the game just started for the initial destination. Planet graphic looses destination when it is already reached,
	 * or if it is not a current destination (individual, not multiple).
	 * @param isDestination
	 */
	public void setDestination(boolean isDestination){
		if(isDestination == true){
			this.isDestination = true;//Set the destination of where the taxi should travel 
			graphic.setAppearance("DESTINATION");
		}
		else if (isDestination == false){ //if the taxi already reached this destination, then its no longer activated as a destination.
			this.isDestination = false;
			graphic.setAppearance("PLANET");
		}
		return;
	}
	
	/**
	 * Returns true if the taxi lands safely using thrusters at the destination. If it uses warpspeed, it crashes, and they are
	 * immobilized, returning false. 
	 */
	public boolean handleLanding(Taxi taxi){
		if(taxi.checkCollision(graphic) && taxi.isTravellingAtWarp()) 
			taxi.crash();
		else if(taxi.checkCollision(graphic) && isDestination)
			return true;
		
		return false;
	}
}


