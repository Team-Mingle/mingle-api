package community.mingle.api.domain.member.service;

import java.util.List;
import community.mingle.api.domain.auth.controller.response.CountryResponse;
import community.mingle.api.domain.member.entity.Country;
import community.mingle.api.domain.member.repository.CountryRepository;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
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
                .map(country -> new CountryResponse(country.getName()))
                .collect(Collectors.toList());
    }

    public Country getById(String countryId) {
        return countryRepository.findById(countryId).orElseThrow(
                () -> new CustomException(ErrorCode.COUNTRY_NOT_FOUND)
        );
    }

}
