package ncherevko.moneytransfer.service;

import ncherevko.moneytransfer.model.Account;
import ncherevko.moneytransfer.persistance.PersistanceManager;
import ncherevko.moneytransfer.persistance.repository.AccountRepository;
import ncherevko.moneytransfer.persistance.repository.impl.AccountRepositoryImpl;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private final AccountRepository accountRepository = new AccountRepositoryImpl(new PersistanceManager());

    public List<Account> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }

    public void addAccount(Account account) {
        accountRepository.saveAccount(account);
    }

    public void transfer(String sender, String receiver, BigDecimal amount) {
        accountRepository.transfer(sender, receiver, amount);
    }
}
