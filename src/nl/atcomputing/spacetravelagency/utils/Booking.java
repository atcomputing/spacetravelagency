package nl.atcomputing.spacetravelagency.utils;

import java.util.ArrayList;
import java.util.Collections;

import nl.atcomputing.spacetravelagency.Planet;

/**
 * Current positions provided by: 
 * http://www.planetary-aspects.com/curr_asp/curr_posns.php
 * 16 march 2012
 * 
  Heliocentric
ecliptic-of-date
longitude in degrees	Distance from Sun
                          in AU
Mercury	      158.77	0.359159
Venus	      124.30	0.718490
Earth	      176.30	0.994911
Mars	      169.20	1.661620
Jupiter	      47.95	    4.986600
Saturn	      205.20	9.713603
Uranus	      4.43	    20.072888
Neptune	      330.76	29.999926

 * For the calculations below see http://www.davidcolarusso.com/astro/#
 *
 */

public class Booking {

	public ArrayList<Planet> getTravellingOrder(ArrayList<Planet> planetNames) {

		Collections.sort(planetNames);

		ArrayList<Planet> planetNamesSorted = new ArrayList<Planet>();

		for( Planet planet : planetNames ) {
			planetNamesSorted.add(planet);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//Do nothing...
			}
		}

		return planetNamesSorted;
	}
}