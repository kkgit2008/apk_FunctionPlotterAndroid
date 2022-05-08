package com.amrhossam.functionplotter.ui.utils

import android.content.Context
import com.amrhossam.functionplotter.R
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

class ValidationHelper {


    companion object {
        @DelicateCoroutinesApi
        fun isNotEmpty(ctx: Context, expression: String, min: String, max: String): Boolean {
            when {
                min.isEmpty() -> {
                    DialogUtils.showErrorDialog(
                        ctx,
                        ctx.getString(R.string.empty_range),
                        ctx.getString(R.string.please_enter_min_value)
                    )
                    return false
                }
                max.isEmpty() -> {
                    DialogUtils.showErrorDialog(
                        ctx,
                        ctx.getString(R.string.empty_range),
                        ctx.getString(R.string.please_enter_max_value)
                    )
                    return false
                }
                expression.isEmpty() -> {
                    DialogUtils.showErrorDialog(
                        ctx,
                        ctx.getString(R.string.empty_expression),
                        ctx.getString(R.string.please_enter_expression)
                    )
                    return false

                }
                else -> return true
            }
        }

        private fun isContainX(exp: String): Boolean {
            return exp.contains("x")
        }

        private fun isBalancedBrackets(expr: String): Boolean {
            //Using Deque is faster than using stack
            //Using this method to check is expression have balanced brackets or not
            val s: Deque<Char> = ArrayDeque()
            var closedCnt = 0
            for (c in expr) {
                if (c == '(') {
                    s.push(c)
                    continue
                } else if (c == ')') ++closedCnt
                if (s.isEmpty() && closedCnt > 0) {
                    return false
                }
                if (c == ')' && s.first == '(') {
                    s.pop()
                    closedCnt--
                }
            }
            return s.isEmpty()
        }

        private fun isValidMinAndMax(min: Int, max: Int): Boolean {
            if (min >= max) return false
            return true
        }

        @DelicateCoroutinesApi
        fun validateExpression(ctx: Context, exp: String, min: Int, max: Int): Boolean {
            //Checking if it's balanced equation
            if (!isContainX(exp)) {
                DialogUtils.showErrorDialog(
                    ctx,
                    ctx.getString(R.string.wrong_expression),
                    ctx.getString(R.string.validate_function_of_x)
                )
                return false
            }
            if (!isBalancedBrackets(exp)) {
                with(DialogUtils) {
                    showErrorDialog(
                        ctx,
                        ctx.getString(R.string.wrong_expression),
                        ctx.getString(R.string.bad_math_formula)
                    )
                }
                return false
            }
            if (!isValidMinAndMax(min, max)) {
                DialogUtils.showErrorDialog(
                    ctx,
                    ctx.getString(R.string.wrong_range),
                    ctx.getString(R.string.max_value_should_be_greater)
                )
                return false
            }
            return true
        }
    }
}