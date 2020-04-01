package student;

import java.math.BigDecimal;
import java.util.List;

import operations.BuyerOperations;
import student.db.dao.BuyerDAO;

public class gd150330_BuyerOperations implements BuyerOperations {

	@Override
	public int createBuyer(String name, int cityId) {

		BuyerDAO b = new BuyerDAO();

		int res = b.createBuyer(name, cityId);

		return res;
	}

	@Override
	public int setCity(int buyerId, int cityId) {

		BuyerDAO b = new BuyerDAO();

		int res = b.setCity(buyerId, cityId);

		return res;
	}

	@Override
	public int getCity(int buyerId) {
		BuyerDAO b = new BuyerDAO();

		int res = b.getCity(buyerId);

		return res;
	}

	@Override
	public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
		BuyerDAO b = new BuyerDAO();

		BigDecimal res = b.increaseCredit(buyerId,credit);

		return res;
	}

	@Override
	public int createOrder(int buyerId) {
		BuyerDAO b = new BuyerDAO();

		int res = b.createOrder(buyerId);

		return res;
	}

	@Override
	public List<Integer> getOrders(int buyerId) {
		BuyerDAO b = new BuyerDAO();

		List<Integer> res = b.getOrders(buyerId);

		return res;
	}

	@Override
	public BigDecimal getCredit(int buyerId) {
		
		BuyerDAO b = new BuyerDAO();

		BigDecimal res = b.getCredit(buyerId);

		return res;
		
	}

}
