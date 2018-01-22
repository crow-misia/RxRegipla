package app.util

private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

fun ByteArray.toHexString(): String {
    val result = CharArray(size * 2)
    var c = 0
    for (b in this) {
        result[c++] = hexDigits[b.toInt() shr 4 and 0xf]
        result[c++] = hexDigits[b.toInt() and 0xf]
    }
    return String(result)
}

