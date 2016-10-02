package io.eliez.fintools.bin2brand;

import lombok.Value;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.Comparator;
import java.util.regex.Pattern;

@Value
@Accessors(fluent = true)
class NumericPrefixRange {

    private static final Pattern            SPEC_PATTERN = Pattern.compile("(\\d+)(-(\\d+))?");
    private static final Comparator<String> COMPARATOR   = (element, prefix) ->
            element.startsWith(prefix) ? 0 : element.compareTo(prefix);

    String label;
    String lower;
    String upper;

    int length() {
        return lower().length();
    }

    static NumericPrefixRange parse(String name, String rangeSpec) {
        val matcher = SPEC_PATTERN.matcher(rangeSpec);
        if (matcher.matches()) {
            if (matcher.group(3) == null) {
                final String element = matcher.group(1);
                return new NumericPrefixRange(name, element, element);
            } else {
                final String lower = matcher.group(1);
                final String upper = matcher.group(3);
                if ((lower.length() != upper.length()) || (lower.compareTo(upper) > 0)) {
                    throw new IllegalArgumentException("invalid prefix spec (invalid range): " + rangeSpec);
                }
                return new NumericPrefixRange(name, lower, upper);
            }
        } else {
            throw new IllegalArgumentException("invalid prefix spec (pattern): " + rangeSpec);
        }
    }

    boolean contains(String input) {
        return (COMPARATOR.compare(input, lower) >= 0) && (COMPARATOR.compare(input, upper) <= 0);
    }

    String toShortString() {
        return String.format("%s[%s-%s]", label, lower, upper);
    }
}
