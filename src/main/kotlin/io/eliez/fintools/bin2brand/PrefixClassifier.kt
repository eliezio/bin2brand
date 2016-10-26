package io.eliez.fintools.bin2brand

import one.util.streamex.StreamEx
import kotlin.comparisons.compareByDescending
import kotlin.comparisons.thenBy

internal class PrefixClassifier(rangesTable: Map<String, List<PrefixRange>>) {

    private val prefixes: List<LabeledPrefixRange>

    init {
        this.prefixes = loadPrefixes(rangesTable)
    }

    fun findLabel(target: String): String? {
        return prefixes.find { it -> it.contains(target) }?.label
    }

    private fun loadPrefixes(rangesTable: Map<String, List<PrefixRange>>): List<LabeledPrefixRange> {
        val ranges = rangesTable.flatMap { entry -> entry.value.map { it -> LabeledPrefixRange(entry.key, it) } }
                .sortedWith(compareByDescending<LabeledPrefixRange> { it.length() }.thenBy { it.lower })
        validateRanges(ranges)
        return ranges
    }

    private fun validateRanges(ranges: List<LabeledPrefixRange>) {
        StreamEx.of(ranges)
                .forPairs { npr1, npr2 ->
                    if (npr1.label != npr2.label &&
                            npr1.length() == npr2.length() &&
                            npr1.upper >= npr2.lower) {
                        throw ConflictingRangesException(
                                String.format("illegal intersection between ranges '%s' and '%s'",
                                        npr1.toShortString(), npr2.toShortString()))
                    }
                }
    }

    companion object {

        @JvmStatic
        fun fromTextTable(rangesTextTable: Map<String, String>, pattern: RangePattern): PrefixClassifier {
            return PrefixClassifier(rangesTextTable.mapValues {
                entry ->
                entry.value.split(",").map { it -> PrefixRangeParser.parse(it, pattern) }
            })
        }
    }
}
