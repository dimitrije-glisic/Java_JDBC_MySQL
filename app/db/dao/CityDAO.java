package student.db.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.sql.*;

import student.util.MyPair;

import student.gd150330_CityOperations;
import student.db.DB;

public class CityDAO {

	private Connection con = null;
	Statement stmt = null;

	PreparedStatement pstmt = null;

	public int createCity(String name) {

		int res = -1;
		PreparedStatement ps = null;
		try {
			
			con = DB.getInstance().getConnection();
			stmt = con.createStatement();
			
			//is there city with Name = name
			
			String sql = "select IdCity from City where Name='"+ name + "'";		
			ResultSet rss =stmt.executeQuery(sql);
			
			if(rss.next()) {
				return rss.getInt(1);
			}
			
			
			
			sql = "insert into City (Name) values (?)";
			ps = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			
			int cityId = (int) rs.getLong(1);
			
			return cityId;
			
		} catch (Exception e) {
			e.printStackTrace();
			return res;
		} finally {
			try {
				if(ps!=null) {
					ps.close();
				}				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public List<Integer> getCities() {
		try {
			con = DB.getInstance().getConnection();
			stmt = con.createStatement();

			String sql = "select * from City";

			ResultSet rs = stmt.executeQuery(sql);

			List<Integer> res = new ArrayList<>();
			while (rs.next()) {
				res.add(rs.getInt(1));
			}

			return res;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean cityExist(int cityId1) {

		try {

			con = DB.getInstance().getConnection();
			stmt = con.createStatement();

			String sql = "select * from City where IdCity=" + cityId1;

			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				return true;
			}

			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private boolean isConnected(int cityId1, int cityId2) {

		boolean res = false;

		try {

			con = DB.getInstance().getConnection();
			stmt = con.createStatement();

			String sql = "select * from CityToCity where (FirstCity=" + cityId1 + " and SecondCity=" + cityId2 + ")"
					+ " or (FirstCity=" + cityId2 + " and SecondCity=" + cityId1 + ")";

			ResultSet rs = stmt.executeQuery(sql);

			res = rs.next();
			return res;

		} catch (Exception e) {
			e.printStackTrace();
			return res;
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public int connectCities(int cityId1, int cityId2, int distance) {

		try {

			if (cityId1 == cityId2)
				return -1;

			if (!cityExist(cityId1)) {
				return -1;
			}

			if (!cityExist(cityId2)) {
				return -1;
			}

			if (isConnected(cityId1, cityId2)) {
				return -1;
			}

			con = DB.getInstance().getConnection();

			String sql = "insert into CityToCity (FirstCity, SecondCity, Distance) values (?, ?, ?)";

			pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			// pstmt.setInt(1, 1);
			pstmt.setInt(1, cityId1);
			pstmt.setInt(2, cityId2);
			pstmt.setInt(3, distance);

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows == 0) {
				return -1;
			}

			ResultSet rs = pstmt.getGeneratedKeys();

			int lineId = -1;
			if (rs.next()) {
				lineId = (int) rs.getLong(1);
			}

			return lineId;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	
	
	
	 static boolean endFlag = false;
	
	 static boolean firstCond = false;
	 static boolean secondCond = false;
	
	
	static Set<Integer> resultSet = new HashSet<>();
	
	gd150330_CityOperations city = new gd150330_CityOperations(); 
	
	int forbiden = 0;
	
	static boolean flagFirst = true;
	
	
	static int buyerId;
	static int i = 0;
	static int V = 0;
	
	{
		  //List<List<Node> > adj = new ArrayList<List<Node> >(); 
	}
	
	static List<Integer> temp = new ArrayList<>();
	
	
	static int srcCity;
	
	public List<Integer> getConnectedCities(int cityId) {
		/*
		List<Integer> preResult = new ArrayList<>();
		
		if(flagFirst) {
			srcCity = cityId;
		}
		try {

			con = DB.getInstance().getConnection();
			stmt = con.createStatement();
			
			
			String sql= "select FirstCity, SecondCity from CityToCity where FirstCity=" + cityId + " or SecondCity="
					+ cityId;
			
			
			ResultSet rs = stmt.executeQuery(sql);

			int first, second;
			endFlag = true;
			while (rs.next()) {

				first = rs.getInt(1);
				second = rs.getInt(2);

				//int distance = findDistance(first,second);
				
				if(!flagFirst && (first ==srcCity || second ==srcCity))
					continue;
				
				if (first == cityId) {
					
					preResult.add(second);
					firstCond = resultSet.add(second);
				} else {
					preResult.add(first);
					secondCond = resultSet.add(first);
				}
			
				endFlag = ! (firstCond || secondCond);
			}
			
			
			flagFirst = false;

			if (endFlag){
				return null;
			}
			
			
			List<Integer> tmp = null; 
			for(Integer cid : preResult) {			
				getConnectedCities(cid);
			}
			
			temp.addAll(resultSet);
			
			return temp;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;*/
		
		try {

			con = DB.getInstance().getConnection();
			stmt = con.createStatement();
			
			
			String sql = "select FirstCity, SecondCity from CityToCity where FirstCity="+cityId + " or SecondCity="+cityId;
			
			ResultSet rs = stmt.executeQuery(sql);
			
			List<Integer> result = new ArrayList<>();
			
			int first, second;
			while(rs.next()) {
				
				first = rs.getInt(1);
				second = rs.getInt(2);
				
				if(first == cityId) {
					result.add(second);
				} else {
					result.add(first);
				}
				
			}
			
			if(result.isEmpty()) {
				return null;
			}
			
			return result;
			
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	public List<Integer> getShops(int cityId) {

		List<Integer> res = new ArrayList<>();

		try {

			con = DB.getInstance().getConnection();
			stmt = con.createStatement();

			String sql = "select IdShop from Shop where IdCity=" + cityId;

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				int currShop = rs.getInt(1);
				res.add(currShop);
			}

			if (res.isEmpty()) {
				return null;
			}

			return res;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}// cityDAO
