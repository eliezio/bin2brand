package io.eliez.fintools.bin2brand;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

@Getter
@Accessors(fluent = true)
public class RangePattern {
    public static final RangePattern NUMERIC = new RangePattern("\\d+");

    private final Pattern pattern;

    public RangePattern(String boundPattern) {
        this.pattern = Pattern.compile(String.format("(%s)(-(%s))?", boundPattern, boundPattern));
    }
}
