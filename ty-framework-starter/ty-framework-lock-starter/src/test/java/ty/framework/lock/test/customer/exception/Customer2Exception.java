package ty.framework.lock.test.customer.exception;

/**
 * 框架异常父类
 *
 * @author xuchenglong
 * @createTime 2019-08-14 15:21
 */
public class Customer2Exception extends RuntimeException {

    protected String code;

    protected String message;

    public Customer2Exception(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public Customer2Exception() {
    }

    public Customer2Exception(String message) {
        super(message);
        this.message = message;
    }

    public Customer2Exception(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public Customer2Exception(Throwable cause) {
        super(cause);
        if (cause != null && cause.getMessage() != null) {
            this.message = cause.getMessage();
        }
    }

    public Customer2Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }
}