package acc.common.cmdline;

/**
 * Implements a non-empty validator.
 */
public class NonEmptyValidator implements IValidator {
    /**
     * Validates whether the specified value is a non-empty string.
     * @param value Value to validate
     * @return Error message if a value is empty, null otherwise
     */
    @Override
    public String validateValue(Object value) {
        String testedValue = (String)value;
        if (testedValue == null || testedValue.trim().length() == 0) {
            return "The value must not be empty";
        }

        // Success, error message is null
        return null;
    }
}
