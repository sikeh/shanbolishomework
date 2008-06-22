package tslab.exception;

/**
 * User: Shanbo Li
 * Date: Jun 22, 2008
 * Time: 11:14:55 PM
 *
 * @author Shanbo Li
 */
public class ValidationFailedException extends Exception{
    public ValidationFailedException() {
    }

    public ValidationFailedException(String message) {
        super(message);
    }

    public ValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationFailedException(Throwable cause) {
        super(cause);
    }
}
