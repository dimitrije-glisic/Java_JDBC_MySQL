package student;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import operations.TransactionOperations;
import student.db.dao.TransactionDAO;

public class gd150330_TransactionOperations implements TransactionOperations {

	@Override
	public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
		TransactionDAO tdao = new TransactionDAO();

		BigDecimal res = tdao.getBuyerTransactionsAmmount(buyerId);

		return res;
	}

	@Override
	public BigDecimal getShopTransactionsAmmount(int shopId) {
		TransactionDAO tdao = new TransactionDAO();

		BigDecimal res = tdao.getShopTransactionsAmmount(shopId);

		return res;
	}

	@Override
	public List<Integer> getTransationsForBuyer(int buyerId) {
		TransactionDAO tdao = new TransactionDAO();

		List<Integer> res = tdao.getTransactionsForBuyer(buyerId);

		return res;
	}

	@Override
	public int getTransactionForBuyersOrder(int orderId) {
		TransactionDAO tdao = new TransactionDAO();

		int res = tdao.getTransactionForBuyersOrder(orderId);

		return res;
	}

	@Override
	public int getTransactionForShopAndOrder(int orderId, int shopId) {
		TransactionDAO tdao = new TransactionDAO();

		int res = tdao.getTransactionForShopAndOrder(orderId,shopId);

		return res;
	}
	
	
	
	@Override
	public List<Integer> getTransationsForShop(int shopId) {
		TransactionDAO tdao = new TransactionDAO();

		List<Integer> res = tdao.getTransactionsForShop(shopId);

		return res;
	}

	@Override
	public Calendar getTimeOfExecution(int transactionId) {
		TransactionDAO tdao = new TransactionDAO();

		Calendar res = tdao.getTimeOfExecution(transactionId);

		return res;
	}

	@Override
	public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
		TransactionDAO tdao = new TransactionDAO();

		BigDecimal res = tdao. getAmmountThatBuyerPayedForOrder(orderId);

		return res;
	}

	@Override
	public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
		TransactionDAO tdao = new TransactionDAO();

		BigDecimal res = tdao. getAmmountThatShopRecievedForOrder(orderId);

		return res;
	}

	@Override
	public BigDecimal getTransactionAmount(int transactionId) {
		TransactionDAO tdao = new TransactionDAO();

		BigDecimal res = tdao. getTransactionAmount(transactionId);

		return res;
	}

	@Override
	public BigDecimal getSystemProfit() {
		TransactionDAO tdao = new TransactionDAO();

		BigDecimal res = tdao. getSystemProfit();

		return res;
	}

}
