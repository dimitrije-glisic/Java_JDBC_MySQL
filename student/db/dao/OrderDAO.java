package student.db.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import student.gd150330_BuyerOperations;
import student.gd150330_CityOperations;
import student.gd150330_GeneralOperations;
import student.gd150330_OrderOperations;
import student.gd150330_ShopOperations;
import student.db.DB;
import student.util.MyPair;
import student.util.*;

public class OrderDAO {

	private Connection con = null;
	private Statement st = null;
	private PreparedStatement pst = null;

	private int closestCity = -1;

	// <key=orderId,value=sentTime>
	private static Map<Integer, Calendar> sentTime = new HashMap<Integer, Calendar>();

	private static Map<Integer, Calendar> receivedTime = new HashMap<Integer, Calendar>();
	private static Set<Integer> completedOrder = new HashSet<>();

	private static Map<Integer, BigDecimal> systemProfitBuffer = new HashMap<>();
	static BigDecimal systemProfit = new BigDecimal("0");

	// ---------------------------------------------------------

	public int addArticle(int orderId, int articleId, int count) {

		int q = 0;

		if (count < 0) {
			return -1;
		}

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			// is there order with id=orderId

			String sql = "select * from [Order] where IdOrder=" + orderId;
			ResultSet rs = st.executeQuery(sql);

			// no such order
			if (!rs.next()) {
				return -1;
			}

			// quantity
			sql = "select Quantity from Article where IdArticle=" + articleId;

			rs = st.executeQuery(sql);

			// no such article
			if (!rs.next()) {
				return -1;
			}

			q = rs.getInt(1);

			if (q < count) {
				return -1;
			}

			q -= count;

			sql = "update Article set Quantity=" + q + " where IdArticle=" + articleId;

			st.executeUpdate(sql);

			// ORDERARTICLE INSERT OR UPDATE

			sql = "select * from OrderArticle where IdOrder=" + orderId + " and IdArticle=" + articleId;

			rs = st.executeQuery(sql);

			// update
			if (rs.next()) {

				sql = "select Quantity from OrderArticle where IdOrder=" + orderId + " and IdArticle=" + articleId;

				rs = st.executeQuery(sql);
				rs.next();
				q = rs.getInt(1);

				q += count;

				sql = "update OrderArticle set Quantity=" + q + " where IdOrder=" + orderId + " and IdArticle="
						+ articleId;

				// insert
			} else {

				sql = "insert into OrderArticle (IdOrder, IdArticle, Quantity) values (?,?,?)";
				pst = con.prepareStatement(sql);
				pst.setInt(1, orderId);
				pst.setInt(2, articleId);
				pst.setInt(3, count);

				pst.executeUpdate();

			}

			return articleId;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	// return articles to shop
	// delete article from order
	public int removeArticle(int orderId, int articleId) {

		int itemId = 0;

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select Quantity from OrderArticle where IdArticle=" + articleId;
			ResultSet rs = st.executeQuery(sql);

			if (!rs.next()) {
				return -1;
			}

			int qret = rs.getInt(1);

			sql = "select Quantity from Article where IdArticle=" + articleId;
			rs = st.executeQuery(sql);

			if (!rs.next()) {
				return -1;
			}

			int q = rs.getInt(1);

			q += qret;

			sql = "update Article set Quanity=" + q + " where IdArticle=" + articleId;

			st.executeUpdate(sql);

			// -----------------------------------------------------------------------------
			// delete from OA

			sql = "delete from OrderArticle where IdArticle=" + articleId;
			st.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	public List<Integer> getItems(int orderId) {

		List<Integer> result = new ArrayList<>();

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdArticle from OrderArticle where IdOrder=" + orderId;

			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				result.add(rs.getInt(1));
			}

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public int getLocation(int orderId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			// select order status

			String q = "select OrderState from [Order] where IdOrder=" + orderId;

			ResultSet rss = st.executeQuery(q);
			rss.next();

			String state = rss.getString(1);

			if ("created".equals(state)) {
				return -1;
			}

			String sql = "select max(IdCity) from City;";

			ResultSet rs = st.executeQuery(sql);

			rs.next();

			int maxId = rs.getInt(1);

			int[][] matrix = new int[maxId][maxId];
			int[][] t = new int[maxId][maxId];

			// init for floyd algorithm
			for (int i = 0; i < maxId; i++) {
				for (int j = 0; j < maxId; j++) {
					if (i == j) {
						matrix[i][j] = 0;
						t[i][j] = 0;
					} else {
						matrix[i][j] = 100000000;
						t[i][j] = 0;
					}
				}
			}

			sql = "select FirstCity, SecondCity, Distance from CityToCity";

			rs = st.executeQuery(sql);

			// more init
			while (rs.next()) {

				int id1 = rs.getInt(1) - 1;
				int id2 = rs.getInt(2) - 1;
				int distance = rs.getInt(3);

				matrix[id1][id2] = distance;
				t[id1][id2] = id1;
				matrix[id2][id1] = distance;
				t[id2][id1] = id2;
			}

			// floyd algorithm

			//

			for (int k = 0; k < maxId; k++) {
				for (int i = 0; i < maxId; i++) {
					for (int j = 0; j < maxId; j++) {

						if (matrix[i][j] > matrix[i][k] + matrix[k][j]) {
							matrix[i][j] = matrix[i][k] + matrix[k][j];
							t[i][j] = t[k][j];
						}

					}
				}
			}

			// finding closest city from the buyer's city

			gd150330_OrderOperations order = new gd150330_OrderOperations();

			int buyerId = order.getBuyer(orderId);

			sql = "select IdCity from Buyer where IdBuyer=" + buyerId;

			rs = st.executeQuery(sql);

			rs.next();

			int B = rs.getInt(1) - 1;

			int A = 0;

			List<Integer> shopCities = getShopCities(orderId);

			if (shopCities.isEmpty()) {
				return -1;
			}

			shopCities.remove((Integer) buyerId);

			List<Integer> shopCitiesAdjusted = new ArrayList<>();

			for (Integer c : shopCities) {
				shopCitiesAdjusted.add(c - 1);
			}

			int min = Integer.MAX_VALUE;
			for (int i = 0; i < maxId; i++) {
				if (shopCitiesAdjusted.contains(i) && matrix[B][i] < min) {
					min = matrix[B][i];
					A = i;
				}
			}

			// more operations

			gd150330_OrderOperations ord = new gd150330_OrderOperations();
			gd150330_GeneralOperations gen = new gd150330_GeneralOperations();

			int cnt = gen.getCnt();

			// Transport time to A

			shopCitiesAdjusted.remove((Integer) A);

			int transportTimeToA;
			int max = Integer.MIN_VALUE;
			for (Integer cid : shopCities) {
				if (matrix[cid][A] > max) {
					max = matrix[cid][A];
				}
			}

			transportTimeToA = max;

			int transportTimeToB = matrix[A][B] + transportTimeToA;

			findPath(t, A, B);

			int i = 0;

			int c1 = path.get(0);
			int c2 = path.get(1);
			int distance = matrix[c1][c2];

			if (cnt < transportTimeToA + distance) {
				resetPath();
				return A + 1;
			}

			if (cnt >= transportTimeToB) {
				// arrive - > trigger
				// receivedTime
				Calendar time = Calendar.getInstance();
				Calendar temp = ord.getSentTime(orderId);
				time.setTimeInMillis(temp.getTimeInMillis());
				time.add(Calendar.DAY_OF_MONTH, transportTimeToB);

				receivedTime.put(orderId, time);

				// exec time
				
				
				sql = "update [Order] set OrderState='arrived' where IdOrder=" + orderId;
				st.executeUpdate(sql);
				
				
				
				
				sql = "select IdTransaction from [Transaction] where IdOrder="+orderId;
				
				ResultSet r = st.executeQuery(sql);
				r.next();
				
				int tranid = r.getInt(1);
				
				
				sql = "update TransactionShop set Status='sent' where IdTransaction="+ tranid;
				st.executeUpdate(sql);
				
				
				java.sql.Timestamp received = new Timestamp(time.getTimeInMillis());
				sql = "update [Transaction] set ExecTime='"+received+"' where IdTransaction="+tranid;
				st.executeUpdate(sql);
				
				
				emptySystemProfitBuffer(orderId);

				resetPath();

				return B + 1;
			}

			distance += transportTimeToA;

			do {
				++i;
				c1 = path.get(i);
				c2 = path.get(i + 1);
				distance += matrix[c1][c2];

				if (cnt < distance) {
					resetPath();
					return c1 + 1;
				}
			} while (cnt > distance);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	private void emptySystemProfitBuffer(int orderId) {

		BigDecimal sysProfitForOrder = systemProfitBuffer.get(orderId);

		systemProfit = new BigDecimal(systemProfit.toString()).add(sysProfitForOrder).setScale(3);

		systemProfitBuffer.remove(orderId);

	}

	private static List<Integer> path = new ArrayList<>();

	private void resetPath() {
		path = new ArrayList<>();
	}

	private void findPath(int[][] t, int i, int j) {

		if (i == j) {
			path.add(i);
		} else {
			findPath(t, i, t[i][j]);
			path.add(j);
		}

	}

	private List<Integer> getShopCities(int orderId) {

		List<Integer> articles = new ArrayList<>();
		Set<Integer> shops = new HashSet<>();
		Set<Integer> cities = new HashSet<>();

		gd150330_OrderOperations order = new gd150330_OrderOperations();
		gd150330_BuyerOperations buyer = new gd150330_BuyerOperations();
		int buyerCity = buyer.getCity(order.getBuyer(orderId));

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdArticle from OrderArticle where IdOrder=" + orderId;

			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				articles.add(rs.getInt(1));
			}

			if (articles.isEmpty())
				return null;

			for (Integer aid : articles) {

				sql = "select IdShop from Article where IdArticle=" + aid;

				rs = st.executeQuery(sql);
				rs.next();

				shops.add(rs.getInt(1));

			}

			gd150330_ShopOperations s = new gd150330_ShopOperations();

			for (Integer sid : shops) {

				int cid = s.getCity(sid);

				if (cid != buyerCity)
					cities.add(s.getCity(sid));

			}

			return new ArrayList<>(cities);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private int makePayment(int orderId) {

		// shopId, Ammount
		Map<Integer, BigDecimal> shopAmmount = new HashMap<>();

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql;
			ResultSet rs;

			// 1

			sql = "select IdArticle,Quantity from OrderArticle where IdOrder=" + orderId;
			rs = st.executeQuery(sql);

			List<Integer> articleId = new ArrayList<>();
			List<Integer> quantity = new ArrayList<>();

			while (rs.next()) {
				articleId.add(rs.getInt(1));
				quantity.add(rs.getInt(2));
			}

			// 2

			List<Integer> price = new ArrayList<>();
			List<Integer> shop = new ArrayList<>();

			for (Integer aid : articleId) {

				sql = "select Price,IdShop from Article where IdArticle=" + aid;
				rs = st.executeQuery(sql);
				rs.next();

				int prc = rs.getInt(1);
				int sid = rs.getInt(2);

				price.add(prc);
				shop.add(sid);
			}

			double priceAccumulator = 0;

			int i = 0;
			for (Integer aid : articleId) {

				int p = price.get(i);
				int q = quantity.get(i);
				int sid = shop.get(i);

				// sys profit buffer

				// --------------------------

				sql = "select Discount from Shop where IdShop=" + sid;
				rs = st.executeQuery(sql);
				rs.next();

				double d = rs.getInt(1);
				double dsc = (100 - d) / 100;

				priceAccumulator += p * q * dsc;

				BigDecimal adder = new BigDecimal(Integer.toString(p)).multiply(new BigDecimal(Integer.toString(q)))
						.multiply(new BigDecimal(Double.toString(dsc))).setScale(3);

				// shops ammount for transactio shop
				BigDecimal temp = shopAmmount.get(sid);
				if (temp == null) {
					shopAmmount.put(sid, adder);
				} else {
					shopAmmount.put(sid, new BigDecimal(temp.toString()).add(adder)).setScale(3);
				}

				++i;
			}

			// 4

			boolean additionalDiscount = false;

			Calendar currTime = gd150330_GeneralOperations.timeStatic;
			int month = currTime.MONTH;

			int buyerId = new gd150330_OrderOperations() {
			}.getBuyer(orderId);
			
			
			sql = "select Systime from System where IdSystem="+1;
			
			rs=st.executeQuery(sql);
			rs.next();
			
			java.sql.Date  curr = rs.getDate(1);
			
			
			
			
			sql = "select Ammount from [Transaction] where IdBuyer=" + buyerId + " and" + " '" +curr+"' -StartTime <=30" ;

			rs = st.executeQuery(sql);

			if (rs.next()) {
				BigDecimal ammount = rs.getBigDecimal(1);
				ammount.setScale(3);
				BigDecimal amm2 = new BigDecimal("10000").setScale(3);
				if (ammount.compareTo(amm2) == 1) {
					additionalDiscount = true;
				}
			}

			Set<Integer> sids = shopAmmount.keySet();

			BigDecimal finalPrice;
			if (additionalDiscount) {
				priceAccumulator *= 0.98;
				finalPrice = new BigDecimal(Double.toString(priceAccumulator)).setScale(3);

				systemProfitBuffer.put(orderId,
						new BigDecimal("0.03").multiply(new BigDecimal(Double.toString(priceAccumulator))).setScale(3));

				// shops ammount

				// profit prodavnica se SMANJUJE
				for (Integer sid : sids) {
					BigDecimal tmp = shopAmmount.get(sid);
					shopAmmount.put(sid, new BigDecimal(tmp.toString()).multiply(new BigDecimal("0.97")).setScale(3));
				}

			} else {

				finalPrice = new BigDecimal(Double.toString(priceAccumulator)).setScale(3);

				systemProfitBuffer.put(orderId,
						new BigDecimal("0.05").multiply(new BigDecimal(Double.toString(priceAccumulator))).setScale(3));

				for (Integer sid : sids) {
					BigDecimal tmp = shopAmmount.get(sid);
					shopAmmount.put(sid, new BigDecimal(tmp.toString()).multiply(new BigDecimal("0.95")).setScale(3));
				}

			}

			// buyer's credit

			sql = "select Credit from Buyer where IdBuyer=" + buyerId;
			rs = st.executeQuery(sql);
			rs.next();

			BigDecimal credit = rs.getBigDecimal(1);

			if (credit.compareTo(finalPrice) == -1)
				return -1;

			BigDecimal newCredit = new BigDecimal(credit.toString()).subtract(finalPrice);

			sql = "update Buyer set credit=" + newCredit + " where IdBuyer=" + buyerId;
			st.executeUpdate(sql);

			// make transactions

			Calendar cal = gd150330_GeneralOperations.timeStatic;

			// java.util.Date date = new Date
			// (gd150330_GeneralOperations.timeStatic.getTimeInMillis());

			java.sql.Timestamp date = new Timestamp(gd150330_GeneralOperations.timeStatic.getTimeInMillis());

			
			
			sql = "insert into [Transaction] (IdBuyer,IdOrder,Ammount,ExecTime,StartTime) values ("+buyerId +","+orderId+","+finalPrice+",'"
					+date +"' , '" +date +"' )";
			st.executeUpdate(sql);
			
			sql = "select max(IdTransaction) from [Transaction]";
			rs = st.executeQuery(sql);
			rs.next();
			int transactionId = (int) rs.getInt(1);
			
			
			gd150330_GeneralOperations.executionTime.put(transactionId,cal);
			

			//sql = "SET IDENTITY_INSERT TransactionShop ON";
			//st.executeUpdate(sql);

			sql = "insert into TransactionShop (IdTransaction, IdShop,Ammount,Status) values (?,?,?,?)";

			pst = con.prepareStatement(sql);

			sids = shopAmmount.keySet();

			for (Integer sid : sids) {

				pst.setInt(1, transactionId);
				pst.setInt(2, sid);
				pst.setBigDecimal(3, shopAmmount.get(sid));
				pst.setString(4, "wait");

				pst.executeUpdate();

			}

			//sql = "SET IDENTITY_INSERT TransactionShop OFF";
			//st.executeUpdate(sql);

			return 1;

		} catch (Exception e) {
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

	public int completeOrder(int orderId) {

		// status = sent;

		// Calendar Time set sendingTime;

		// naplati i napravi transakciju

		if (completedOrder.contains(orderId)) {
			return 1;
		}

		int res = makePayment(orderId);

		if (res == -1) {
			return res;
		}

		completedOrder.add(orderId);

		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(gd150330_GeneralOperations.timeStatic.getTimeInMillis());

		sentTime.put(orderId, time);

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			// update order status

			String sql = "Update [Order] set OrderState='sent' where IdOrder=" + orderId;
			st.executeUpdate(sql);

		} catch (Exception e) {
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

	public Calendar getSentTime(int orderId) {

		Calendar time = sentTime.get(orderId);

		return time;

	}

	public BigDecimal getFinalPrice(int orderId) {

		// is order completed - status = 'sent'

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();
			ResultSet rs;

			
			String sql = "select OrderState from [Order] where IdOrder=" + orderId;

			rs = st.executeQuery(sql);
			rs.next();

			String res = rs.getString(1);

			if ("created".equals(res)) {
				return new BigDecimal(-1);
			}
			
			
			sql = "select IdBuyer from [Order] where IdOrder="+orderId;
			rs = st.executeQuery(sql);
			rs.next();
			
			int buyerId = rs.getInt(1);
			
			sql = "exec spFinalPrice " + orderId + ", " + buyerId;
			
			st.execute(sql);

			sql = "select Ammount from [Order] where IdOrder=" + orderId;

			rs = st.executeQuery(sql);

			if (rs.next()) {
				BigDecimal fprice = rs.getBigDecimal(1).setScale(3);

				return fprice;
			}
			
			return new BigDecimal(-1);
			
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
		
		return new BigDecimal(-1);
	}

	public Calendar getReceivedTime(int orderId) {

		Calendar res = receivedTime.get(orderId);

		return res;
	}

	public int getBuyer(int orderId) {

		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select IdBuyer from [Order] where IdOrder=" + orderId;

			ResultSet rs = st.executeQuery(sql);
			rs.next();

			int res = rs.getInt(1);

			return res;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	public String getState(int orderId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select OrderState from [Order] where IdOrder=" + orderId;

			ResultSet rs = st.executeQuery(sql);
			rs.next();

			String res = rs.getString(1);

			return res;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "";

	}

	public BigDecimal getDiscountSum(int orderId) {
		try {

			con = DB.getInstance().getConnection();
			st = con.createStatement();

			String sql = "select OrderState from [Order] where IdOrder=" + orderId;

			ResultSet rs = st.executeQuery(sql);
			rs.next();

			String res = rs.getString(1);

			if ("created".equals(res)) {
				return new BigDecimal("-1");
			}

			// 1

			sql = "select IdArticle,Quantity from OrderArticle where IdOrder=" + orderId;
			rs = st.executeQuery(sql);

			List<Integer> articleId = new ArrayList<>();
			List<Integer> quantity = new ArrayList<>();

			while (rs.next()) {
				articleId.add(rs.getInt(1));
				quantity.add(rs.getInt(2));
			}

			// 2

			List<Integer> price = new ArrayList<>();
			List<Integer> shop = new ArrayList<>();

			for (Integer aid : articleId) {

				sql = "select Price,IdShop from Article where IdArticle=" + aid;
				rs = st.executeQuery(sql);
				rs.next();

				int prc = rs.getInt(1);
				int sid = rs.getInt(2);

				price.add(prc);
				shop.add(sid);
			}

			// 3

			double priceAccumulator = 0;
			double priceWithoutDiscount = 0;

			int i = 0;
			for (Integer aid : articleId) {

				int p = price.get(i);
				int q = quantity.get(i);
				int sid = shop.get(i);

				sql = "select Discount from Shop where IdShop=" + sid;
				rs = st.executeQuery(sql);
				rs.next();

				double d = rs.getInt(1);

				double dsc = (100 - d) / 100;

				priceWithoutDiscount += p * q;

				priceAccumulator += p * q * dsc;

				++i;
			}

			// 4

			boolean additionalDiscount = false;

			Calendar currTime = gd150330_GeneralOperations.timeStatic;

			int month = currTime.MONTH;

			int buyerId = new gd150330_OrderOperations() {
			}.getBuyer(orderId);

			sql = "select Ammount from [Transaction] where IdBuyer=" + buyerId + " and" + " MONTH(ExecTime)=" + month;

			rs = st.executeQuery(sql);

			if (rs.next()) {
				BigDecimal ammount = rs.getBigDecimal(1);
				BigDecimal amm2 = new BigDecimal("10000");
				if (ammount.compareTo(amm2) == 1) {
					additionalDiscount = true;
				}
			}

			if (additionalDiscount) {
				priceAccumulator *= 0.98;
			}

			BigDecimal discountSum = new BigDecimal(Double.toString(priceWithoutDiscount))
					.subtract(new BigDecimal(Double.toString(priceAccumulator))).setScale(3);

			return discountSum;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new BigDecimal(-1);

	}

}// OrderDAO
