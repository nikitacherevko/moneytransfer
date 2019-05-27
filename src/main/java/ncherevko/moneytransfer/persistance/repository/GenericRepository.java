package ncherevko.moneytransfer.persistance.repository;

import ncherevko.moneytransfer.persistance.PersistanceManager;
import ncherevko.moneytransfer.persistance.Session;
import ncherevko.moneytransfer.persistance.SessionFactory;
import ncherevko.moneytransfer.persistance.TransactionManager;

import java.sql.SQLException;
import java.util.Optional;

public abstract class GenericRepository<T> {

    private final SessionFactory sessionFactory;
    protected final TransactionManager transactionManager;

    public GenericRepository(PersistanceManager persistanceManager) {
        this.sessionFactory = persistanceManager.getSessionFactory();
        this.transactionManager = persistanceManager.getTransactionManager();

        this.createTable();
    }

    public Session getSession() throws SQLException {
        return sessionFactory.getSession();
    }

    public abstract Optional<T> getByName(String name);

    public abstract void createTable();

    public abstract String getTableName();
}
