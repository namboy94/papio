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
    EUR(CurrencyType.FIAT, 2, "€"),
    USD(CurrencyType.FIAT, 2, "$"),
    ZAR(CurrencyType.FIAT, 2, "R"),
    NAD(CurrencyType.FIAT, 2, "N$"),
    BTC(CurrencyType.CRYPTO, 8, "BTC");

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