package ph.edu.dlsu.lbycpob.calculatorapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The CalculatorView class represents the user interface (View) component of the calculator
 * following the MVC (Model-View-Controller) design pattern.
 *
 * This class is responsible for:
 * - Creating and organizing all visual components (buttons, display, layout)
 * - Managing the appearance and styling of the calculator
 * - Providing access to UI components for the controller
 * - Updating the display when calculations are performed
 *
 * The view does NOT handle any business logic or calculations - it only manages
 * the visual presentation and provides interfaces for user interaction.
 *
 */

public class CalculatorView {

    private static final double BUTTON_HEIGHT = 46;
    private static final double ROW_GAP = 6;

    private final VBox root;
    private final TextField display;
    private final Map<String, Button> buttons = new LinkedHashMap<>();

    public CalculatorView() {
        root = new VBox(8);
        root.setPadding(new Insets(12, 15, 12, 15));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("calculator-body");

        Label topLabel = new Label("LBYCPOB - E2x+ Modified by Group 10");
        topLabel.getStyleClass().addAll("top-label", "branding-label");

        display = new TextField("0");
        display.setEditable(false);
        display.getStyleClass().add("display");
        display.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = buildButtonGrid();

        root.getChildren().addAll(topLabel, display, grid);
    }

    private GridPane buildButtonGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(ROW_GAP);
        grid.setVgap(ROW_GAP);

        // Row order matches the reference layout exactly.

        String[][] rowLabels = {
                {"(", ")", "C", "CE"},
                // ==================== ADDED ROW ====================
                // Memory functions: MC (memory clear), MR (memory recall),
                // M+ (memory add), M- (memory subtract).
                {"MC", "MR", "M+", "M-"},
                // =====================================================
                {"shft", "sin", "cos", "tan"},
                {"π", "!", "√", "%"},
                {"+/-", "ln", "log", "^"},
                // ==================== ADDED ROW ====================
                // 1/x (reciprocal), x^2 (square), EXP (e^x), |x| (absolute value)
                {"1/x", "x²", "EXP", "|x|"},
                // =====================================================
                // ==================== ADDED ROW ====================
                // ∛x (cube root), x³ (cube), e (Euler's number), Ans (last answer)
                {"∛x", "x³", "e", "Ans"},
                // =====================================================
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"},
        };

        String[][] rowStyles = {
                {"function-button", "function-button", "function-button", "function-button"},
                // ==================== ADDED ====================
                {"function-button", "function-button", "function-button", "function-button"},
                // =================================================
                {"function-button", "scientific-button", "scientific-button", "scientific-button"},
                {"function-button", "scientific-button", "scientific-button", "operator-button"},
                {"function-button", "scientific-button", "scientific-button", "operator-button"},
                {"scientific-button", "scientific-button", "scientific-button", "scientific-button"},
                // ==================== ADDED ====================
                {"scientific-button", "scientific-button", "function-button", "function-button"},
                // =================================================
                {"number-button", "number-button", "number-button", "operator-button"},
                {"number-button", "number-button", "number-button", "operator-button"},
                {"number-button", "number-button", "number-button", "operator-button"},
                {"number-button", "number-button", "equals-button", "operator-button"},
        };


        for (int r = 0; r < rowLabels.length; r++) {
            addRow(grid, r, rowLabels[r], rowStyles[r]);

            // Fixed row height keeps the grid compact regardless of window size,
            // instead of letting rows stretch to fill leftover space.
            RowConstraints rc = new RowConstraints(BUTTON_HEIGHT);
            grid.getRowConstraints().add(rc);
        }

        for (int c = 0; c < 4; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }

        return grid;
    }

    private void addRow(GridPane grid, int rowIndex, String[] labels, String[] styleClasses) {
        for (int col = 0; col < labels.length; col++) {
            String label = labels[col];
            Button button = new Button(label);
            button.getStyleClass().add(styleClasses[col]);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setPrefHeight(BUTTON_HEIGHT);
            grid.add(button, col, rowIndex);
            buttons.put(label, button);
        }
    }

    /**
     * Returns the button registered under the given label, or null if no such
     * button exists in the view. The Controller always null-checks before use.
     */
    public Button getButton(String label) {
        return buttons.get(label);
    }

    public TextField getDisplay() {
        return display;
    }

    public VBox getRoot() {
        return root;
    }

    public void updateDisplay(String text) {
        display.setText(text);
    }
}