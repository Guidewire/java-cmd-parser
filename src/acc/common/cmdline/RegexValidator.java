package acc.common.cmdline;

/**
 * Implements a regex validator.
 */
public class RegexValidator implements IValidator {
    /**
     * Regex expression used for validation.
     */
    private String _regex;

    /**
     * Constructor of the class.
     * @param regex Regex expression to validate against
     */
    public RegexValidator(String regex) {
        this._regex = regex;
    }

    /**
     * Validates whether a specified value is a string matching a given regex expression.
     * @param value Value to validate
     * @return Error message when validation failed, null otherwise
     */
    @Override
    public String validateValue(Object value) {
        String testedValue = (String)value;
        if (testedValue == null) {
            return "The value cannot be null";
        }
        if (!testedValue.matches(this._regex)) {
            return "The value doesn't match the required regex expression: " + this._regex;
        }

        // Success, error message is null
        return null;
    }
}
