package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.exception.CountryNotFoundException;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

        @Autowired
        private final CountryRepository countryRepository;

        @Autowired
        private final RestTemplate restTemplate;

        public List<Country> getAllCountries() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
                return response.stream().map(this::mapToCountry).collect(Collectors.toList());
        }

        /**
         * Agregar mapeo de campo cca3 (String)
         * Agregar mapeo campos borders ((List<String>))
         */
        private Country mapToCountry(Map<String, Object> countryData) {
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
                return Country.builder()
                        .name((String) nameData.get("common"))
                        .population(((Number) countryData.get("population")).longValue())
                        .area(((Number) countryData.get("area")).doubleValue())
                        .region((String) countryData.get("region"))
                        .code((String) countryData.get("cca3"))
                        .languages((Map<String, String>) countryData.get("languages"))
                        .borders((List<String>) countryData.get("borders"))
                        .build();
        }


        public CountryDTO mapToDTO(Country country) {
                return new CountryDTO(country.getCode(), country.getName());
        }

        public List<CountryDTO> getCountriesFiltered(String code, String name) {
                List<Country> allCountries = getAllCountries();

                return allCountries.stream()
                        .filter(country -> (code == null || country.getCode().equalsIgnoreCase(code)) &&
                                (name == null || country.getName().equalsIgnoreCase(name)))
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        public List<CountryDTO> getCountriesByContinent(String continent) {
                return getAllCountries().stream()
                        .filter(country -> country.getRegion().equalsIgnoreCase(continent))
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        public List<CountryDTO> getCountriesByLanguage(String language) {
                return getAllCountries().stream()
                        .filter(country -> {
                                Map<String, String> languages = country.getLanguages();
                                return languages != null && languages.containsValue(language);
                        })
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
        }

        public CountryDTO getCountryWithMostBorders() {
                List<Country> allCountries = getAllCountries();

                return allCountries.stream()
                        .filter(country -> country.getBorders() != null && !country.getBorders().isEmpty())
                        .max(Comparator.comparingInt(country -> country.getBorders().size()))
                        .map(this::mapToDTO)
                        .orElseThrow(() -> new CountryNotFoundException("No countries with borders found"));
        }


        public List<CountryDTO> saveRandomCountries(int amount) {
                List<Country> allCountries = getAllCountries();
                if (amount > 10) {
                        throw new IllegalArgumentException("The máximum number ofcountries to save is 10.");
                }
                Collections.shuffle(allCountries);
                List<Country> countriesToSave = allCountries.stream()
                        .limit(amount)
                        .collect(Collectors.toList());
                List<Country> savedCountries = countryRepository.saveAll(countriesToSave);
                return savedCountries.stream()
                        .map(this::mapToDTO)
                        .collect(Collectors.toList());
        }




}