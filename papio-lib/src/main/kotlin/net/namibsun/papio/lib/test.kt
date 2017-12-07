package net.namibsun.papio.lib

abstract class Base

class A: Base() { fun a(){} }
class B: Base() { fun b(){} }

fun getx(x: Boolean): Base {
    return if (x) {
        A()
    } else {
        B()
    }
}

fun work() {
    val x = getx(true) as B
    x.b()
}