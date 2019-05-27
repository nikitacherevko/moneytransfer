package ncherevko.moneytransfer.persistance;

/**
 * Used to provide session for interacting with DB.
 */
public interface SessionFactory {
    Session getSession();
}
