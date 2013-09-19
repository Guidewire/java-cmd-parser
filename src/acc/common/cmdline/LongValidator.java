package acc.common.cmdline;

/**
 * Implements a long validator.
 */
public class LongValidator implements IValidator {
    /**
     * Minimum value (inclusive).
     */
    private long _min;

    /**
     * Maximum value (inclusive).
     */
    private long _max;

    /**
     * Constructor of the class.
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     */
    public LongValidator(long min, long max) {
        this._min = min;
        this._max = max;
    }

    /**
     * Validates whether the specified value is between the given range.
     * @param value Value to validate
     * @return Error message if the value is incorrect, null otherwise
     */
    @Override
    public String validateValue(Object value) {
        long testedValue = Long.valueOf(value.toString());
        if (testedValue < this._min || testedValue > this._max) {
            return String.format("The value must be between %d and %d", this._min, this._max);
        }

        // Success, error message is null
        return null;
    }
}
