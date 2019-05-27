package ncherevko.moneytransfer.persistance.exception;

public class TransferFailedException extends Exception {
    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferFailedException(Throwable cause) {
        super(cause);
    }
}
