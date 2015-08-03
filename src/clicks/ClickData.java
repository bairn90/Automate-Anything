package clicks;

import java.util.LinkedList;

public class ClickData implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<LinkedList<Integer>> clicksPos = null;
	private LinkedList<Integer> clickActions = null, clickDelays = null;
	
	public void setClick(LinkedList<LinkedList<Integer>> clicksPos) {
		this.clicksPos = clicksPos;
	}
	
	public void setClickData(LinkedList<Integer> clickActions, LinkedList<Integer> clickDelays) {
		this.clickActions = clickActions;
		this.clickDelays = clickDelays;
	}
	
	public LinkedList<Integer> getActions() {
		return clickActions;
	}
	
	public LinkedList<Integer> getDelays() {
		return clickDelays;
	}
	
	public LinkedList<LinkedList<Integer>> getClickPos() {
		return clicksPos;
	}
}
