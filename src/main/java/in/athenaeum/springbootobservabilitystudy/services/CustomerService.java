package in.athenaeum.springbootobservabilitystudy.services;

import in.athenaeum.springbootobservabilitystudy.exceptions.RecordNotFoundException;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerCreateViewModel;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerUpdateViewModel;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerViewModel;

import java.util.List;

public interface CustomerService {
    List<CustomerViewModel> getAll();
    CustomerViewModel getById(int customerId) throws RecordNotFoundException;
    CustomerViewModel create(CustomerCreateViewModel viewModel);
    CustomerViewModel update(int customerId, CustomerUpdateViewModel viewModel) throws RecordNotFoundException;
    void deleteById(int customerId) throws RecordNotFoundException;
}
