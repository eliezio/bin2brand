package io.eliez.fintools.bin2brand;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
class LabeledPrefixRange extends PrefixRange {
    private final String label;

    private LabeledPrefixRange(String label, String lower, String upper) {
        super(lower, upper);
        this.label = label;
    }

    LabeledPrefixRange(String label, PrefixRange range) {
        this(label, range.lower, range.upper);
    }

    String toShortString() {
        return String.format("%s[%s-%s]", label, lower, upper);
    }
}
