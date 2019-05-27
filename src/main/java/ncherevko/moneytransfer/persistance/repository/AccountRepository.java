package ncherevko.moneytransfer.persistance.repository;

import ncherevko.moneytransfer.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountRepository {

    List<Account> getAllAccounts();

    void saveAccount(Account account);

    void updateAccount(Account account);

    void transfer(String sender, String target, BigDecimal amount);
}
