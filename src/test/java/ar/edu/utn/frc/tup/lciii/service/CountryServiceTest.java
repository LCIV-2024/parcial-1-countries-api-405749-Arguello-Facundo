package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCountries() {
        List<Map<String, Object>> response = Arrays.asList(
                Map.of("name", Map.of("common", "Argentina"), "population", 45000000, "area", 2780400.0, "region", "Americas", "cca3", "ARG", "languages", Map.of("spa", "Spanish"), "borders", List.of("BRA", "CHL")),
                Map.of("name", Map.of("common", "Brazil"), "population", 211000000, "area", 8515767.0, "region", "Americas", "cca3", "BRA", "languages", Map.of("por", "Portuguese"), "borders", List.of("ARG", "URY"))
        );

        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(response);

        List<Country> countries = countryService.getAllCountries();

        assertEquals(2, countries.size());
        assertEquals("Argentina", countries.get(0).getName());
        assertEquals("Brazil", countries.get(1).getName());
    }

    @Test
    void testGetCountriesFilteredByCode() {
        List<Country> countries = List.of(
                Country.builder().name("Argentina").code("ARG").build(),
                Country.builder().name("Brazil").code("BRA").build()
        );

        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(Collections.emptyList());

        List<CountryDTO> filteredCountries = countryService.getCountriesFiltered("ARG", null);

        assertTrue(filteredCountries.isEmpty());
    }

    @Test
    void testSaveRandomCountries() {
        List<Country> countries = List.of(
                Country.builder().name("Argentina").code("ARG").build(),
                Country.builder().name("Brazil").code("BRA").build()
        );

        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(countries);
        when(countryRepository.saveAll(anyList())).thenReturn(countries);

        List<CountryDTO> savedCountries = countryService.saveRandomCountries(2);

        assertEquals(2, savedCountries.size());
        verify(countryRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetCountryWithMostBorders() {
        List<Country> countries = List.of(
                Country.builder().name("Argentina").code("ARG").borders(List.of("BRA", "CHL", "URY")).build(),
                Country.builder().name("Brazil").code("BRA").borders(List.of("ARG", "URY")).build()
        );
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(countries);
        CountryDTO countryWithMostBorders = countryService.getCountryWithMostBorders();
        assertEquals("Argentina", countryWithMostBorders.getName());
    }
}
