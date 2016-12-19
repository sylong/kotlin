enum class A {
    X,
    Y
}

fun getNull(): A? = null

fun getUndefined(): A? = js("void 0")

fun getX(): A? = A.X

fun box(): String {
    if (getX() == getNull()) return "fail1"
    if (getX() == getUndefined()) return "fail2"
    if (getNull() != null) return "fail3"
    if (getUndefined() != null) return "fail4"
    if (getUndefined() != getNull()) return "fail5"
    if (A.X == getUndefined()) return "fail6"
    if (A.X == getNull()) return "fail6"
    if (A.X == null) return "fail7"
    if (A.X == A.Y) return "fail8"
    if (A.X != getX()) return "fail9"

    return "OK"
}

