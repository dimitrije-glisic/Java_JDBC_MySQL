package student.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import student.db.DB;

public class ArticleDAO {
	
	
	private Connection con = null;
	private Statement st = null;
	private PreparedStatement pst = null;
	
	public int createArticle(int shopId, String articleName, int articlePrice) {
		
		try {
			
			con = DB.getInstance().getConnection();
			
			String sql = "insert into Article (IdShop, Name, Price, Quantity) values (?,?,?,?)";
			
			pst = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setInt(1, shopId);
			pst.setString(2, articleName);
			pst.setInt(3, articlePrice);
			pst.setInt(4,0);
			
			pst.executeUpdate();
			
			ResultSet rs = pst.getGeneratedKeys();
			rs.next();			
			int aid = (int) rs.getLong(1);
			
			return aid;
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (pst != null) {
					pst.close();
				}			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		return -1;
	}
	
	
	
}
