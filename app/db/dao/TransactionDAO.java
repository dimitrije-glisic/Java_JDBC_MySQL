package student.db.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import student.gd150330_GeneralOperations;
import student.db.DB;

public class TransactionDAO {

	private Connection con;
	private Statement st;

	public BigDecimal getBuyerTransactionsAmmount(int buyerId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();
			
			
			//proveri kupca
			
			
			String sql = "select Ammount from [Transaction] where IdBuyer=" + buyerId;
			ResultSet rs = st.executeQuery(sql);

			double acc = 0;
			while (rs.next()) {
				acc += rs.getBigDecimal(1).doubleValue();
			}

			if (acc == 0)
				return new BigDecimal("0");

			BigDecimal amnt = new BigDecimal(Double.toString(acc)).setScale(3);

			return amnt;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return new BigDecimal("-1");
	}

	public BigDecimal getShopTransactionsAmmount(int shopId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Ammount from Shop where IdShop=" + shopId;
			ResultSet rs = st.executeQuery(sql);
			rs.next();

			BigDecimal ammount = rs.getBigDecimal(1);

			if (ammount.compareTo(new BigDecimal("0")) == 0)
				return null;

			return ammount;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public List<Integer> getTransactionsForBuyer(int buyerId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdTransaction from [Transaction] where IdBuyer=" + buyerId;
			ResultSet rs = st.executeQuery(sql);

			List<Integer> res = new ArrayList<>();

			while (rs.next()) {
				res.add(rs.getInt(1));
			}

			return res;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public int getTransactionForBuyersOrder(int orderId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdTransaction from [Transaction] where IdOrder=" + orderId;
			ResultSet rs = st.executeQuery(sql);

			if (rs.next())
				return rs.getInt(1);

			return -1;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public int getTransactionForShopAndOrder(int orderId, int shopId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdTransaction from [Transaction] where IdOrder=" + orderId;
			ResultSet rs = st.executeQuery(sql);

			if (!rs.next())
				return -1;

			int transactionId = rs.getInt(1);

			/*
			 * sql = "select IdTransaction from TransactionShop where IdTransaction=" +
			 * transactionId + " and IdShop=" + shopId; rs = st.executeQuery(sql);
			 * 
			 * rs.next(); int tsId = rs.getInt(1);
			 */

			return transactionId;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public List<Integer> getTransactionsForShop(int shopId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdTransaction, Status from TransactionShop where IdShop=" + shopId;
			ResultSet rs = st.executeQuery(sql);

			List<Integer> res = new ArrayList<>();

			while (rs.next()) {
				String status = rs.getString(2);

				if ("sent".equals(status)) {
					res.add(rs.getInt(1));
				}
			}

			if (res.isEmpty()) {
				return null;
			}

			return res;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Ammount from [Transaction] where IdOrder=" + orderId;
			ResultSet rs = st.executeQuery(sql);

			if (!rs.next()) {
				return new BigDecimal("-1");
			}

			BigDecimal res = rs.getBigDecimal(1);

			return res;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return new BigDecimal("-1");

	}

	public BigDecimal getAmmountThatShopRecievedForOrder(int orderId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Ammount from TransactionShop where Status='sent' and IdOrder=" + orderId;

			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {

				BigDecimal amnt = rs.getBigDecimal(1);

				return amnt;
			}

			return new BigDecimal("0");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return new BigDecimal("-1");

	}

	public BigDecimal getTransactionAmount(int transactionId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Ammount from [Transaction] where IdTransaction=" + transactionId;

			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {

				BigDecimal amnt = rs.getBigDecimal(1);

				return amnt;
			}

			return new BigDecimal("-1");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return new BigDecimal("-1");

	}

	public BigDecimal getSystemProfit() {
		return OrderDAO.systemProfit;
	}

	public Calendar getTimeOfExecution(int transactionId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select ExecTime from [Transaction] where IdTransaction=" + transactionId;

			ResultSet rs = st.executeQuery(sql);

			rs.next();

			java.sql.Date execTime = rs.getDate(1);

			//java.util.Date date = new Date(execTime.getTime());

			Calendar time = Calendar.getInstance();

			time.setTimeInMillis(execTime.getTime());

			return time;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;

	//	Calendar time = gd150330_GeneralOperations.executionTime.get(transactionId);

		//return time;
	}

}// transactionDAO
