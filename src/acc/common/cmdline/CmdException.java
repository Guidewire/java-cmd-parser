package acc.common.cmdline;

/**
 * The class {@code CmdException} indicates the error in command line arguments.
 */
public class CmdException extends Exception {
    /**
     * Additional error code.
     */
    private CmdExceptionCode _code;

    /**
     * Additional information about argument which caused the exception to be thrown.
     */
    private String _invalidArgument;

    /**
     * Constructor of the class.
     * @param code Additional error code
     * @param message Exception message
     */
    public CmdException(CmdExceptionCode code, String message) {
        this(code, null, message);
    }

    /**
     * Constructor of the class.
     * @param code Additional error code
     * @param invalidArgument Argument name which caused the exception
     * @param message Exception message
     */
    public CmdException(CmdExceptionCode code, String invalidArgument, String message) {
        super(message);
        this._code = code;
        this._invalidArgument = invalidArgument;
    }

    /**
     * Constructor of the class.
     * @param code Additional error code
     * @param message Exception message
     * @param innerException Inner exception information
     */
    public CmdException(CmdExceptionCode code, String message, Throwable innerException) {
        this(code, null, message, innerException);
    }

    /**
     * Constructor of the class.
     * @param code Additional error code
     * @param invalidArgument Argument name which caused the exception
     * @param message Exception message
     * @param innerException Inner exception information
     */
    public CmdException(CmdExceptionCode code, String invalidArgument, String message, Throwable innerException) {
        super(message, innerException);
        this._code = code;
        this._invalidArgument = invalidArgument;
    }

    /**
     * Returns additional error code.
     * @return Additional error code
     */
    public CmdExceptionCode getErrorCode() {
        return this._code;
    }

    /**
     * Returns argument name which caused the exception.
     * @return Argument name which caused the exception
     */
    public String getInvalidArgument() {
        return this._invalidArgument;
    }
}
