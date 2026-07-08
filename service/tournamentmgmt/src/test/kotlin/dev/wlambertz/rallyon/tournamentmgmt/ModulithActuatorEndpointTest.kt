package dev.wlambertz.rallyon.tournamentmgmt

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ModulithActuatorEndpointTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `exposes nested setup modules via actuator`() {
        mockMvc.perform(get("/actuator/modulith"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("\"setup.configuration\"")))
            .andExpect(content().string(containsString("\"setup.rules\"")))
            .andExpect(content().string(containsString("\"setup.phases\"")))
    }
}
