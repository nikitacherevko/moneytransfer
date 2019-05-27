package ncherevko.moneytransfer.persistance;

public class PersistanceManager {

    public SessionFactory getSessionFactory() {
        return new TransactionManager();
    }

    public TransactionManager getTransactionManager() {
        return new TransactionManager();
    }
}
