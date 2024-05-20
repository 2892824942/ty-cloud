package ty.framework.sms.test.customer.exception;

/**
 * 框架异常父类 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 15:21 
 */
public class CustomerException extends RuntimeException {

    protected String code;

    protected String message;

    public CustomerException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CustomerException() {
    }

    public CustomerException(String message) {
        super(message);
        this.message = message;
    }

    public CustomerException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public CustomerException(Throwable cause) {
        super(cause);
        if (cause != null && cause.getMessage() != null) {
            this.message = cause.getMessage();
        }
    }

    public CustomerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }
}