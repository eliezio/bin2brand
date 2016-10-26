package io.eliez.fintools.bin2brand

import java.util.regex.Pattern

class RangePattern(boundPattern: String) {

    val pattern: Pattern

    init {
        this.pattern = Pattern.compile(String.format("(%s)(-(%s))?", boundPattern, boundPattern))
    }

    companion object {
        val NUMERIC = RangePattern("\\d+")
    }
}
