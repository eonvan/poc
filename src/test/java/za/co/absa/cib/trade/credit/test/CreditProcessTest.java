package za.co.absa.cib.trade.credit.test;
import org.junit.Test;

import za.co.absa.cib.trade.credit.CreditApplication;
import za.co.absa.cib.trade.credit.CreditEvaluation;
import za.co.absa.cib.trade.credit.Status;

import static org.junit.Assert.*;

public class CreditProcessTest {

	@Test
	public void test() {
		CreditApplication application = new CreditApplication("Test Case 1", "Absa", 2);
		
		assertEquals(Status.INITIATED.name(), application.getStatus());
		
		CreditEvaluation evaluation = new CreditEvaluation(application);
		
		evaluation.execute();
	
		assertEquals(Status.DECLINED.name(), application.getStatus());
		
		application.setRating(3);
		evaluation.execute();
				
		assertEquals(Status.APPROVED.name(), application.getStatus());
		
		application.setBank("HSBC");
		evaluation.execute();
		
		assertEquals(Status.DECLINED.name(), application.getStatus());
		
		application.setRating(4);
		evaluation.execute();
		
		assertEquals(Status.APPROVED.name(), application.getStatus());
	}

}
