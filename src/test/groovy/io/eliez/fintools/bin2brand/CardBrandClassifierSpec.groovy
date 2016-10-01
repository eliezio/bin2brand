package io.eliez.fintools.bin2brand

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class CardBrandClassifierSpec extends Specification {

    @Shared def cardBrandClassifier = new CardBrandClassifier([
            "Visa": "4",
            "Mastercard": "5",
            "Diners": "301,305,36,38",
            "Elo": "4011,401178,401179,431274,438935,451416,457393,4576,457631,457632,504175,504175,506699-506778,509000-509999,627780,636297,636368,636369,650031-650033,650035-650051,650405-650439,650485-650538,650541-650598,650700-650718,650720-650727,650901-650920,651652-651679,655000-655019,655021-655058",
            "Amex": "34,37",
            "Discover": "6011,622,64,65",
            "Aura": "50",
            "JCB": "35",
            "Hipercard": "3841,606282",
    ])

    @Unroll
    def "identifica o cartão com BIN #bin"() {
        when:
        final Optional<String> brand = cardBrandClassifier.findBrand(bin)

        then:
        brand.isPresent()
        brand.get() == expectedBrand

        where:
        bin       || expectedBrand
        "400912"  || "Visa"
        "401200"  || "Visa"
        "407302"  || "Visa"
        "606282"  || "Hipercard"
        "384100"  || "Hipercard"
        "552129"  || "Mastercard"
        "545301"  || "Mastercard"
        "549167"  || "Mastercard"
        "555566"  || "Mastercard"
        "376449"  || "Amex"
        "376411"  || "Amex"
        "636297"  || "Elo"
        "506766"  || "Elo"
        "364901"  || "Diners"
        "301111"  || "Diners"
        "601102"  || "Discover"
        "356600"  || "JCB"
        "507860"  || "Aura"
    }
}