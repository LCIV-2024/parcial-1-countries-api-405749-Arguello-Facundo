package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryRequest;
import ar.edu.utn.frc.tup.lciii.exception.CountryNotFoundException;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CountryController {

    @Autowired
    private final CountryService countryService;

    @GetMapping("/countries")
    public List<CountryDTO> getCountry(@RequestParam(value = "code", required = false) String code,
                                       @RequestParam(value = "name", required = false) String name) {
        if (code != null || name != null) {
            return countryService.getCountriesFiltered(code, name);
        } else {
            List<Country> countries = countryService.getAllCountries();
            return countries.stream()
                    .map(countryService::mapToDTO)
                    .collect(Collectors.toList());
        }
    }

    @GetMapping("/countries/{continent}/continent")
    public List<CountryDTO> getCountriesByContinent(@PathVariable String continent) {
        return countryService.getCountriesByContinent(continent);
    }

    @GetMapping("/countries/{language}/language")
    public List<CountryDTO> getCountriesByLanguage(@PathVariable String language) {
        List<CountryDTO> countries = countryService.getCountriesByLanguage(language);
        if (countries.isEmpty()) {
            throw new CountryNotFoundException("No countries found for language: " + language);
        }
        return countries;
    }

    @GetMapping("/countries/most-borders")
    public CountryDTO getCountryWithMostBorders() {
        return countryService.getCountryWithMostBorders();
    }

    @PostMapping("/countries")
    public List<CountryDTO> saveRandomCountries(@RequestBody CountryRequest request) {
        int amountToSave = request.getAmountOfCountryToSave();
        if (amountToSave > 10) {
            throw new IllegalArgumentException("The maximum numberof countries to save is 10.");
        }

        return countryService.saveRandomCountries(amountToSave);
    }


}


