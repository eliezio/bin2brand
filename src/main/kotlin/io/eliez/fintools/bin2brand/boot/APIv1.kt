package io.eliez.fintools.bin2brand.boot

import io.eliez.fintools.bin2brand.PrefixClassifier
import io.eliez.fintools.bin2brand.RangePattern
import io.eliez.fintools.validation.BIN
import one.util.streamex.EntryStream
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1")
@CrossOrigin
@Validated
class APIv1 {
    private val prefixClassifier = PrefixClassifier.fromTextTable(EntryStream.of(
            "Visa", "4",
            "Mastercard", "51-55,2221-2720",
            "Diners", "301,305,36,38",
            "Elo", "4011,431274,438935,451416,457393,4576,457631,457632,504175,504175,506699-506778,509,627780," +
            "636297,636368,636369,650031-650033,650035-650051,650405-650439,650485-650538,650541-650598," +
            "650700-650718,650720-650727,650901-650920,651652-651679,655000-655019,655021-655058",
            "Amex", "34,37",
            "Discover", "6011,622,64,65",
            "Aura", "50",
            "JCB", "35",
            "Hipercard", "384100,384140,384160,606282,637095,637568,637599,637609,637612"
    ).toMap(), RangePattern.NUMERIC)

    @GetMapping("/card-brand")
    fun findBrand(@BIN @RequestParam bin: String): Map<*, *> {
        return prefixClassifier.findLabel(bin)
                ?.associate { it -> Pair("brand", it) }
                ?: throw ResourceNotFoundException()
    }
}
