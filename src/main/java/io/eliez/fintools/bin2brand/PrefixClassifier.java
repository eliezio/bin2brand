package io.eliez.fintools.bin2brand;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PrefixClassifier {

    private final List<LabeledPrefixRange> prefixes;

    public PrefixClassifier(Map<String, List<PrefixRange>> rangesTable) {
        this.prefixes = loadPrefixes(rangesTable);
    }

    public Optional<String> findLabel(String target) {
        return StreamEx.of(prefixes)
                .findFirst(prefix -> prefix.contains(target))   //NOSONAR -- likely a Sonarqube error
                .map(LabeledPrefixRange::label);
    }

    public static PrefixClassifier fromTextTable(Map<String, String> rangesTextTable, RangePattern pattern) {
        return new PrefixClassifier(EntryStream.of(rangesTextTable)
                .mapValues(vv -> StreamEx.split(vv, ",").map(v -> PrefixRangeParser.parse(v, pattern)).toList())
                .toMap());
    }

    private List<LabeledPrefixRange> loadPrefixes(Map<String, List<PrefixRange>> rangesTable) {
        final List<LabeledPrefixRange> ranges = EntryStream.of(rangesTable)
                .flatMapKeyValue((k, vv) -> StreamEx.of(vv).map(v -> new LabeledPrefixRange(k, v)))
                .sorted(Comparator.comparingInt(LabeledPrefixRange::length)
                        .reversed()
                        .thenComparing(LabeledPrefixRange::lower))
                .toList();
        validateRanges(ranges);
        return ranges;
    }

    private void validateRanges(List<LabeledPrefixRange> ranges) {
        StreamEx.of(ranges)
                .forPairs((npr1, npr2) -> {
                    if ((!(npr1.label().equals(npr2.label()))
                            && npr1.length() == npr2.length())
                            && (npr1.upper().compareTo(npr2.lower()) >= 0)) {
                        throw new ConflictingRangesException(
                                String.format("illegal intersection between ranges '%s' and '%s'",
                                        npr1.toShortString(), npr2.toShortString()));
                    }
                });
    }
}
