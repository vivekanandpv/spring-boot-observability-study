package in.athenaeum.springbootobservabilitystudy.apis;

import in.athenaeum.springbootobservabilitystudy.exceptions.RecordNotFoundException;
import in.athenaeum.springbootobservabilitystudy.services.CustomerService;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerCreateViewModel;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerUpdateViewModel;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerApi {
    private final CustomerService customerService;
    
    //  Loggers are not per-instance properties. They are related to the class itself.
    //  If it is not static, each object would have its own logger instance unnecessarily. This wastes memory.
    //  Loggers typically don’t hold any instance-specific state. They just delegate log messages to the logging framework.
    
    //  Slf4j is a facade for logging (Simple Logging Facade for Java)
    //  The real logger in the case of Spring Boot is logback, which can be replaced with Log4j v2
    //  Also read: https://www.ncsc.gov.uk/information/log4j-vulnerability-what-everyone-needs-to-know
    //  And this: https://logging.apache.org/log4j/2.12.x/index.html 
    private static final Logger logger = LoggerFactory.getLogger(CustomerApi.class);

    public CustomerApi(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @GetMapping
    public ResponseEntity<List<CustomerViewModel>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }
    
    @GetMapping("{customerId}")
    public ResponseEntity<CustomerViewModel> getById(@PathVariable int customerId) {
        return ResponseEntity.ok(customerService.getById(customerId));
    }
    
    @PostMapping
    public ResponseEntity<CustomerViewModel> create(@RequestBody CustomerCreateViewModel viewModel) {
        return ResponseEntity.ok(customerService.create(viewModel));
    }
    
    @PutMapping("{customerID}")
    public ResponseEntity<CustomerViewModel> update(@PathVariable int customerID, @RequestBody CustomerUpdateViewModel viewModel) {
        return ResponseEntity.ok(customerService.update(customerID, viewModel));
    }
    
    @DeleteMapping("{customerID}")
    public ResponseEntity<Void> deleteById(@PathVariable int customerID) {
        customerService.deleteById(customerID);
        return ResponseEntity.noContent().build();
    }
    
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRecordNotFoundException(RecordNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }
}
