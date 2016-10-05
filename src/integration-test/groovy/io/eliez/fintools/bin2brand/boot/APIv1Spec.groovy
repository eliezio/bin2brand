package io.eliez.fintools.bin2brand.boot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll

import static org.hamcrest.core.Is.is
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
@AutoConfigureRestDocs("build/generated-snippets")
class APIv1Spec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Unroll
    def "fails with NOT_FOUND when BIN is unallocated to any brand (BIN=#bin)"() {
        expect:
        mockMvc.perform(get('/v1/card-brand').param('bin', bin))
                .andExpect(status().isNotFound())
                .andDo(print())

        where:
        bin << ['100000']
    }

    @Unroll
    def "fails with BAD_REQUEST when BIN is ill-formed (#bin) -- #description"() {
        expect:
        mockMvc.perform(get('/v1/card-brand').param('bin', bin))
                .andExpect(status().isBadRequest())
                .andDo(print())

        where:
        bin       | description
        '43210A'  | 'has a non-decimal char'
        '4321321' | 'if longer thant 6 digits'
        '43213'   | 'if shorter thant 6 digits'
    }

    @Unroll
    def "succeeds to find the brand of a valid BIN=#bin"() {
        expect: 'classification succeeds'
        mockMvc.perform(get('/v1/card-brand').param('bin', bin))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.brand', is(expectedBrand)))
                .andDo(document('v1/get-card-brand',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('bin').description('Bank Identification Number, i.e., the first 6 digits of a card number'),
                        ),
                        responseFields(
                                fieldWithPath('brand').description('The brand\'s name')
                )))
                .andDo(print())

        where:
        bin      || expectedBrand
        '376411' || 'Amex'
    }
}
