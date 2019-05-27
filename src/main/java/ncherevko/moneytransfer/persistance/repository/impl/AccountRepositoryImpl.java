package ncherevko.moneytransfer.persistance.repository.impl;

import ncherevko.moneytransfer.model.Account;
import ncherevko.moneytransfer.persistance.PersistanceManager;
import ncherevko.moneytransfer.persistance.Session;
import ncherevko.moneytransfer.persistance.exception.QueryExecutionException;
import ncherevko.moneytransfer.persistance.repository.AccountRepository;
import ncherevko.moneytransfer.persistance.repository.GenericRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl extends GenericRepository<Account> implements AccountRepository {

    private static final Logger log = LoggerFactory.getLogger(AccountRepositoryImpl.class);

    private static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public AccountRepositoryImpl(PersistanceManager persistanceManager) {
        super(persistanceManager);
    }

    @Override
    public void createTable() {
        try (Session session = getSession()) {
            try (Statement statement = session.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS " + getTableName() + " (\n"
                        + "  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,\n"
                        + "  `name` VARCHAR(100) NOT NULL,\n"
                        + "  `balance` DECIMAL(13,2) NOT NULL,\n"
                        + "  PRIMARY KEY (`id`),\n"
                        + "  UNIQUE INDEX `name_UNIQUE` (`name` ASC))");
            }
        } catch (SQLException e) {
            log.error("Failed to create account table", e);
        }
    }

    @Override
    public String getTableName() {
        return "account";
    }

    @Override
    public Optional<Account> getByName(String name) {
        try (Session session = getSession()) {
            try (Statement statement = session.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + getTableName() + " AS ac "
                        + " WHERE ac.name = '" + name + "'");
                List<Account> accounts = mapToAccounts(resultSet);
                return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0));
            }
        } catch (SQLException e) {
            log.error("Failed to find account", e);
            throw new QueryExecutionException(e);
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        try (Session session = getSession()) {
            try (Statement statement = session.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + getTableName());
                return mapToAccounts(resultSet);
            }
        } catch (SQLException e) {
            log.error("Failed to get all accounts", e);
            throw new QueryExecutionException(e);
        }
    }

    @Override
    public void updateAccount(Account account) {
        try (Session session = getSession()) {
            try (Statement statement = session.createStatement()) {
                statement.executeUpdate(String.format("UPDATE %s "
                                + " SET balance = %s"
                                + " WHERE name = '%s'",
                        getTableName(), rounded(account.getBalance()), account.getName()));
            }

            log.info("Account updated {}", account);
        } catch (SQLException e) {
            log.error("Failed to update account", e);
            throw new QueryExecutionException(e);
        }
    }

    @Override
    public void saveAccount(Account account) {
        try (Session session = getSession()) {
            try (Statement statement = session.createStatement()) {
                statement.executeUpdate(String.format(
                        "INSERT INTO %s (`name`, `balance`) VALUES ('%s', %s)",
                        getTableName(), account.getName(), rounded(account.getBalance())));
            }

            log.info("Added new account {}", account);
        } catch (SQLException e) {
            log.error("Failed to add new account", e);
            throw new QueryExecutionException(e);
        }
    }

    @Override
    public void transfer(String sender, String target, BigDecimal amount) {
        try {
            transactionManager.doInTransaction(() -> {
                Account source = getByName(sender)
                        .orElseThrow(() ->
                                new IllegalStateException(String.format("Sender's account %s wasn't found", sender)));
                if (source.getBalance().compareTo(amount) < 0) {
                    throw new IllegalStateException("Not enough money on the balance");
                }

                Account destination = getByName(target)
                        .orElseThrow(() ->
                                new IllegalStateException(String.format("Target account %s wasn't found", sender)));

                source.setBalance(source.getBalance().subtract(amount));
                updateAccount(source);

                destination.setBalance(destination.getBalance().add(amount));
                updateAccount(destination);
            });
        } catch (SQLException ex) {
            log.error("Failed to transfer money", ex);
        }
    }

    private List<Account> mapToAccounts(ResultSet resultSet) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        while (resultSet.next()) {
            Account account = new Account();
            account.setName(resultSet.getString("name"));
            account.setBalance(rounded(resultSet.getBigDecimal("balance")));
            accounts.add(account);
        }

        return accounts;
    }

    private BigDecimal rounded(BigDecimal number) {
        return number.setScale(2, ROUNDING_MODE);
    }

}
