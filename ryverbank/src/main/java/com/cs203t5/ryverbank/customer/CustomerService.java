package com.cs203t5.ryverbank.customer;

import java.util.List;

// import com.cs203t5.ryverbank.token.*;

public interface CustomerService {
    List<Customer> listUsers();

    Customer getUser(Long userId, String authenticatedUsername, String authenticatedUserRole);

    Customer addUser(Customer user);

    Customer updateUser(Long userId, Customer user, String authenticatedUsername, String authenticatedUserRole);

    // Customer deactiveUser(Long userId);

    Customer createCustomer(Customer user);

    // void sendEmail(String toUser, String token);
}
