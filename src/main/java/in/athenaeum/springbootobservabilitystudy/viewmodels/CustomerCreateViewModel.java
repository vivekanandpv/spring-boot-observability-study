package in.athenaeum.springbootobservabilitystudy.viewmodels;

import in.athenaeum.springbootobservabilitystudy.config.Sensitive;

public class CustomerCreateViewModel {
    private String firstName;
    private String lastName;
    
    @Sensitive
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
