package dev.wlambertz.rallyon.tournamentmgmt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ModulithActuatorEndpointTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void exposesNestedSetupModulesViaActuator() throws Exception {
    mockMvc.perform(get("/actuator/modulith"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("\"setup.configuration\"")))
        .andExpect(content().string(containsString("\"setup.rules\"")))
        .andExpect(content().string(containsString("\"setup.phases\"")));
  }
}
