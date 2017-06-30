public class WarpStar {
	private Graphic graphic;
	
	public WarpStar(float x, float y){
		graphic = new Graphic("");
		graphic.setPosition(x, y); //sets the positions of the warpstar from the Level class's constructor.
	}
	
	public void update(){	
		graphic.setAppearance("WARP_STAR");
		graphic.draw();
	}
	 
	/**If the taxi has fuel left in it, and the mouse pointer has clicked on any of the 6 warpStar graphics,
	 * then the setWarp() method gets called, allowing for the taxi to travel at warpspeed toward this star,
	 * and beyond it, until a new star is clicked, repeating the action. 
	 * @param taxi
	 */
	public void handleNavigation(Taxi taxi){
		if(taxi.getFuel() > 0f) 
			if(graphic.isCoveringPosition(GameEngine.getMouseX(), GameEngine.getMouseY()))
				if(GameEngine.isKeyPressed("MOUSE")) 
					taxi.setWarp(graphic.getX(), graphic.getY());
		}
	}
