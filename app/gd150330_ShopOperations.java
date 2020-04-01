package student;

import java.util.List;

import operations.ShopOperations;
import student.db.dao.ShopDAO;

public class gd150330_ShopOperations implements ShopOperations{

	@Override
	public int createShop(String name, String cityName) {
		
		ShopDAO s = new ShopDAO();
		
		return s.createShop(name,cityName);
		
	}

	@Override
	public int setCity(int shopId, String cityName) {
		
		ShopDAO s = new ShopDAO();
		
		return s.setCity(shopId,cityName);
		
		
	}

	@Override
	public int getCity(int shopId) {
		ShopDAO s = new ShopDAO();

		return s.getCity(shopId);
	}

	@Override
	public int setDiscount(int shopId, int discountPercentage) {		
		ShopDAO s = new ShopDAO();
		
		return s.setDiscount(shopId,discountPercentage);
		
	}

	@Override
	public int increaseArticleCount(int articleId, int increment) {

		ShopDAO s = new ShopDAO();
		
		int res = s.increaseArticleCount(articleId,increment);
		
		return res;
	}

	@Override
	public int getArticleCount(int articleId) {
		ShopDAO s = new ShopDAO();
		
		int res = s.getArticleCount(articleId);
		
		return res;
	}

	@Override
	public List<Integer> getArticles(int shopId) {
		ShopDAO s = new ShopDAO();
		
		List<Integer> res = s.getArticles(shopId);
		
		return res;
	}

	@Override
	public int getDiscount(int shopId) {
		ShopDAO s = new ShopDAO();
		
		return s.getDiscount(shopId);
		
	}

	
	
}
