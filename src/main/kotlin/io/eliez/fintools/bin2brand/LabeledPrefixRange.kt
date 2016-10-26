package io.eliez.fintools.bin2brand

internal class LabeledPrefixRange private constructor(val label: String, lower: String, upper: String) : PrefixRange(lower, upper) {

    constructor(label: String, range: PrefixRange) : this(label, range.lower, range.upper) {
    }

    fun toShortString(): String {
        return String.format("%s[%s-%s]", label, lower, upper)
    }
}
