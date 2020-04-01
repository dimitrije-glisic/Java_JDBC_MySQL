package student;

import java.util.List;

import operations.CityOperations;
import student.db.dao.*;
import student.db.dao.CityDAO;

public class gd150330_CityOperations implements CityOperations {

	@Override
	public int createCity(String name) {
		
		CityDAO c = new CityDAO();
		int result = c.createCity(name);
		
		return result;
	}

	@Override
	public List<Integer> getCities() {
		
		CityDAO c = new CityDAO();
		
		List<Integer> res = c.getCities();
		
		return res;
	}

	@Override
	public int connectCities(int cityId1, int cityId2, int distance) {
		
		CityDAO c = new CityDAO();
		
		
		//check if both cities exist in a system
		//check if there is already a connection between two cities
		//make a connection
		
		int res = c.connectCities(cityId1,cityId2,distance);
		
		
		return res;
	}

	@Override
	public List<Integer> getConnectedCities(int cityId) {
		
		CityDAO c = new CityDAO();
		
		List<Integer> res = c.getConnectedCities(cityId);
		
		return res;
	}

	@Override
	public List<Integer> getShops(int cityId) {
		
		CityDAO c = new CityDAO();
		
		List<Integer> res = c.getShops(cityId);
		
		return res;
		
		
	}

}
