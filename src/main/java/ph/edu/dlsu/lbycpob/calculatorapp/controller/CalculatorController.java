package ph.edu.dlsu.lbycpob.calculatorapp.controller;

import javafx.scene.control.Button;
import ph.edu.dlsu.lbycpob.calculatorapp.model.CalculatorModel;
import ph.edu.dlsu.lbycpob.calculatorapp.view.CalculatorView;

/**
 * The CalculatorController class serves as the intermediary between the calculator's
 * user interface (view) and its mathematical logic (model). It follows the MVC
 * (Model-View-Controller) design pattern.
 * <p>
 * This controller handles all user interactions, processes button clicks, manages
 * input validation, and coordinates between the view and model components.
 */
public class CalculatorController {
    // Instance variables - these store the state of our calculator controller

    /**
     * The model component that handles all mathematical calculations
     */
    private CalculatorModel model;

    /**
     * The view component that manages the user interface
     */
    private CalculatorView view;

    /**
     * Stores the current mathematical expression being entered by the user
     */
    private StringBuilder currentInput;

    /**
     * Flag to track if we're waiting for a new number after an operation
     */
    private boolean waitingForOperand;

    /**
     * Constructor to create a new CalculatorController instance.
     * <p>
     * This sets up the controller with references to the model and view,
     * and initializes the input tracking variables.
     *
     * @param model The CalculatorModel that will handle mathematical operations
     * @param view  The CalculatorView that manages the user interface
     */
    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
        this.currentInput = new StringBuilder();
        this.waitingForOperand = false;
    }

    /**
     * Starts the calculator by setting up all button event handlers.
     * This method should be called after the controller is created to make
     * the calculator functional.
     */
    public void run() {
        initializeEventHandlers();
    }

    /**
     * Sets up event handlers for all calculator buttons.
     * <p>
     * This method connects each button in the view to its corresponding
     * action method. When a user clicks a button, the appropriate handler
     * method will be called.
     */
    private void initializeEventHandlers() {
        // Set up number buttons (0-9) and decimal point
        // We use an array to avoid repeating similar code for each number
        for (String num : new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."}) {
            Button button = view.getButton(num);
            // Check if the button exists before setting up the handler
            if (button != null) {
                // Lambda expression: when button is clicked, call handleNumberInput
                button.setOnAction(e -> handleNumberInput(num));
            }
        }

        // Set up basic arithmetic operation buttons (+, -, *, /)
        for (String op : new String[]{"+", "-", "*", "/", "%"}) {
            Button button = view.getButton(op);
            if (button != null) {
                // When an operation button is clicked, handle it as a basic operation
                button.setOnAction(e -> handleBasicOperation(op));
            }
        }

        // Set up scientific function buttons (sin, cos, tan, etc.)
        setupScientificButtons();

        // ==================== ADDED ====================
        // Wire the new row of four "instant apply" buttons. Per the assignment note
        // the way sin/cos/etc. do.
        setupImmediateButton("1/x", "reciprocal");
        setupImmediateButton("x²", "sqr");
        setupImmediateButton("EXP", "exp");
        setupImmediateButton("|x|", "abs");
        // =================================================

        Button equalsButton = view.getButton("=");
        if (equalsButton != null) {
            equalsButton.setOnAction(e -> handleEquals());
        }

        Button negativeButton = view.getButton("+/-");
        if (negativeButton != null) {
            negativeButton.setOnAction(e -> handleNegativeButton());
        }

        Button piButton = view.getButton("π");
        if (piButton != null) {
            piButton.setOnAction(e -> handlePiButton());
        }

        Button clearButton = view.getButton("C");
        if (clearButton != null) {
            clearButton.setOnAction(e -> handleClear());
        }

        Button shiftButton = view.getButton("shft");
        if (shiftButton != null) {
            shiftButton.setOnAction(e -> handleShift());
        }

        Button clearEntryButton = view.getButton("CE");
        if (clearEntryButton != null) {
            clearEntryButton.setOnAction(e -> handleClearEntry());
        }

        Button leftParenButton = view.getButton("(");
        if (leftParenButton != null) {
            leftParenButton.setOnAction(e -> handleInput("("));
        }

        Button rightParenButton = view.getButton(")");
        if (rightParenButton != null) {
            rightParenButton.setOnAction(e -> handleInput(")"));
        }
    }

    private void handleShift() {
        model.calculator.toggleShift();
        if (model.calculator.isShiftActive()) {
            view.getButton("shft").setStyle("-fx-text-fill: black;");
        } else {
            view.getButton("shft").setStyle("-fx-text-fill: white;");
        }
    }

    private void handlePiButton() {
        if (model.isResultDisplayed()) {
            currentInput.setLength(0);
            model.setResultDisplayed(false);
        }

        if (waitingForOperand) {
            waitingForOperand = false;
        }

        currentInput.append(model.calculator.PI);
        updateDisplay();
    }

    private void setupScientificButtons() {
        setupScientificButton("sin", "sin");
        setupScientificButton("cos", "cos");
        setupScientificButton("tan", "tan");
        setupScientificButton("ln", "ln");
        setupScientificButton("log", "log");
        setupScientificButton("√", "sqrt");
        setupScientificButton("!", "factorial");
        setupScientificButton("^", "^");
    }

    private void setupScientificButton(String buttonLabel, String operation) {
        Button button = view.getButton(buttonLabel);
        if (button != null) {
            if (operation.equals("^")) {
                button.setOnAction(e -> handleInput("^"));
            } else {
                button.setOnAction(e -> handleScientificOperation(operation));
            }
        }
    }

    /**
     * ==================== ADDED ====================
     * Wires a button that applies a scientific operation immediately to the
     * currently displayed/entered value and shows the result right away
     */
    private void setupImmediateButton(String buttonLabel, String operation) {
        Button button = view.getButton(buttonLabel);
        if (button != null) {
            button.setOnAction(e -> handleImmediateOperation(operation));
        }
    }

    private void handleImmediateOperation(String operation) {
        try {
            double value;
            if (model.isResultDisplayed() || currentInput.isEmpty()) {
                value = model.getCurrentValue();
            } else {
                value = Double.parseDouble(currentInput.toString());
            }
            double result = model.performScientificOperation(operation, value);
            displayResult(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            view.updateDisplay("Error");
            model.setResultDisplayed(true);
        }
    }
    // =================================================

    private void handleNumberInput(String number) {
        if (model.isResultDisplayed()) {
            currentInput.setLength(0);
            model.setResultDisplayed(false);
        }

        if (waitingForOperand) {
            waitingForOperand = false;
        }

        if (number.equals(".")) {
            String currentToken = getCurrentToken();
            if (currentToken.contains(".")) return;
        }

        currentInput.append(number);
        updateDisplay();
    }

    private void handleInput(String input) {
        if (model.isResultDisplayed() && !input.matches("[+\\-*/^()]")) {
            currentInput.setLength(0);
            model.setResultDisplayed(false);
        }

        currentInput.append(input);
        updateDisplay();
    }

    private void handleBasicOperation(String operation) {
        if (model.isResultDisplayed()) {
            String result = String.valueOf(model.getCurrentValue());
            currentInput.setLength(0);
            currentInput.append(result);
            model.setResultDisplayed(false);
        }

        currentInput.append(operation);
        waitingForOperand = true;

        updateDisplay();
    }

    private void handleScientificOperation(String functionName) {
        if (model.isResultDisplayed()) {
            currentInput.setLength(0);
            model.setResultDisplayed(false);
        }

        if (!model.calculator.isShiftActive()) {
            currentInput.append(functionName);
        } else {
            currentInput.append("a" + functionName);
        }
        currentInput.append("(");

        updateDisplay();
    }

    private void handleNegativeButton() {
        if (model.isResultDisplayed()) {
            double currentValue = model.getCurrentValue();
            double negatedValue = -currentValue;

            model.setCurrentValue(negatedValue);
            String resultText = model.formatResult(negatedValue);
            view.updateDisplay(resultText);
            model.setDisplayText(resultText);

            currentInput.setLength(0);
            currentInput.append(negatedValue);
            return;
        }

        String currentToken = getCurrentToken();
        String inputStr = currentInput.toString();

        if (currentInput.isEmpty() || waitingForOperand ||
                inputStr.endsWith("+") || inputStr.endsWith("-") ||
                inputStr.endsWith("*") || inputStr.endsWith("/") ||
                inputStr.endsWith("^") || inputStr.endsWith("(")) {

            currentInput.append("-");
            waitingForOperand = false;
        } else if (!currentToken.isEmpty()) {
            int tokenStart = inputStr.lastIndexOf(currentToken);

            if (tokenStart > 0 && inputStr.charAt(tokenStart - 1) == '-') {

                boolean isNegativeSign = (tokenStart - 2 < 0)
                        || isOperatorChar(inputStr.charAt(tokenStart - 2))
                        || inputStr.charAt(tokenStart - 2) == '(';

                if (isNegativeSign) {
                    // The '-' is already a negative sign remove it to make positive
                    currentInput.deleteCharAt(tokenStart - 1);
                } else {
                    // The '-' is subtraction insert a NEW '-' to negate
                    // instead of deleting the operator
                    currentInput.insert(tokenStart, "-");
                }
            } else {
                currentInput.insert(tokenStart, "-");
            }
        } else if (inputStr.endsWith("-") && inputStr.length() == 1) {
            currentInput.setLength(0);
        }
        updateDisplay();
    }

    private boolean isOperatorChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private void handleEquals() {
        try {
            System.out.println("Input expression: '" + currentInput + "'");
            if (!currentInput.isEmpty()) {
                String expression = currentInput.toString();

                String postfix = (model.calculator).infixToPostfix(currentInput.toString());
                System.out.println("Postfix: '" + postfix + "'");

                double result = (model.calculator).evaluateExpression(expression);
                System.out.println("Result: " + model.formatResult(result));
                displayResult(result);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            view.updateDisplay("Error");
            model.setResultDisplayed(true);
        }
    }

    private void handleClear() {
        model.clear();
        currentInput.setLength(0);
        waitingForOperand = false;
        view.updateDisplay("0");
    }

    private void handleClearEntry() {
        currentInput.setLength(0);
        view.updateDisplay("0");
        waitingForOperand = false;
    }

    private void displayResult(double result) {
        model.setCurrentValue(result);
        String resultText = model.formatResult(result);
        view.updateDisplay(resultText);
        model.setDisplayText(resultText);
        model.setResultDisplayed(true);

        currentInput.setLength(0);
        currentInput.append(result);
    }

    private void updateDisplay() {
        String displayText = !currentInput.isEmpty() ? currentInput.toString() : "0";
        view.updateDisplay(displayText);
        model.setDisplayText(displayText);
    }


    private String getCurrentToken() {
        String input = currentInput.toString();
        String[] tokens = input.split("[+\\-*/%^()]");
        return tokens.length > 0 ? tokens[tokens.length - 1] : "";
    }
}

