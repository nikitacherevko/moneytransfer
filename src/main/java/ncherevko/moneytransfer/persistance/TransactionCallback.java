package ncherevko.moneytransfer.persistance;

import java.sql.SQLException;

@FunctionalInterface
public interface TransactionCallback {
    void doInTransaction() throws SQLException;
}
