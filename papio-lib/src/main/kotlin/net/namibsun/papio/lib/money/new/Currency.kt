package net.namibsun.papio.lib.money.new

/**
 * Enum that models Fiat (e.g. real-world) currencies like Euro or the US Dollar.
 */
enum class Fiat(centSize: Int) {
    EUR(2),
    USD(2)
}

fun x(c: Fiat) {
    c.name
}

/**
 * Enum that models various crypto-currencies
 */
enum class Crypto {
    BTC
}

/**
 * Wrapper around a currency enum
 */
interface BaseCurrency

/**
 * Wrapper around a Fiat currency enum
 * @param currency: The Enum value for this currency
 */
class FiatCurrency(val currency: Fiat) : BaseCurrency

/**
 * Wrapper around a crypto-currency enum
 * @param currency: The Enum value for this currency
 */
class CryptoCurrency(val currency: Crypto) : BaseCurrency