package ncherevko.moneytransfer.persistance.exception;

public class QueryExecutionException extends RuntimeException {
    public QueryExecutionException(String message) {
        super(message);
    }

    public QueryExecutionException(Throwable cause) {
        super(cause);
    }

    public QueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
