package com.banking.account.service;

import com.banking.account.dto.CreateAccountRequest;
import com.banking.account.entity.Account;
import com.banking.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final Random random = new Random();

    public Account createAccount(CreateAccountRequest request) {
        log.info("Creating account for user ID: {}", request.getUserId());

        // Generate unique account number
        String accountNumber = generateAccountNumber();

        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setStatus("ACTIVE");

        Account saved = accountRepository.save(account);
        log.info("Account created successfully: {}", saved.getAccountNumber());

        return saved;
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
    }

    public Account updateAccountStatus(Long accountId, String status) {
        log.info("Updating account {} status to {}", accountId, status);

        Account account = getAccountById(accountId);
        account.setStatus(status);

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            // Generate ACC + 10 digit number
            long number = 1000000000L + (long) (random.nextDouble() * 9000000000L);
            accountNumber = "ACC" + number;
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
