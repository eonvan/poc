package za.co.absa.cib.trade.credit.config;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import za.co.absa.cib.trade.credit.absa.AbsaCreditCheck;
import za.co.absa.cib.trade.credit.absa.AbsaCreditCheckTransformer;
import za.co.absa.cib.trade.credit.hsbc.HsbcCreditCheck;
import za.co.absa.cib.trade.credit.hsbc.HsbcCreditCheckTransformer;

@Configuration
public class IntegrationConfig {

	@Bean(name = "absaCreditCheck")
	public AbsaCreditCheck produceAbsaCreditCheck() {
		return new AbsaCreditCheck();
	}

	@Bean(name = "absaCreditCheckTransformer")
	public AbsaCreditCheckTransformer produceAbsaCreditCheckTransformer() {
		return new AbsaCreditCheckTransformer();
	}

	@Bean(name = "hsbcCreditCheck")
	public HsbcCreditCheck produceHsbcCreditCheck() {
		return new HsbcCreditCheck();
	}

	@Bean(name = "hsbcCreditCheckTransformer")
	public HsbcCreditCheckTransformer produceHsbcCreditCheckTransformer() {
		return new HsbcCreditCheckTransformer();
	}
	
	@Bean(name = "defaultCreditCheck")
	public DefaultCreditCheck produceDefaultCreditCheck() {
		return new DefaultCreditCheck();
	}
	

	@Bean
	RouteBuilder produceCreditCheckRoute() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("direct:start").choice().when(body().method("getBank").isEqualToIgnoreCase("Absa"))
						.transform().method("absaCreditCheckTransformer", "transform").to("absaCreditCheck")
						.when(body().method("bank").isEqualToIgnoreCase("HSBC")).transform()
						.method("hsbcCreditCheckTransformer", "transform").to("hsbcCreditCheck")
						.otherwise().to("defaultCreditCheck");
			}

		};
	}
}
