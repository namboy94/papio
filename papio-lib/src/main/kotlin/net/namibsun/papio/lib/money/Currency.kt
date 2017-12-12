/*
This file is part of papio.

papio is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

papio is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with papio.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.namibsun.papio.lib.money

/**
 * Enum that models Fiat (e.g. real-world) currencies like Euro or the US Dollar
 * as well as crypto-currencies like Bitcoin and Ethereum
 * @param currencyType: The type of currency of a currency. For example, FIAT for Euro or CRYPTO for Bitcoin
 * @param displayAccuracy: The accuracy with which to generally display the currency's values.
 *                         This specifies how many digits after the decimal point will be displayed
 * @param symbol: The currency's symbol, for example € for Euro
 */
enum class Currency(val currencyType: CurrencyType, val displayAccuracy: Int, val symbol: String) {
    EUR(CurrencyType.FIAT, 2, "€"), // Euro
    USD(CurrencyType.FIAT, 2, "US$"), // US Dollar
    ZAR(CurrencyType.FIAT, 2, "R"), // South African Rand
    NAD(CurrencyType.FIAT, 2, "N$"), // Namibian Dollar
    JPY(CurrencyType.FIAT, 3, "¥"), // Japanese Yen
    BGN(CurrencyType.FIAT, 2, "лв"), // Bulgarian Lew
    CZK(CurrencyType.FIAT, 2, "Kč"), // Czech Koruna
    DKK(CurrencyType.FIAT, 2, "dkr."), // Danish Krona
    GBP(CurrencyType.FIAT, 2, "£"), // Great Britain Pound
    HUF(CurrencyType.FIAT, 2, "Ft"), // Hungarian Forint
    PLN(CurrencyType.FIAT, 2, "zł"), // Polish złoty
    RON(CurrencyType.FIAT, 2, "leu"), // Romanian leu
    SEK(CurrencyType.FIAT, 2, "kr"), // Swedish Krona
    CHF(CurrencyType.FIAT, 2, "Fr."), // Swiss Franks
    NOK(CurrencyType.FIAT, 2, "kr"), // Norwegian Krone
    HRK(CurrencyType.FIAT, 2, "kn"), // Croatian kuna
    RUB(CurrencyType.FIAT, 2, "\u20BD"), // Russian Ruble
    TRY(CurrencyType.FIAT, 2, "₺"), // Turkish Lira
    AUD(CurrencyType.FIAT, 2, "A$"), // Australian Dollar
    BRL(CurrencyType.FIAT, 2, "R$"), // Brazilian Real
    CAD(CurrencyType.FIAT, 2, "C$"), // Canadian Dollar
    CNY(CurrencyType.FIAT, 2, "¥"), // Chinese Renminbi/Yuan
    HKD(CurrencyType.FIAT, 2, "HK$"), // Hongkong Dollar
    IDR(CurrencyType.FIAT, 2, "Rp"), // Indonesian Rupiah
    ILS(CurrencyType.FIAT, 2, "₪"), // Israeli Shekel
    INR(CurrencyType.FIAT, 2, "₹"), // Indian Rupee
    KRW(CurrencyType.FIAT, 2, "₩"), // South Korean Won
    MXN(CurrencyType.FIAT, 2, "Mex$"), // Mexican Peso
    MYR(CurrencyType.FIAT, 2, "RM"), // Malaysian ringgit
    NZD(CurrencyType.FIAT, 2, "NZ$"), // New Zealand Dollar
    PHP(CurrencyType.FIAT, 2, "₱"), // Philippine Peso
    SGD(CurrencyType.FIAT, 2, "S$"), // Singapore Dollar
    THB(CurrencyType.FIAT, 2, "฿"), // Thai baht

    BTC(CurrencyType.CRYPTO, 8, "BTC"), // Bitcoin
    BCH(CurrencyType.CRYPTO, 8, "BCH"), // Bitcoin Cash
    BCN(CurrencyType.CRYPTO, 8, "BCN"), // Bytecoin
    DASH(CurrencyType.CRYPTO, 8, "DASH"), // Dash
    DOGE(CurrencyType.CRYPTO, 8, "DOGE"), // Doge
    ETH(CurrencyType.CRYPTO, 8, "ETH"), // Ethereum
    ETC(CurrencyType.CRYPTO, 8, "ETC"), // Ethereum Classic
    LTC(CurrencyType.CRYPTO, 8, "LTC"), // Litecoin
    XMR(CurrencyType.CRYPTO, 8, "XMR"), // Monero
    PPC(CurrencyType.CRYPTO, 8, "PPC"), // Peercoin
    XRP(CurrencyType.CRYPTO, 8, "XRP"), // Ripple
    MLN(CurrencyType.CRYPTO, 8, "MLN"), // Meloncoin
    EOS(CurrencyType.CRYPTO, 8, "EOS"), // EOS
    ZEC(CurrencyType.CRYPTO, 8, "ZEC"); // ZCash

    /**
     * Helper methods for retrieving different types of currencies
     */
    companion object {

        /**
         * @return All real-world (Fiat) currencies
         */
        fun getAllFiatCurrencies(): List<Currency> {
            return Currency.values().filter { it.currencyType == CurrencyType.FIAT }
        }

        /**
         * @return All crypto currencies
         */
        fun getAllCryptoCurrencies(): List<Currency> {
            return Currency.values().filter { it.currencyType == CurrencyType.CRYPTO }
        }
    }
}

/**
 * Enum that models different options for currency types
 */
enum class CurrencyType {
    FIAT, CRYPTO
}