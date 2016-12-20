external interface I

external object O : I


class Delegate {
    operator fun getValue(thisRef: Any?, property: Any): String = ""
}

external class A : <!EXTERNAL_DELEGATION!>I by O<!> {
    val prop <!EXTERNAL_DELEGATION!>by Delegate()<!>
}