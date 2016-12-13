// MODULE: lib
// FILE: lib.kt

interface I {
    fun foo(): String
}

abstract class A {
    abstract fun bar(): String
}

abstract class G<T> {
    abstract fun baz(): T
}

class C {
    private val propA = object : A() {
        override fun bar() = "propA.bar"

        fun x() = "OK"
    }

    private val propI = object : I {
        override fun foo() = "propI.foo"

        fun x() = "OK"
    }

    private val propAI = object : A(), I {
        override fun foo() = "propAI.foo"

        override fun bar() = "propAI.bar"

        fun x() = "OK"
    }

    private val propG = object : G<String>() {
        override fun baz() = "propG.baz"

        fun x() = "OK"
    }
}

// MODULE: main(lib)
// FILE: main.kt
fun test() {
    println(C.<!UNRESOLVED_REFERENCE!>propA<!>.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!>())
    println(C.<!UNRESOLVED_REFERENCE!>propI<!>.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!>())
    println(C.<!UNRESOLVED_REFERENCE!>propAI<!>.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!>())
    println(C.<!UNRESOLVED_REFERENCE!>propG<!>.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!>())
}