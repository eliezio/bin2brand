package io.eliez.fintools.bin2brand;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PrefixClassifier {

    private final List<NumericPrefixRange> prefixes;

    public PrefixClassifier(Map<String, String> namedPrefixRanges) {
        this.prefixes = loadPrefixes(namedPrefixRanges);
    }

    public Optional<String> findName(String target) {
        return StreamEx.of(prefixes)
                .findFirst(prefix -> prefix.contains(target))
                .map(NumericPrefixRange::label);
    }

    private List<NumericPrefixRange> loadPrefixes(Map<String, String> rangesMap) {
        final List<NumericPrefixRange> ranges = EntryStream.of(rangesMap)
                .flatMapKeyValue((k, v) -> StreamEx.split(v, ",").map(s -> NumericPrefixRange.parse(k, s)))
                .sorted(Comparator.comparingInt(NumericPrefixRange::length)
                        .reversed()
                        .thenComparing(NumericPrefixRange::lower))
                .toList();
        validateRanges(ranges);
        return ranges;
    }

    private void validateRanges(List<NumericPrefixRange> ranges) {
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
