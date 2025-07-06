package in.athenaeum.springbootobservabilitystudy.repositories;

import in.athenaeum.springbootobservabilitystudy.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
