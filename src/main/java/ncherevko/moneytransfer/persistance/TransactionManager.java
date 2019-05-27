package ncherevko.moneytransfer.persistance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

public class TransactionManager implements SessionFactory {

    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);

    private static final ThreadLocal<Session> SESSION = new ThreadLocal<>();

    @Override
    public Session getSession() {
        return Optional.ofNullable(SESSION.get()).orElseGet(Session::openSession);
    }

    public void doInTransaction(TransactionCallback callback) throws SQLException {
        Session session = getSession();
        SESSION.set(session);
        try {
            session.beginTransaction();
            callback.doInTransaction();
            session.commit();
        } catch (Exception e) {
            try {
                session.rollback();
            } catch (SQLException sqlException) {
                log.error("Unable to rollback transaction", sqlException);
            }

            throw e;
        } finally {
            try {
                session.close();
            } catch (SQLException e) {
                log.error("Failed to close session", e);
            }
            SESSION.remove();
        }
    }

}
