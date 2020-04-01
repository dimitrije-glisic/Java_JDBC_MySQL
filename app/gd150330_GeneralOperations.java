package student;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import operations.GeneralOperations;
import student.db.DB;
import student.db.dao.GeneralDAO;

public class gd150330_GeneralOperations implements GeneralOperations {

	private Calendar time;

	private static int cnt = 0;

	private static Calendar initTime;

	public static Calendar timeStatic;

	// for transactions
	public static Map<Integer, Calendar> executionTime = new HashMap<>();

	// -------------------------------------------
	public Calendar getInitalTime() {
		return initTime;
	}

	public Calendar getCurrentTimeStatic() {
		return timeStatic;
	}

	public int getCnt() {
		return cnt;
	}

	@Override
	public void setInitialTime(Calendar time) {
		this.time = time;

		// this.initTime = Calendar.getInstance();

		timeStatic = Calendar.getInstance();
		timeStatic.setTimeInMillis(this.time.getTimeInMillis());

		cnt = 0;
		
		setSysTime();
		
	}

	@Override
	public Calendar time(int days) {
		cnt += days;
		Calendar currTime = Calendar.getInstance();
		currTime.setTimeInMillis(timeStatic.getTimeInMillis());

		timeStatic.add(Calendar.DAY_OF_MONTH, days);

		
		
		
		setExecutionAndSysTime(days);
		
		
		
		return currTime;
	}

	private void setSysTime() {
		Connection con = null;
		Statement st = null;

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();
			
			java.sql.Timestamp time = new Timestamp(timeStatic.getTimeInMillis());
			
			
			String sql = "insert into System (Systime) values ('" + time + "')";
			
			st.executeUpdate(sql);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(st!= null) {
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	private void setExecutionAndSysTime(int days) {
		
		Set<Integer> keys = executionTime.keySet();
		
		for(Integer k : keys) {
			
			executionTime.remove(k);
			
			Calendar newTime = Calendar.getInstance();
			newTime.setTimeInMillis(timeStatic.getTimeInMillis());
			
			executionTime.put(k,newTime);
			
		}
		
		
		
		Connection con = null;
		Statement st = null;

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			// java.util.Date date = new Date (timeStatic.getTimeInMillis());

			java.sql.Timestamp sqlDate = new Timestamp(timeStatic.getTimeInMillis());

			// are there transactions check

			String sql = "select * from [Transaction]";
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				//update where status != arrived
				sql = "update [Transaction] set ExecTime='"+sqlDate+"'";
				st.executeUpdate(sql);				
			}
			
			sql = "update System set Systime='"+sqlDate+"'";
			st.executeUpdate(sql);

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

	}

	@Override
	public Calendar getCurrentTime() {
		return timeStatic;
	}

	@Override
	public void eraseAll() {
		GeneralDAO g = new GeneralDAO();
		g.eraseAll();
	}

}
