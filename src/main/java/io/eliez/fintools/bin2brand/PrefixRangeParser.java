package io.eliez.fintools.bin2brand;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class PrefixRangeParser {

    static PrefixRange parse(String rangeSpec, RangePattern rangePattern) {
        val matcher = rangePattern.pattern().matcher(rangeSpec);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("invalid prefix spec (pattern): " + rangeSpec);
        }
        if (matcher.group(3) == null) {
            final String element = matcher.group(1);
            return new PrefixRange(element, element);
        } else {
            final String lower = matcher.group(1);
            final String upper = matcher.group(3);
            return new PrefixRange(lower, upper);
        }
    }

    static PrefixRange parseNumeric(String rangeSpec) {
        return parse(rangeSpec, RangePattern.NUMERIC);
    }
}
