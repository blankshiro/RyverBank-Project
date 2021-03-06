package com.cs203t5.ryverbank.customer;

import java.util.*;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

import com.cs203t5.ryverbank.account_transaction.*;
import com.cs203t5.ryverbank.portfolio.Portfolio;
import com.cs203t5.ryverbank.trading.Trade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.*;

/**
 * Customer class for managing customers of the bank application. Each user can
 * have either the role of USER, ANALYST or MANAGER. Every customer have have
 * many accounts and trades but only one portfolio.
 */
@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long customerId;

    @NotNull(message = "Username should not be null")
    @Size(min = 5, max = 20, message = "Username should be between 5 and 20 characters")
    @Column(name = "username", unique = true)
    private String username;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "nric", unique = true)
    private String nric;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "address")
    private String address;

    @NotNull(message = "Authorities should not be null")
    private String authorities;

    @NotNull(message = "Active should not be null")
    private Boolean active = null;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Account> accounts;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Portfolio portfolio;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trade> trades;

    /**
     * Constructs a new customer with the following parameters.
     * 
     * @param username    The username of the customer.
     * @param password    The password of the customer.
     * @param full_name   The full name of the customer.
     * @param nric        The nric of the customer.
     * @param phone       The phone number of the customer.
     * @param address     The address of the customer.
     * @param authorities The role of the customer.
     * @param active      The active status of the account.
     */
    public Customer(String username, String password, String full_name, String nric, String phone, String address,
            String authorities, boolean active) {
        this.full_name = full_name;
        this.nric = nric;
        this.phone = phone;
        this.address = address;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.active = active;
    }

    /**
     * Return a collection of authorities (roles) granted to the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(authorities));
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot
     * be authenticated.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     */
    @Override
    public boolean isEnabled() {
        boolean actives = active.booleanValue();
        return actives;
    }

    /**
     * Validates Singapore NRIC / FIN in 2 stages: 1) Ensure first letter starts
     * with S, T, F or G and the last letter is A-Z 2) Calculate weight of digits in
     * between first and last character of the input string, determine what the last
     * letter should be, then match it against the last character of the input
     * string.
     * 
     * Credit to: https://gist.github.com/squeeish/65cc82b0acaea3f551eac6e7885dc9c5
     *
     * @param nric NRIC / FIN string to be validated
     * @return true if NRIC/FIN passes, false otherwise
     */
    public boolean validateNric(String nric) {
        String nricToTest = nric.toUpperCase();

        // first letter must start with S, T, F or G. Last letter must be A - Z
        if (!Pattern.compile("^[STFG]\\d{7}[A-Z]$").matcher(nricToTest).matches()) {
            return false;
        } else {
            char[] icArray = new char[9];
            char[] st = "JZIHGFEDCBA".toCharArray();
            char[] fg = "XWUTRQPNMLK".toCharArray();

            for (int i = 0; i < 9; i++) {
                icArray[i] = nricToTest.charAt(i);
            }

            // calculate weight of positions 1 to 7
            int weight = (Integer.parseInt(String.valueOf(icArray[1]), 10)) * 2
                    + (Integer.parseInt(String.valueOf(icArray[2]), 10)) * 7
                    + (Integer.parseInt(String.valueOf(icArray[3]), 10)) * 6
                    + (Integer.parseInt(String.valueOf(icArray[4]), 10)) * 5
                    + (Integer.parseInt(String.valueOf(icArray[5]), 10)) * 4
                    + (Integer.parseInt(String.valueOf(icArray[6]), 10)) * 3
                    + (Integer.parseInt(String.valueOf(icArray[7]), 10)) * 2;

            int offset = icArray[0] == 'T' || icArray[0] == 'G' ? 4 : 0;

            int lastCharPosition = (offset + weight) % 11;

            if (icArray[0] == 'S' || icArray[0] == 'T') {
                return icArray[8] == st[lastCharPosition];
            } else if (icArray[0] == 'F' || icArray[0] == 'G') {
                return icArray[8] == fg[lastCharPosition];
            } else {
                return false; // this line should never reached due to regex above
            }
        }
    }

    /**
     * A method to validate whether the user's phone is valid or not. A valid phone
     * number will begin with 6/8/9 and has a total of 8 digits.
     * 
     * @param phone The phone number of the user.
     * @return True if the phone number is valid, otherwise return false.
     */
    public boolean validatePhone(String phone) {
        // validate phone numbers of format "1234567890"
        if (phone.matches("^[6|8|9]\\d{7}$"))
            return true;
        // return false if nothing matches the input
        else {
            return false;
        }
    }
}
