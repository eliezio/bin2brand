package io.eliez.fintools.bin2brand.boot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.TestPropertySource
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
@TestPropertySource("file:config/application-test.properties")
@AutoConfigureRestDocs("build/generated-snippets")
class APIv1Test extends Specification {

    @Autowired
    MockMvc mockMvc

    @Unroll
    def "fails if BIN is invalid (#bin)"() {
        expect:
        mockMvc.perform(get('/v1/card-brand').param('bin', bin))
                .andExpect(status().isNotFound())

        where:
        bin << ['1000']
    }

    @Unroll
    def "can classify valid BIN=#bin"() {
        expect: 'classification succeeds'
        mockMvc.perform(get('/v1/card-brand').param('bin', bin))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(expectedBrand)))
                .andDo(document('v1/get-card-brand',
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName('bin').description('Bank Identification Number, i.e., the first 6 digits of a card number'),
                        ),
                        responseFields(
                                fieldWithPath('name').description('The brand\'s name')
                )))

        where:
        bin      || expectedBrand
        '376411' || 'Amex'
    }
}
