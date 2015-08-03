package clicks;
import java.util.Observable;
import java.util.Observer;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class ClickListener implements Observer, NativeMouseInputListener {
	
	private UpdateClicks clickListener;
	
	public ClickListener(UpdateClicks x) {
		this.clickListener = x;
		clickListener.addObserver(this);
	}
	
    public void nativeMouseClicked(NativeMouseEvent e) {
    	
    }

    public void nativeMousePressed(NativeMouseEvent e) {
    	clickListener.click(e.getX(),e.getY());
    }

    public void nativeMouseReleased(NativeMouseEvent e) {
        //System.out.println("Mouse Released: " + e.getButton());
    }

    public void nativeMouseMoved(NativeMouseEvent e) {
        //System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
    }

    public void nativeMouseDragged(NativeMouseEvent e) {
        //System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
    }

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}