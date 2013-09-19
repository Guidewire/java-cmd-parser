package acc.common.cmdline;

/**
 * Defines a data validator.
 */
public interface IValidator {
    /**
     * Validates the specified value and returns an error message if the data is not valid.
     * If the data is valid, method should return null.
     * @param value Value to validate
     * @return Null value if data is valid, error message otherwise
     */
    String validateValue(Object value);
}
