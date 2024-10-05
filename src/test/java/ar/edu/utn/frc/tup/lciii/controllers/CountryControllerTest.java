package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @Test
    void testGetCountries() throws Exception {
        when(countryService.getCountriesFiltered(anyString(), anyString())).thenReturn(List.of(new CountryDTO("ARG", "Argentina")));
        mockMvc.perform(get("/countries?code=ARG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));
    }

    @Test
    void testSaveRandomCountries() throws Exception {
        when(countryService.saveRandomCountries(anyInt())).thenReturn(List.of(new CountryDTO("ARG", "Argentina")));
        mockMvc.perform(post("/countries")
                        .contentType("application/json")
                        .content("{\"amountOfCountryToSave\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));
    }
}
