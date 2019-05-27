package ncherevko.moneytransfer.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Session implements AutoCloseable {

    private static final String HOST = "jdbc:h2:mem:sa;DB_CLOSE_DELAY=-1";
    private Connection connection;
    private boolean pendingTransaction = false;

    private Session(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
    }

    public static Session openSession() {
        try {
            return new Session(HOST);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create session", e);
        }
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
        pendingTransaction = true;
    }

    public void commit() throws SQLException {
        connection.commit();
        pendingTransaction = false;
    }

    public void rollback() throws SQLException {
        connection.rollback();
        pendingTransaction = false;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !pendingTransaction) {
            if (connection.isClosed()) {
                throw new IllegalStateException("Connection is already closed");
            } else {
                connection.close();
            }
        }
    }
}
