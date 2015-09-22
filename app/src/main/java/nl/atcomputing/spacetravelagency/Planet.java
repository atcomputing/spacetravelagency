/**
 * 
 * Copyright 2015 AT Computing BV
 *
 * This file is part of Space Travel Agency.
 *
 * Space Travel Agency is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Space Travel Agency is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Space Travel Agency.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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