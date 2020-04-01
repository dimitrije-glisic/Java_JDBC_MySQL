package student;

import operations.ArticleOperations;
import student.db.dao.ArticleDAO;

public class gd150330_ArticleOperations implements ArticleOperations {

	@Override
	public int createArticle(int shopId, String articleName, int articlePrice) {
		
		ArticleDAO a = new ArticleDAO();
		
		int res = a.createArticle(shopId,articleName,articlePrice);
		
		return res;
	}

}
