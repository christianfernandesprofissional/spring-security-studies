package com.security.config.securityconfig;

import com.security.model.Customer;
import com.security.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EazyBankUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public EazyBankUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User details not  found for the user: " + username));
        return new User(customer.getEmail(), customer.getPwd(), List.of(new SimpleGrantedAuthority(customer.getRole())));

    }
}
