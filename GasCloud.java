public class GasCloud {
	
	private Graphic graphic;
	private float rotationSpeed;
	private boolean shouldRemove = false;

	public GasCloud(float xPosition, float yPosition, float direction){
		graphic = new Graphic("");
		graphic.setPosition(xPosition, yPosition);
		rotationSpeed = 0f; 
		graphic.setDirection(direction);
	}
	
	public void update(int time){	
		graphic.setAppearance("GAS");
		rotationSpeed = 0.001f * time;
		graphic.setDirection(graphic.getDirection() + rotationSpeed);
		graphic.draw();
		return;
	}
	

	public void handleFueling(Taxi taxi){
		if(taxi.checkCollision(graphic)){
			taxi.addFuel(20f); 
			shouldRemove = true;
		}
		else
			shouldRemove = false;
		return;
	}
	
	public boolean shouldRemove(){
		return shouldRemove;
	}
	
}



