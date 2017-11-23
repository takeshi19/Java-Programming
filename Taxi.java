
public class Taxi {
	private Graphic graphic;
	private float thrusterSpeed;
	private float fuel;
	private float warpSpeed;
	private boolean isTravellingAtWarp;
	private boolean hasCrashed;
	
	public Taxi(float x, float y){
		fuel = 300;//fuel of the taxi
		thrusterSpeed = 0.01f; //speed of the taxi's regular movements
		warpSpeed = 0.2f; //much faster speed of warpspeed
		isTravellingAtWarp = false;//boolean value-indicates if taxi is travelling at WS or not
		hasCrashed = false; //indicates if that taxi crashed into a planet or landed safely
		graphic = new Graphic("TAXI");
		graphic.setPosition(x, y);
	}
	
	/**
	 * Adds fuel to the taxi object whenever it collides with a gascloud.
	 * @param fuel
	 */
	public void addFuel(float fuel){
		this.fuel = this.fuel + fuel; 
	}
	
	public float getFuel(){
		return fuel; //get the current fuel status.
	}
	
	public boolean checkCollision(Graphic other){
		return graphic.isCollidingWith(other); //check if the taxi is colliding with a planet
	}
	
	public void crash(){ //Disables vehicle controls when called to depict the immobilized taxi after a crash.
		hasCrashed = true;
		graphic.setAppearance("EXPLOSION");	//therefore setting its appearance to an explosion
	}
	
	/**
	 * This getter method returns true or false depending on if the taxi has crashed with a planet.
	 * True if collision occurred, false if not.
	 * @return
	 */
	public boolean hasCrashed(){
		return hasCrashed; //bolean value returns true depending on the setter method above 
	}
	
	public void setWarp(float x, float y){//coordinates of the star the taxi is heading toward at warpspeed.
		graphic.setDirection(x, y); // Taxi should face the star we clicked.
		isTravellingAtWarp = true;  //Sets the below getter method (isTravellingAtWarp()) to true when warpspeed is activated.
	}
	
	public boolean isTravellingAtWarp(){ 
		return isTravellingAtWarp;
	}
	
	/**
	 * This method controls the mobile abilities of the taxi when it is moving either at thruster speed or warpspeed. 
	 * It allows for the wrapping of the taxi across the screen so it doesnt disappear and gets lost when it travels outside
	 * of the screen borders. 
	 * @param time
	 * @return
	 */
	public boolean update(int time){
		graphic.draw();		
		
		if (!hasCrashed()) { //If the taxi hasn't crashed, then allow for these warpspeed controls and movements to occur. Also encompasses the abilities of thrusters.
			if (isTravellingAtWarp()) { //Executes if the setWarp() method was called from the WarpStar class.
				
				//If the taxi travels too far right off of the screen, set it back to the leftmost position.
				if ((graphic.getX() + warpSpeed*time*graphic.getDirectionX()) > GameEngine.getWidth()) {
					graphic.setX((graphic.getX() + 1) % GameEngine.getWidth());
				}
				//If the taxi travels too far left off of the screen, then set it back to the rightmost position.
				else if ((graphic.getX() + warpSpeed*time*graphic.getDirectionX()) < 0) {
					graphic.setX(GameEngine.getWidth());
				}
				//else (it is in valid regions of the screen), allow it travel as normal.
				else {
					graphic.setX(graphic.getX() + warpSpeed*time*graphic.getDirectionX());
				}
				
				//If the taxi travels up past the screen, set it back to the bottom-most position.
				if ((graphic.getY() + warpSpeed*time*graphic.getDirectionY()) > GameEngine.getHeight()) {
					graphic.setY((graphic.getY()+1) % GameEngine.getHeight());
				}
				//If the taxi travels too down past the screen, then set it back to the uppermost position.
				else if ((graphic.getY() + warpSpeed*time*graphic.getDirectionY()) < 0) {
					graphic.setY(GameEngine.getHeight());
				}
				else {
					graphic.setY(graphic.getY() + warpSpeed*time*graphic.getDirectionY());
				}
			}	
		
			if (fuel > 0f) { //Prevents taxi from moving if it's out of fuel.
				if (GameEngine.isKeyHeld("D") || GameEngine.isKeyHeld("RIGHT")) {//move right with keyboard commands d and right key.
					graphic.setDirection(0f);
					
					if (graphic.getX() > GameEngine.getWidth()) {//Wraps taxi back to original position when travelling past right-hand border
						graphic.setX((graphic.getX() + 1) % GameEngine.getWidth());
					}
					else {
						graphic.setX(graphic.getX() + thrusterSpeed *time*graphic.getDirectionX());
					}
				
					fuel -=  thrusterSpeed * time;
					isTravellingAtWarp = false;
				}
				if (GameEngine.isKeyHeld("A") || GameEngine.isKeyHeld("LEFT")) { //move left with keyboard commands a and left arrow button
					graphic.setDirection((float)Math.PI);
					
					if (graphic.getX() < 0) {//If the taxi goes too far left, then make it come out of the opposite side of the screen (righthand)
						graphic.setX(GameEngine.getWidth());
					}
					else {
						graphic.setX(graphic.getX() + thrusterSpeed *time*graphic.getDirectionX());
					}
					
					fuel -=  thrusterSpeed * time;
					isTravellingAtWarp = false;
				}
				if (GameEngine.isKeyHeld("W") || GameEngine.isKeyHeld("UP")) {//move up with keyboard commands w and up arrow button
					graphic.setDirection((float)Math.PI/2);
					
					if (graphic.getY() < 0) { //wraps taxi back to original position when travelling past upper-hand border
						graphic.setY(GameEngine.getHeight());
					}
					else {
						graphic.setY(graphic.getY() + thrusterSpeed *time*graphic.getDirectionY());	
					}
					
					fuel -=  thrusterSpeed * time;
					isTravellingAtWarp = false;
				}
				if (GameEngine.isKeyHeld("S") || GameEngine.isKeyHeld("DOWN")) { //move down with keyboard commands s and the down key 
					graphic.setDirection((float)(Math.PI)*3/2);
					
					if(graphic.getY() > GameEngine.getHeight()) { //wraps taxi back to original position when travelling past lower-hand border
						graphic.setY((graphic.getY()+1) % GameEngine.getHeight());
					}
					else {
						graphic.setY(graphic.getY() + thrusterSpeed*time*graphic.getDirectionY());
					}
					
					fuel -= thrusterSpeed * time;
					isTravellingAtWarp = false;
				}
			}
		}
		
		if (fuel <= 0 || hasCrashed()) { //If the player ran out of fuel or if they crashed, then they have lost the game.
			if (GameEngine.isKeyPressed("SPACE")) { //Space to exit the game screen after you lose.
			 	return true;
			}
		}
		return false;
	}
}