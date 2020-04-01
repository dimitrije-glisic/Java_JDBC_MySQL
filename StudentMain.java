import operations.*;
import student.gd150330_ArticleOperations;
import student.gd150330_BuyerOperations;
import student.gd150330_CityOperations;
import student.gd150330_GeneralOperations;
import student.gd150330_OrderOperations;
import student.gd150330_ShopOperations;
import student.gd150330_TransactionOperations;

import org.junit.Test;
import tests.TestHandler;
import tests.TestRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class StudentMain {

	public static void main(String[] args) {
		
		// Change this for your implementation
		// (points will be negative if
		// interfaces are not implemented).

		ArticleOperations articleOperations = new gd150330_ArticleOperations();
		BuyerOperations buyerOperations = new gd150330_BuyerOperations();
		CityOperations cityOperations = new gd150330_CityOperations();
		GeneralOperations generalOperations = new gd150330_GeneralOperations();
		OrderOperations orderOperations = new gd150330_OrderOperations();
		ShopOperations shopOperations = new gd150330_ShopOperations();
		TransactionOperations transactionOperations = new gd150330_TransactionOperations();

		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2010, Calendar.JANUARY, 01);

		Calendar c2 = Calendar.getInstance();
		c2.clear();
		c2.set(2010, Calendar.JANUARY, 01);
		if (c.equals(c2))
			System.out.println("jednako");
		else
			System.out.println("nije jednako");

		TestHandler.createInstance(articleOperations, buyerOperations, cityOperations, generalOperations,
				orderOperations, shopOperations, transactionOperations);

		TestRunner.runTests();

		// ----------------------------------

//		gd150330_GeneralOperations general = new gd150330_GeneralOperations();
//		general.eraseAll();
//
//		gd150330_CityOperations city = new gd150330_CityOperations();
//
//		city.createCity("Beograd");
//
//		gd150330_BuyerOperations buyer = new gd150330_BuyerOperations();
//
//		buyer.createBuyer("Dimitrije", 1);
//
//		BigDecimal increase = new BigDecimal("1000.000");
//
//		BigDecimal increased = buyer.increaseCredit(1, increase);
//
//		System.out.println(increased);

	}
}
