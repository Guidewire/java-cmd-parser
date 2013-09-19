package acc.common.cmdline;

/**
 * Implements a double validator.
 */
public class DoubleValidator implements IValidator {
    /**
     * Minimum value (inclusive).
     */
    private double _min;

    /**
     * Maximum value (inclusive).
     */
    private double _max;

    /**
     * Constructor of the class.
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     */
    public DoubleValidator(double min, double max) {
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
        double testedValue = (Double)value;
        if (testedValue < this._min || testedValue > this._max) {
            return String.format("The value must be between %1.3f and %1.3f", this._min, this._max);
        }

        // Success, error message is null
        return null;
    }
}
