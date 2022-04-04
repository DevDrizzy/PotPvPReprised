package net.frozenorb.potpvp.kt.util

object NumberUtils {

    fun isInteger(s: String): Boolean {
        val radix = 10
        var result = 0
        var i = 0
        val len = s.length
        var limit = -2147483647

        if (len > 0) {
            val firstChar = s[0]
            if (firstChar < '0') {
                if (firstChar == '-') {
                    limit = Integer.MIN_VALUE
                } else if (firstChar != '+') {
                    return false
                }
                if (len == 1) {
                    return false
                }
                ++i
            }

            val multmin = limit / radix
            while (i < len) {
                val digit = Character.digit(s[i++], radix)
                if (digit < 0) {
                    return false
                }
                if (result < multmin) {
                    return false
                }
                result *= radix
                if (result < limit + digit) {
                    return false
                }
                result -= digit
            }

            return true
        }

        return false
    }

    fun isShort(input: String): Boolean {
        if (!isInteger(input)) {
            return false
        }

        val value = Integer.parseInt(input)
        return value > -32768 && value < 32767
    }

}