package io.eliez.fintools.bin2brand;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Comparator;

@Getter
@Accessors(fluent = true)
class PrefixRange {
    private static final Comparator<String> COMPARATOR = (element, prefix) ->
            element.startsWith(prefix) ? 0 : element.compareTo(prefix);

    final String lower;
    final String upper;

    PrefixRange(String lower, String upper) {
        if ((lower.length() != upper.length()) || (lower.compareTo(upper) > 0)) {
            throw new IllegalArgumentException(String.format("invalid bounds: '%s-%s'", lower, upper));
        }
        this.lower = lower;
        this.upper = upper;
    }

    int length() {
        return lower().length();
    }

    boolean contains(String input) {
        return (COMPARATOR.compare(input, lower) >= 0) && (COMPARATOR.compare(input, upper) <= 0);
    }
}
