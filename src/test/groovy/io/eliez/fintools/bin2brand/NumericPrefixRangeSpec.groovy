package io.eliez.fintools.bin2brand

import spock.lang.Specification
import spock.lang.Unroll

class NumericPrefixRangeSpec extends Specification {

    @Unroll
    def "accepts valid range spec: '#lower-#upper'"() {
        when:
        def npr = NumericPrefixRange.parse("foobar", "$lower-$upper")

        then:
        npr.lower() == lower
        npr.upper() == upper

        where:
        lower | upper
        '122' | '123'
        '125' | '151'
        '123' | '456'
    }

    @Unroll
    def "fails when range spec is invalid: '#rangeSpec'"() {
        when:
        NumericPrefixRange.parse("foobar", rangeSpec)

        then:
        thrown(IllegalArgumentException)

        where:
        rangeSpec << [
                '123-122',
                '12-151',
                '123 - 456',
                '123:456'
        ]
    }
}
