package io.eliez.fintools.bin2brand

open class PrefixRange(val lower: String, val upper: String) {

    init {
        if (lower.length != upper.length || lower > upper) {
            throw IllegalArgumentException(String.format("invalid bounds: '%s-%s'", lower, upper))
        }
    }

    fun length(): Int {
        return lower.length
    }

    operator fun contains(input: String): Boolean {
        return COMPARATOR(input, lower) >= 0 && COMPARATOR(input, upper) <= 0
    }

    companion object {
        private fun COMPARATOR(element: String, prefix: String): Int =
                if (element.startsWith(prefix)) 0 else element.compareTo(prefix)
    }
}
