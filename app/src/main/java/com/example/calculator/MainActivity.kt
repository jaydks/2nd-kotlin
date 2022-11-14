package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.calculator.databinding.ActivityMainBinding
import kotlin.math.exp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isOperator = false // 가장 마지막에 연산자 입력한 경우
    private var hasOperator = false // expression에 연산자 존재
    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.main_tv_expression)
    }
    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.main_tv_result)
    }
    lateinit var expressionList: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.mainTvExpression.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.mainTvExpression.text.isNullOrBlank()) {
                    binding.mainBtnBackOff.visibility = View.VISIBLE
                    binding.mainBtnBackOn.visibility = View.GONE
                } else {
                    binding.mainBtnBackOff.visibility = View.GONE
                    binding.mainBtnBackOn.visibility = View.VISIBLE
                }
            }

        })

    }

    // 버튼 클릭
    fun btnClicked(v: View) {
        when (v.id) {
            R.id.num0 -> numClicked("0")
            R.id.num1 -> numClicked("1")
            R.id.num2 -> numClicked("2")
            R.id.num3 -> numClicked("3")
            R.id.num4 -> numClicked("4")
            R.id.num5 -> numClicked("5")
            R.id.num6 -> numClicked("6")
            R.id.num7 -> numClicked("7")
            R.id.num8 -> numClicked("8")
            R.id.num9 -> numClicked("9")

            R.id.btn_plus -> operationClicked("+")
            R.id.btn_minus -> operationClicked("-")
            R.id.btn_multi -> operationClicked("x")
            R.id.btn_div -> operationClicked("÷")
            R.id.btn_percent -> operationClicked("%")

            R.id.btn_cancel -> clearClicked(v)
            R.id.btn_result -> resultClicked(v)
            R.id.btn_dot -> numClicked(".")

            R.id.main_btn_back_on -> backNum(v)

        }
    }

    // 버튼 클릭 시
    private fun numClicked(number: String) {

        // 연산자가 마지막
        if (isOperator) {
            expressionTextView.append("")
            isOperator = false
        }

        if (expressionTextView.text.contains("+")) {
            expressionList = expressionTextView.text.split("+")
        } else if (expressionTextView.text.contains("-")) {
            expressionList = expressionTextView.text.split("+")
        } else if (expressionTextView.text.contains("x")) {
            expressionList = expressionTextView.text.split("x")
        } else if (expressionTextView.text.contains("÷")) {
            expressionList = expressionTextView.text.split("÷")
        } else if (expressionTextView.text.contains("%")) {
            expressionList = expressionTextView.text.split("%")
        } else {
            expressionList = listOf(expressionTextView.text.toString())
        }

        if (expressionList.isNotEmpty() && expressionList.last().length >= 15) {
            Toast.makeText(this, "15자리까지 입력할 수 있어요", Toast.LENGTH_SHORT).show()
        } else if (expressionList.last() == "0") {   // 첫 글자가 0
            expressionTextView.text = ""
        }
        expressionTextView.append(number)
        resultTextView.text = calculateExpression()
    }

    // 계산
    private fun calculateExpression(): String {

        val expressionTexts: List<String>
        var op: String

        if (expressionTextView.text.contains("+")) {
            expressionTexts = expressionTextView.text.split("+")
            op = "+"
        } else if (expressionTextView.text.contains("-")) {
            expressionTexts = expressionTextView.text.split("-")
            op = "-"
        } else if (expressionTextView.text.contains("x")) {
            expressionTexts = expressionTextView.text.split("x")
            op = "x"
        } else if (expressionTextView.text.contains("÷")) {
            expressionTexts = expressionTextView.text.split("÷")
            op = "÷"
        } else if (expressionTextView.text.contains("%")) {
            expressionTexts = expressionTextView.text.split("%")
            op = "%"
        } else {
            expressionTexts = expressionTextView.text.split("")
            op = ""
        }

        if (hasOperator.not()) {
            return ""
        } else if ((expressionTexts[0].isNumber().not() || expressionTexts[1].isNumber()
                .not()) && op != "%"
        ) {
            return ""
        }
        val exp1 = expressionTexts[0].toInt()
        val exp2 = expressionTexts[1].toInt()

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "x" -> (exp1 * exp2).toString()
            "%" -> {
                (exp1.toDouble() / 100).toString()
            }
            "÷" -> (exp1.toDouble() / exp2.toDouble()).toString()
            else -> ""
        }
    }

    // 연산자 클릭
    private fun operationClicked(operator: String) {
        if (expressionTextView.text.isNullOrEmpty()) {
            Toast.makeText(this, "완성되지 않은 수식입니다", Toast.LENGTH_SHORT).show()
            expressionTextView.text = ""
        }

        when {
            isOperator || hasOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(0) + operator
            }

            else -> {
                expressionTextView.append("$operator")
            }

        }
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1, expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        expressionTextView.text = ssb
        isOperator = true
        hasOperator = true
    }

    // 결과
    fun resultClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")
        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }
        if (hasOperator) {
            Toast.makeText(this, "수식을 완성해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()

            return
        }

        val resultText = calculateExpression()

        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false

    }

    // clear
    fun clearClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    // back
    fun backNum(v: View) {
        expressionTextView.text =
            expressionTextView.text.substring(0, expressionTextView.text.length - 1)
        resultTextView.text = calculateExpression()
    }

    //
    private fun String.isNumber(): Boolean {
        return try {
            this.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}
