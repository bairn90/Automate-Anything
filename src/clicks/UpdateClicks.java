package clicks;
import java.util.Observable;

public class UpdateClicks extends Observable {

	private int x,y;
	private boolean status = false;
	
    public void click(int x, int y) {
    	if (status) {
        	this.x = x;
        	this.y = y;
        	setChanged();
        	notifyObservers();	
    	}
    }
    
    public boolean isActive() {
    	return status;
    }
    
    public void makeActive() {
    	status = true;
    }
    
    public void makeInactive() {
    	status = false;
    }
    
    public int getY() {
    	return y;
    }
    
    public int getX() {
    	return x;
    }

}