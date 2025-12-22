package com.security.controller;

import com.security.model.Customer;
import com.security.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        try{
            String hashPwd = passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashPwd);
            Customer newCustomer = customerRepository.save(customer);

            if(newCustomer.getId() > 0){
                return ResponseEntity.status(HttpStatus.CREATED).body("Given user details are successfully registered");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed");
            }

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An Exception occured " + e.getMessage());
        }
    }

}
