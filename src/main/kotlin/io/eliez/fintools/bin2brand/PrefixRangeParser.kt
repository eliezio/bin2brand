package io.eliez.fintools.bin2brand

class PrefixRangeParser {
    companion object {

        internal fun parse(rangeSpec: String, rangePattern: RangePattern): PrefixRange {
            val matcher = rangePattern.pattern.matcher(rangeSpec)
            if (!matcher.matches()) {
                throw IllegalArgumentException("invalid prefix spec (pattern): " + rangeSpec)
            }
            if (matcher.group(3) == null) {
                val element = matcher.group(1)
                return PrefixRange(element, element)
            } else {
                val lower = matcher.group(1)
                val upper = matcher.group(3)
                return PrefixRange(lower, upper)
            }
        }

        @JvmStatic
        fun parseNumeric(rangeSpec: String): PrefixRange {
            return parse(rangeSpec, RangePattern.NUMERIC)
        }
    }
}
