package in.athenaeum.observabilityupstreamservice.services;


import in.athenaeum.observabilityupstreamservice.exceptions.RecordNotFoundException;
import in.athenaeum.observabilityupstreamservice.models.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> getAll();
    Customer getById(int customerId) throws RecordNotFoundException;
}
