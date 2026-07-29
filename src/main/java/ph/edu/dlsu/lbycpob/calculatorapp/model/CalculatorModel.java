package ph.edu.dlsu.lbycpob.calculatorapp.model;

/**
 * CalculatorModel is the data/business-logic layer (the "M" in MVC).
 * Manages calculator state and delegates math to a Calculator implementation.
 */
public class CalculatorModel {

    private static final double EPSILON = 1e-12;

    private double currentValue;
    private String displayText;
    private boolean isResultDisplayed;

    // ==================== ADDED ====================
    // Memory register backing the new MC / MR / M+ / M- buttons.
    private double memoryValue;
    // =================================================

    public ScientificCalculator calculator;

    public CalculatorModel() {
        this.calculator = new ScientificCalculator();
        this.currentValue = 0.0;
        this.displayText = "0";
        this.isResultDisplayed = false;
        this.memoryValue = 0.0; // ADDED: memory starts empty
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public String getDisplayText() {
        return displayText;
    }

    public boolean isResultDisplayed() {
        return isResultDisplayed;
    }

    public void setCurrentValue(double value) {
        this.currentValue = value;
    }

    public void setDisplayText(String text) {
        this.displayText = text;
    }

    public void setResultDisplayed(boolean displayed) {
        this.isResultDisplayed = displayed;
    }

    public double performOperation(String operation, double operand1, double operand2) {
        return calculator.performOperation(operation, operand1, operand2);
    }

    public double performScientificOperation(String operation, double operand) {
        if (calculator instanceof ScientificCalculator) {
            return calculator.performScientificOperation(operation, operand);
        }
        return operand;
    }

    public double evaluateExpression(String expression) {
        return roundIfCloseToZero(calculator.evaluateExpression(expression));
    }

    public void clear() {
        this.currentValue = 0.0;
        this.displayText = "0";
        this.isResultDisplayed = false;
    }

    // ==================== ADDED ====================
    // Memory register operations backing MC / MR / M+ / M-.

    /**
     * Clears the memory register back to zero (MC button).
     */
    public void memoryClear() {
        this.memoryValue = 0.0;
    }

    /**
     * Returns whatever is currently stored in the memory register (MR button).
     *
     * @return the stored memory value
     */
    public double memoryRecall() {
        return this.memoryValue;
    }

    /**
     * Adds the given value to the memory register (M+ button).
     *
     * @param value the value to add to memory
     */
    public void memoryAdd(double value) {
        this.memoryValue += value;
    }

    /**
     * Subtracts the given value from the memory register (M- button).
     *
     * @param value the value to subtract from memory
     */
    public void memorySubtract(double value) {
        this.memoryValue -= value;
    }

    /**
     * Reports whether the memory register currently holds a non-zero value,
     * useful if the view ever wants to show an "M" indicator.
     *
     * @return true if memory is non-zero
     */
    public boolean hasMemoryValue() {
        return this.memoryValue != 0.0;
    }
    // =================================================

    private double roundIfCloseToZero(double value) {
        return Math.abs(value) < EPSILON ? 0.0 : value;
    }

    public String formatResult(double result) {
        if (!Double.isFinite(result)) {
            return Double.toString(result);
        }

        double absResult = Math.abs(result);
        if (absResult >= 1e10 || (absResult > 0 && absResult < 1e-4)) {
            return String.format("%.10e", result)
                    .replaceAll("\\.?0*e", "e")
                    .replaceAll("e([+-])0+(\\d)", "e$1$2")
                    .replaceAll("e([+-])0+$", "e$10");
        }

        if (result == Math.floor(result)) {
            return String.format("%.0f", result);
        } else {
            return String.format("%.10g", result).replaceAll("\\.?0*$", "");
        }
    }
}
