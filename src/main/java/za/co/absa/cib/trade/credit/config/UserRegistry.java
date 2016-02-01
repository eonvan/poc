package za.co.absa.cib.trade.credit.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("userRegistry")
@Scope("singleton")
public class UserRegistry {

	private String context = "ABSA";

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	
}
