package com.example.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resultTv, solutionTv;
    MaterialButton buttonC, buttonBrackOpen, buttonBrackClose;
    MaterialButton buttonDivide, buttonMultiply, buttonPlus, buttonMinus, buttonEquals;
    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    MaterialButton buttonAC, buttonDot, buttonsin, buttoncos, buttontan, buttonradiansdegree;

    // Variable para almacenar la función trigonométrica seleccionada
    String trigFunction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        assignId(buttonC, R.id.button_c);
        assignId(buttonBrackOpen, R.id.button_open_bracket);
        assignId(buttonBrackClose, R.id.button_close_bracket);
        assignId(buttonDivide, R.id.button_division);
        assignId(buttonMultiply, R.id.button_multiply);
        assignId(buttonPlus, R.id.button_add);
        assignId(buttonMinus, R.id.button_subtract);
        assignId(buttonEquals, R.id.button_equal);
        assignId(button0, R.id.button_0);
        assignId(button1, R.id.button_1);
        assignId(button2, R.id.button_2);
        assignId(button3, R.id.button_3);
        assignId(button4, R.id.button_4);
        assignId(button5, R.id.button_5);
        assignId(button6, R.id.button_6);
        assignId(button7, R.id.button_7);
        assignId(button8, R.id.button_8);
        assignId(button9, R.id.button_9);
        assignId(buttonAC, R.id.button_ac);
        assignId(buttonDot, R.id.button_decimal);
        assignId(buttonsin, R.id.button_sin);
        assignId(buttoncos, R.id.button_cos);
        assignId(buttontan, R.id.button_tan);
        assignId(buttonradiansdegree, R.id.button_radiansdegrees);
    }

    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionTv.getText().toString();

        // Limpiar al hacer clic en "AC"
        if (buttonText.equals("ac")) {
            solutionTv.setText("");
            resultTv.setText("");
            trigFunction = "";
            return;
        }

        // Evitar concatenar sobre "0" inicial
        if (dataToCalculate.equals("0")) {
            dataToCalculate = "";  // Reemplaza el "0" por vacío
        }

        // Lógica del botón "="
        if (buttonText.equals("=")) {
            if (!trigFunction.isEmpty()) {
                String result = applyTrigFunction(dataToCalculate, trigFunction);
                resultTv.setText(result);
                solutionTv.setText(result);
                trigFunction = "";
            } else {
                String finalResult = getResult(dataToCalculate);
                if (!finalResult.equals("Err")) {
                    resultTv.setText(finalResult);
                    solutionTv.setText(finalResult);
                }
            }
            return;
        }

        // Lógica del botón "C"
        if (buttonText.equals("C")) {
            if (dataToCalculate.length() > 0) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            }
        } else if (buttonText.equals("sin") || buttonText.equals("cos") || buttonText.equals("tan")) {
            trigFunction = buttonText;
            dataToCalculate += buttonText + "(";
        } else {
            dataToCalculate += buttonText;
        }

        solutionTv.setText(dataToCalculate);

        // Calcular si no hay función trigonométrica seleccionada
        if (trigFunction.isEmpty()) {
            String finalResult = getResult(dataToCalculate);
            if (!finalResult.equals("Err")) {
                resultTv.setText(finalResult);
            }
        }
    }

    String applyTrigFunction(String data, String trigFunction) {
        try {
            // Extraer el número de la expresión
            String numberStr = data.replace(trigFunction + "(", "").replace(")", "");
            double value = Double.parseDouble(numberStr);
            double result = 0;

            // Calcular según la función trigonométrica seleccionada
            if (trigFunction.equals("sin")) {
                result = Math.sin(Math.toRadians(value));
            } else if (trigFunction.equals("cos")) {
                result = Math.cos(Math.toRadians(value));
            } else if (trigFunction.equals("tan")) {
                result = Math.tan(Math.toRadians(value));
            }

            // Retornar el resultado como cadena
            return String.valueOf(result);

        } catch (NumberFormatException e) {
            return "Err";
        }
    }

    String getResult(String data) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();

            if (finalResult.contains(".")) {
                double result = Double.parseDouble(finalResult);
                if (result == (int) result) {
                    return String.valueOf((int) result);
                }
            }
            return finalResult;

        } catch (Exception e) {
            return "Err";
        } finally {
            Context.exit();
        }
    }
}
