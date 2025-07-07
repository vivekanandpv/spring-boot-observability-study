package in.athenaeum.observabilityupstreamservice.services;

import in.athenaeum.observabilityupstreamservice.exceptions.RecordNotFoundException;
import in.athenaeum.observabilityupstreamservice.models.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CustomerServiceImplementation implements CustomerService {
    private final RestTemplate restTemplate;
    private final String downstreamUrl;

    public CustomerServiceImplementation(
            RestTemplate restTemplate, 
            @Value("${downstream.url}") String downstreamUrl
    ) {
        this.restTemplate = restTemplate;
        this.downstreamUrl = String.format("%s/api/v1/customers", downstreamUrl);
    }

    @Override
    public List<Customer> getAll() {
        return restTemplate.exchange(
                downstreamUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Customer>>() {}
        ).getBody();
    }

    @Override
    public Customer getById(int customerId) throws RecordNotFoundException {
        try {
            return restTemplate.getForObject(String.format("%s/%d", downstreamUrl, customerId), Customer.class);
        } catch (HttpClientErrorException e) {
            throw new RecordNotFoundException(String.format("Could not find customer with id: %d", customerId));
        }
    }
}
