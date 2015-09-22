package nl.atcomputing.spacetravelagency;



public class Planet implements Comparable<Planet> {

	private String name;
	private String starSystem;
	private int distance;
	private int price;
	private String info;
	
	public Planet() {
		this("unknown", 0, -1, "unknown", "");
	}

	public Planet(String name, int distance, int price, String starSystem, String info) {
		this.name = name;
		this.distance = distance;
		this.price = price;
		this.starSystem = starSystem;
		this.info = info;
	}
	
	public Planet(String name, int distance, int price) {
		this(name, distance, price, "unknown", "");
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setStarSystem(String starSystem) {
		this.starSystem = starSystem;
	}
	
	public String getStarSystem() {
		return starSystem;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setHelioCentricDistance(int distance) {
		this.distance = distance;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getHelioCentricDistance() {
		return this.distance;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	//Required for Arrays.sort
	public int compareTo(Planet p) {
		return this.distance - p.getHelioCentricDistance();
	}
}