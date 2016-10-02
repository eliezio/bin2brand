package io.eliez.fintools.bin2brand;

import lombok.Value;
import lombok.val;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class PrefixClassifier {

    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)(-(\\d+))?");

    private final List<PrefixRange> prefixes;

    public PrefixClassifier(Map<String, String> namedPrefixRanges) {
        this.prefixes = loadPrefixes(namedPrefixRanges);
    }

    public Optional<String> findName(String target) {
        return StreamEx.of(prefixes)
                .findFirst(prefix -> prefix.contains(target))
                .map(PrefixRange::getLabel);
    }

    private List<PrefixRange> loadPrefixes(Map<String, String> namedPrefixRanges) {
        final List<PrefixRange> prefixRanges = EntryStream.of(namedPrefixRanges)
                .flatMapKeyValue((key, value) -> StreamEx.split(value, ",").map(spec -> PrefixRange.parse(key, spec)))
                .sorted(PrefixRange.narrowToWideComparator())
                .toList();
        assertDisjointRanges(prefixRanges);
        return prefixRanges;
    }

    private void assertDisjointRanges(List<PrefixRange> prefixRanges) {
        StreamEx.of(prefixRanges)
                .forPairs((pr1, pr2) -> {
                    if ((pr1.getMinimum().length() == pr2.getMinimum().length())
                            && (pr1.getMaximum().compareTo(pr2.getMinimum()) >= 0)) {
                        throw new IllegalArgumentException(
                                String.format("illegal intersection between ranges '%s' and '%s'",
                                        pr1.toShortString(), pr2.toShortString()));
                    }
                });
    }

    @Value
    private static class PrefixRange {

        private static final Comparator<String>      PREFIX_COMPARATOR       = (element, prefix) ->
                element.startsWith(prefix) ? 0 : element.compareTo(prefix);
        private static final Comparator<PrefixRange> PREFIX_RANGE_COMPARATOR = (left, right) -> {
            final int stricterFirstCmp = right.getMinimum().length() - left.getMinimum().length();
            return (stricterFirstCmp != 0) ? stricterFirstCmp : left.getMinimum().compareTo(right.getMinimum());
        };

        String label;
        String minimum;
        String maximum;

        static PrefixRange is(String name, String element) {
            return new PrefixRange(name, element, element);
        }

        static PrefixRange between(String name, String minimum, String maximum) {
            return new PrefixRange(name, minimum, maximum);
        }

        static PrefixRange parse(String name, String rangeSpec) {
            val matcher = RANGE_PATTERN.matcher(rangeSpec);
            if (matcher.matches()) {
                if (matcher.group(3) == null) {
                    final String element = matcher.group(1);
                    return PrefixRange.is(name, element);
                } else {
                    final String lower = matcher.group(1);
                    final String upper = matcher.group(3);
                    if ((lower.compareTo(upper) > 0) || (lower.length() != upper.length())) {
                        throw new IllegalArgumentException("invalid prefix spec (invalid range): " + rangeSpec);
                    }
                    return PrefixRange.between(name, lower, upper);
                }
            } else {
                throw new IllegalArgumentException("invalid prefix spec (pattern): " + rangeSpec);
            }
        }

        boolean contains(String input) {
            return (PREFIX_COMPARATOR.compare(input, minimum) >= 0) && (PREFIX_COMPARATOR.compare(input, maximum) <= 0);
        }

        String toShortString() {
            return String.format("%s[%s-%s]", label, minimum, maximum);
        }

        static Comparator<PrefixRange> narrowToWideComparator() {
            return PREFIX_RANGE_COMPARATOR;
        }
    }
}
