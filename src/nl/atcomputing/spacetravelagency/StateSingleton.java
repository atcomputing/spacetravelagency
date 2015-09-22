package nl.atcomputing.spacetravelagency;

import nl.atcomputing.spacetravelagency.order.Order;
import nl.atcomputing.spacetravelagency.utils.CalculateTravellingOrder;


public class StateSingleton {
	private static StateSingleton instance;

	private Planet[] travellingOrder;
	private String invitedFriend;
	private int totalPrice;
	private Order order;
	private CalculateTravellingOrder calculateTravellingOrder;
	
	private enum STATES { SELECT_DESTINATIONS, CHECKING_OUT, PLACE_ORDER, SHOWING_PLANET_INFO };
	private STATES state = STATES.SELECT_DESTINATIONS;
	
	public static StateSingleton getInstance() {
		if(instance == null) {
			instance = new StateSingleton();
		}
		return instance;
	}
	
	public void setTravellingOrder(Planet[] travellingOrder) {
		this.travellingOrder = travellingOrder;
	}
	
	public Planet[] getTravellingOrder() {
		return travellingOrder;
	}
	
	public void setInvitedFriend(String invitedFriend) {
		this.invitedFriend = invitedFriend;
	}
	
	public String getInvitedFriend() {
		return invitedFriend;
	}
	
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public int getTotalPrice() {
		return totalPrice;
	}
	
	public boolean isCheckingOut() {
		return this.state == STATES.CHECKING_OUT;
	}
	
	public boolean isSelectingDestinations() {
		return this.state == STATES.SELECT_DESTINATIONS;
	}
	
	public boolean isPlacingOrder() {
		return this.state == STATES.PLACE_ORDER;
	}
	
	public void setCheckingOut() {
		this.state = STATES.CHECKING_OUT;
	}
	
	public void setSelectingDestinations() {
		this.state = STATES.SELECT_DESTINATIONS;
	}
	
	public void setPlacingOrder() {
		this.state = STATES.PLACE_ORDER;
	}
	
	public boolean isShowingPlanetInfo() {
		return this.state == STATES.SHOWING_PLANET_INFO;
	}
	
	public void setShowingPlanetInfo() {
		this.state = STATES.SHOWING_PLANET_INFO;
	}
	
	public void setOrder(Order order) {
		this.order = order;
	}
	
	public Order getOrder() {
		return order;
	}
	
	public void setCalculateTravellingOrderTask(
			CalculateTravellingOrder calculateTravellingOrder) {
		this.calculateTravellingOrder = calculateTravellingOrder;
	}
	
	public CalculateTravellingOrder getCalculateTravellingOrderTask() {
		return calculateTravellingOrder;
	}
	
	@Override
	public String toString() {
		return super.toString()+
				"\ntravellingOrder="+this.travellingOrder+
				"\ntotalPrice="+this.totalPrice+
				"\ninvitedFriend="+this.invitedFriend+
				"\nstate="+this.state.name();
	}
}
