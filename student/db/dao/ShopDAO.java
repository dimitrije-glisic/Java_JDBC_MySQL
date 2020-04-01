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

public class ShopDAO {

	private Connection con = null;
	private Statement st = null;

	public int createShop(String name, String cityName) {

		int idCity;

		int shopId;

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdCity from City where Name='" + cityName + "'";
			
			//da li je shopname unique
			
			
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				idCity = rs.getInt(1);

				sql = "insert into Shop (Name, IdCity, Discount, Ammount) values (?,?,?,?)";

				PreparedStatement pst = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				pst.setString(1, name);
				pst.setInt(2, idCity);
				pst.setInt(3, 0);
				pst.setBigDecimal(4, new BigDecimal("0"));
				
				pst.executeUpdate();

				ResultSet generatedKeys = pst.getGeneratedKeys();
				generatedKeys.next();
				int sid = (int) generatedKeys.getLong(1);
				return sid;
			}

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

	public int setCity(int shopId, String cityName) {

		int idCity;

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			// check if city exists

			String sql = "select * from City where Name='" + cityName + "'";

			ResultSet rs = st.executeQuery(sql);

			if (!rs.next())
				return -1;

			sql = "select IdCity from City where Name='" + cityName + "'";

			rs = st.executeQuery(sql);

			if (rs.next()) {
				idCity = rs.getInt(1);

				sql = "update Shop set IdCity="+idCity + " where IdShop="+shopId;

				int res = st.executeUpdate(sql);

				if (res > 0)
					return 1;

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return -1;

	}

	public int getCity(int shopId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			// check if city exists

			String sql = "select IdCity from Shop where IdShop=" + shopId;

			ResultSet rs = st.executeQuery(sql);
			rs.next();
			
			return rs.getInt(1);

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

	public int setDiscount(int shopId, int discountPercentage) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "update Shop set Discount=" + discountPercentage + " where IdShop=" + shopId;

			int res = st.executeUpdate(sql);

			if (res == 1)
				return 1;

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

	public int getDiscount(int shopId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Discount from Shop where IdShop=" + shopId;

			ResultSet rs = st.executeQuery(sql);

			if (rs.next())
				return rs.getInt(1);

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

	public int increaseArticleCount(int articleId, int increment) {

		int newQuantity = 0;

		if (articleId < 0 || increment < 0)
			return -1;

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Quantity from Article where IdArticle=" + articleId;

			ResultSet rs = st.executeQuery(sql);

			rs.next();
			newQuantity = rs.getInt(1);

			newQuantity += increment;

			sql = "update Article set Quantity=" + newQuantity + " where IdArticle=" + articleId;
			
			
			
			st.executeUpdate(sql);

			return newQuantity;

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

		return 0;
	}

	public int getArticleCount(int articleId) {

		int res = -1;

		if (articleId < 0)
			return res;

		try {

			// check if is there article with such id
			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Quantity from Article where IdArticle=" + articleId;

			ResultSet rs = st.executeQuery(sql);

			if (!rs.next()) {
				return res;
			}

			return rs.getInt(1);

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

		return res;
	}

	public List<Integer> getArticles(int shopId) {

		

		List<Integer> res = new ArrayList<>();

		try {
			
			con = DB.getInstance().getConnection();
			st = con.createStatement();
			
			String sql = "select IdArticle from Article where IdShop=" + shopId;
			
			ResultSet rs = st.executeQuery(sql);
			
			while(rs.next()) {			
				
				res.add(rs.getInt(1));				
			
			}
			
			if(res.isEmpty()) {
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
}
