package community.mingle.api.domain.member.service;

import java.util.List;

import community.mingle.api.domain.member.entity.Country;
import community.mingle.api.domain.member.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<String> getCountries() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream().map(Country::getCountry).collect(Collectors.toList());
    }

}
