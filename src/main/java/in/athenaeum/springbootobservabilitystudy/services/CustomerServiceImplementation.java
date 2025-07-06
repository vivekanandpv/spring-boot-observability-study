package in.athenaeum.springbootobservabilitystudy.services;

import in.athenaeum.springbootobservabilitystudy.exceptions.RecordNotFoundException;
import in.athenaeum.springbootobservabilitystudy.models.Customer;
import in.athenaeum.springbootobservabilitystudy.repositories.CustomerRepository;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerCreateViewModel;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerUpdateViewModel;
import in.athenaeum.springbootobservabilitystudy.viewmodels.CustomerViewModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImplementation implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImplementation(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerViewModel> getAll() {
        return customerRepository
                .findAll()
                .stream()
                .map(this::toViewModel)
                .toList();
    }

    @Override
    public CustomerViewModel getById(int customerId) throws RecordNotFoundException {
        return toViewModel(getEntityById(customerId));
    }

    @Override
    public CustomerViewModel create(CustomerCreateViewModel viewModel) {
        return toViewModel(customerRepository.saveAndFlush(toEntity(viewModel)));
    }

    @Override
    public CustomerViewModel update(int customerId, CustomerUpdateViewModel viewModel) throws RecordNotFoundException {
        Customer entityDb = getEntityById(customerId);
        BeanUtils.copyProperties(viewModel, entityDb);
        
        return toViewModel(customerRepository.saveAndFlush(entityDb));
    }

    @Override
    public void deleteById(int customerId) throws RecordNotFoundException {
        customerRepository.delete(getEntityById(customerId));
    }
    
    private CustomerViewModel toViewModel(Customer entity) {
        CustomerViewModel viewModel = new CustomerViewModel();
        BeanUtils.copyProperties(entity, viewModel);
        return viewModel;
    }
    
    private Customer toEntity(CustomerCreateViewModel viewModel) {
        Customer entity = new Customer();
        BeanUtils.copyProperties(viewModel, entity);
        return entity;
    }
    
    private Customer getEntityById(int customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RecordNotFoundException(String.format("Could not find the customer with id: %d", customerId)));
    }
}
