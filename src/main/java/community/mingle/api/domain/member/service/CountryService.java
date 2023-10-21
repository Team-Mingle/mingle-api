package community.mingle.api.domain.member.service;

import java.util.List;

import community.mingle.api.domain.auth.controller.response.CountryResponse;
import community.mingle.api.domain.member.entity.Country;
import community.mingle.api.domain.member.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<CountryResponse> getCountries() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream()
                .map(country -> new CountryResponse(country.getCountry()))
                .collect(Collectors.toList());
    }



}
