package io.eliez.fintools.bin2brand;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class CardBrandClassifier {

    private final List<Map.Entry<String, Prefix>> prefixes;

    public CardBrandClassifier(Map<String, String> keyValues) {
        this.prefixes = ImmutableList.copyOf(loadPrefixes(keyValues));
    }

    public Optional<String> findBrand(String pan) {
        return prefixes.stream()
                .filter(e -> e.getValue().match(pan))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private static List<Map.Entry<String, Prefix>> loadPrefixes(Map<String, String> keyValues) {
        val prefixes = new ArrayList<Map.Entry<String, Prefix>>();
        keyValues.forEach((brand, ranges) -> {
            for (String spec : ranges.split(",")) {
                prefixes.add(Maps.immutableEntry(brand, PrefixParser.parse(spec)));
            }
        });
        prefixes.sort((left, right) -> -left.getValue().compareTo(right.getValue()));
        return prefixes;
    }

    interface Prefix extends Comparable<Prefix> {
        int length();

        String beginPrefix();

        boolean match(String pan);
    }

    abstract static class AbstractPrefix implements Prefix {

        @Override
        public int compareTo(Prefix other) {
            int cmp = this.length() - other.length();
            if (cmp == 0) {
                cmp = this.beginPrefix().compareTo(other.beginPrefix());
            }
            return cmp;
        }
    }

    @UtilityClass
    private static class PrefixParser {

        private static final Pattern PATTERN = Pattern.compile("(\\d+)(-(\\d+))?");

        static Prefix parse(String spec) {
            val matcher = PATTERN.matcher(spec);
            if (matcher.matches()) {
                if (matcher.group(3) == null) {
                    return new SinglePrefix(matcher.group(1));
                } else {
                    return new RangePrefix(matcher.group(1), matcher.group(3));
                }
            } else {
                throw new IllegalArgumentException("invalid prefix spec: " + spec);
            }
        }
    }

    @lombok.Value
    @EqualsAndHashCode(callSuper = true)
    private static class SinglePrefix extends AbstractPrefix {
        private final String prefix;

        @Override
        public int length() {
            return prefix.length();
        }

        @Override
        public String beginPrefix() {
            return prefix;
        }

        @Override
        public boolean match(String pan) {
            return pan.startsWith(prefix);
        }
    }

    @lombok.Value
    @EqualsAndHashCode(callSuper = true)
    private static class RangePrefix extends AbstractPrefix {
        private final String beginPrefix;
        private final String endPrefix;

        RangePrefix(String beginPrefix, String endPrefix) {
            Preconditions.checkArgument(beginPrefix.length() == endPrefix.length(),
                    "begin and end prefix must have the same length");
            this.beginPrefix = beginPrefix;
            this.endPrefix = endPrefix;
        }

        @Override
        public int length() {
            return beginPrefix.length();
        }

        @Override
        public String beginPrefix() {
            return beginPrefix;
        }

        @Override
        public boolean match(String pan) {
            if (pan.length() < beginPrefix.length()) {
                return false;
            }
            val target = pan.substring(0, beginPrefix.length());
            return (target.compareTo(beginPrefix) >= 0) && (target.compareTo(endPrefix) <= 0);
        }
    }
}
