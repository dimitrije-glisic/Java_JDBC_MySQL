package student;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import operations.OrderOperations;
import student.db.dao.OrderDAO;

public class gd150330_OrderOperations implements OrderOperations {
	
	
	@Override
	public int addArticle(int orderId, int articleId, int count) {
		OrderDAO o = new OrderDAO();

		int res = o.addArticle(orderId, articleId, count);

		return res;
	}

	@Override
	public int removeArticle(int orderId, int articleId) {

		OrderDAO o = new OrderDAO();

		int res = o.removeArticle(orderId, articleId);

		return res;

	}

	@Override
	public List<Integer> getItems(int orderId) {
		OrderDAO o = new OrderDAO();

		List<Integer> res = o.getItems(orderId);

		return res;
	}

	@Override
	public int completeOrder(int orderId) {
		OrderDAO o = new OrderDAO();

		int res = o.completeOrder(orderId);

		return res;
	}

	@Override
	public BigDecimal getFinalPrice(int orderId) {
		
		OrderDAO o = new OrderDAO();

		BigDecimal res = o.getFinalPrice(orderId);

		return res;
	}

	@Override
	public BigDecimal getDiscountSum(int orderId) {
		
		OrderDAO o = new OrderDAO();

		BigDecimal res = o.getDiscountSum(orderId);

		return res;
		
	}

	@Override
	public String getState(int orderId) {
		OrderDAO o = new OrderDAO();

		String res = o.getState(orderId);

		return res;
	}

	@Override
	public Calendar getSentTime(int orderId) {
		OrderDAO o = new OrderDAO();

		Calendar res = null;
		
		res = o.getSentTime(orderId);

		return res;
	}

	@Override
	public Calendar getRecievedTime(int orderId) {
		OrderDAO o = new OrderDAO();

		Calendar res = null;
		
		res = o.getReceivedTime(orderId);

		return res;
	}

	@Override
	public int getBuyer(int orderId) {
		OrderDAO o = new OrderDAO();

		int res = o.getBuyer(orderId);

		return res;
	}

	@Override
	public int getLocation(int orderId) {
		OrderDAO o = new OrderDAO();

		int res = o.getLocation(orderId);

		return res;
	}

}
