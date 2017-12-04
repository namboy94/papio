package net.namibsun.papio.cli.argparse

/**
 * Class that contains a method that prints a help message
 */
class HelpPrinter {

    /**
     * Prints a help message to the console and exits with exit code 1
     */
    fun printAndExit() {

        println("papio-cli options:\n")
        for (rootMode in RootMode.values()) {
            val options = "${modeMap[rootMode]!!.map { it.name }}"
            println("${rootMode.name} $options")
        }
        System.exit(1)
    }
}
