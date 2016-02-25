package za.co.absa.cib.trade.credit;

import java.util.Collections;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.apache.camel.Produce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import za.co.absa.cib.trade.credit.config.UserRegistry;

@RestController
public class CreditRestController {

	private static final Logger logger = 
			LoggerFactory.getLogger(CreditRestController.class);
	
	@Autowired
	private RuntimeService runtimeService;

//	@Autowired
//	private CamelContext camelContext;
	
	@Autowired
	private CreditApplicationRepository repository;
	
	@Autowired
	private UserRegistry userRegistry;

	@Produce(uri = "direct:start")
	private CreditIntegrationService service;
	

	@RequestMapping(value="/start-credit-process", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus( value = HttpStatus.OK )
	public @ResponseBody CreditApplication submitCreditApplication(@RequestBody Map<String, String> data) throws Exception {
		CreditApplication application = new CreditApplication(data.get("applicantName"), data.get("bank"), Integer.parseInt(data.get("rating")));
	
		userRegistry.setContext(application.getBank());
		
		Boolean approved = service.send(application);
		
		if (approved) {
			application.setStatus(Status.APPROVED.name());
			repository.save(application);
		} else {
			application.setStatus(Status.PENDING.name());
			
			//start a task: taskid = creditApproval
			repository.save(application);
			Map<String, Object> vars = Collections.<String, Object>singletonMap("application", application);
		    String procId = runtimeService.startProcessInstanceByKey("creditApproval", vars).getId();
			
			logger.info(String.format("Started process %s", procId));
		}		
		
		return application;
		
	}

	@RequestMapping(value="/application/{id}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus( value = HttpStatus.OK )
	public @ResponseBody CreditApplication readApplication(@PathVariable Long id) {
		
		logger.info(String.format("Retrieving result credit application for %s", id));
		
		CreditApplication result = repository.findOne(id);
		
		logger.info(String.format("Found application %s", result.getApplicantName()));
		
		return result;
	}
}
