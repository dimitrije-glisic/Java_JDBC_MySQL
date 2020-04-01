package student.db.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import student.db.DB;

public class GeneralDAO {
	
	private Connection con = null;
	private Statement st = null;
	
	public void eraseAll() {
	
		try {
			
			con = DB.getInstance().getConnection();
			st = con.createStatement();
			
			String sql = "EXEC sp_MSForEachTable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'";			
			st.executeUpdate(sql);
			
			sql = "EXEC sp_MSForEachTable 'DELETE FROM ?'";
			st.executeUpdate(sql);
			
			sql = "EXEC sp_MSForEachTable 'ALTER TABLE ? WITH CHECK CHECK CONSTRAINT ALL'";
			st.executeUpdate(sql);
			
			sql = "EXEC sp_MSForEachTable 'DBCC CHECKIDENT(''?'', RESEED, 0)'";
			st.executeUpdate(sql);
			
		}catch (Exception e) {
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
		
		
	}

}
