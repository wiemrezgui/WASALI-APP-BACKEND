package com.example.service;

public class MapService {
	
	
	 public Long calculateDist(double lat1, double lon1, double lat2, double lon2) {
		 	
	        double dLt = Math.toRadians(lat2 - lat1);
	        double dLn = Math.toRadians(lon2 - lon1);
	        double a = Math.sin(dLt / 2) * Math.sin(dLt / 2) +
	                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	                   Math.sin(dLn / 2) * Math.sin(dLn / 2); 
	        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	        return Math.round(6371 * c);
}

}
