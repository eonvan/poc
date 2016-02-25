package za.co.absa.cib.trade.credit.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import za.co.absa.cib.trade.credit.Application;
import za.co.absa.cib.trade.credit.CreditApplication;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
@EnableJpaRepositories(basePackageClasses = CreditApplication.class,
entityManagerFactoryRef = "entityManagerFactory")
public class DatasourceConfig {

	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean defaultEntityManagerFactory(
	        EntityManagerFactoryBuilder builder) {
	    return builder
	            .dataSource(testDataSource())
	            .packages(CreditApplication.class)
	            .persistenceUnit("trade")
	            .properties(jpaProperties())
	            .build();
	}
	
	public Map<String, String> jpaProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.format_sql", "true");
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		
		return properties;
	}
	
	@Bean
	public DataSource defaultDataSource() {
	    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
	        dataSourceBuilder.url("jdbc:postgresql:prototype");
	        dataSourceBuilder.username("hsbc");
	        dataSourceBuilder.password("hsbc123");
	        return dataSourceBuilder.build();   
	}
	
	@Bean
	public DataSource testDataSource() {
	    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:postgresql:postgres");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("jack123");
        return dataSourceBuilder.build();   
	}
	
	@Bean(name="dataSources")
	public Map<String, DataSource> targetDataSources() {
		Map<String, DataSource> result = new HashMap<String, DataSource>();
		result.put("HSBC", defaultDataSource());
		result.put("ABSA", testDataSource());
		return result;
	}
	
	@Bean
	@Primary
	public AbstractRoutingDataSource tenantDatasource() {
		return new AbstractRoutingDataSource() {
			
			@Autowired
			private UserRegistry userRegistry;
			
			@Override
			protected Object determineCurrentLookupKey() {
				return userRegistry.getContext();
			}
			
		    @Resource(name="dataSources")
		    public void setTargetDataSources(Map dataSources) {
		        super.setTargetDataSources(dataSources);	
		    }
			
		};
	}
	
}
