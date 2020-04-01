package student.db.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import student.db.DB;

public class BuyerDAO {

	private Connection con = null;
	private Statement st = null;
	private PreparedStatement pst = null;
	
	public int createBuyer(String name, int cityId) {

		int buyerId = -1;

		try {

			con = DB.getInstance().getConnection();

			String sql = "insert into Buyer (Name,Credit,IdCity) values (?,?,?)";

			pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setString(1, name);
			pst.setBigDecimal(2, new BigDecimal("0").setScale(3));
			pst.setInt(3, cityId);

			int affectedRows = pst.executeUpdate();

			if (affectedRows == 0) {
				return -1;
			}

			ResultSet generatedKeys = pst.getGeneratedKeys();
			if (generatedKeys.next()) {
				buyerId = (int) generatedKeys.getLong(1);
			}

			return buyerId;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public int setCity(int buyerId, int cityId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "update Buyer set IdCity=" + cityId + " where IdBuyer=" + buyerId;

			int affRows = st.executeUpdate(sql);

			if (affRows > 0)
				return 1;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public int getCity(int buyerId) {

		int cityId = 0;

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdCity from Buyer where IdBuyer=" + buyerId;

			ResultSet rs = st.executeQuery(sql);

			if (!rs.next()) {
				return -1;
			}

			return rs.getInt(1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {

		BigDecimal cred = null;

		try {

			con = DB.getInstance().getConnection();

			st = con.createStatement();

			String sql = "select Credit from Buyer where IdBuyer=" + buyerId;

			ResultSet rs = st.executeQuery(sql);

			if (!rs.next())
				return null;

			cred = rs.getBigDecimal(1).setScale(3);
			
			BigDecimal newCred = new BigDecimal(cred.toString()).add(credit).setScale(3);

			sql = "update Buyer set Credit=" + newCred + " where IdBuyer=" + buyerId;

			st.executeUpdate(sql);

			return newCred;

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

	public BigDecimal getCredit(int buyerId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Credit from Buyer where IdBuyer=" + buyerId;

			ResultSet rs = st.executeQuery(sql);

			if (!rs.next())
				return null;

			return rs.getBigDecimal(1);

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

	public int createOrder(int buyerId) {

		try {

			con = DB.getInstance().getConnection();
			String sql = "insert into [Order] (Ammount, IdBuyer, OrderState) values(?,?,?)";
			pst = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setBigDecimal(1, new BigDecimal("0").setScale(3));
			pst.setInt(2, buyerId);
			pst.setString(3, "created");

			pst.executeUpdate();
			
			ResultSet rs = pst.getGeneratedKeys();
			rs.next();

			int orderId = (int) rs.getLong(1);
			
			return orderId;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public List<Integer> getOrders(int buyerId) {

		List<Integer> result = new ArrayList<>();
		
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();
			
			String sql = "select IdOrder from [Order] where IdBuyer="+buyerId;

			ResultSet rs = st.executeQuery(sql);
			
			Integer orderId;
			while(rs.next()) {
				orderId = rs.getInt(1);
				result.add(orderId);
			}
			
			return result;
			
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

}
