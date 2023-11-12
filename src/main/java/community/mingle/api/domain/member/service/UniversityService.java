package community.mingle.api.domain.member.service;

import community.mingle.api.domain.auth.controller.response.DomainResponse;
import community.mingle.api.domain.member.entity.University;
import community.mingle.api.domain.member.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;

    public List<DomainResponse> getDomains(String countryName) {
        List<University> domains = universityRepository.findAllByCountryName(countryName);
        return domains.stream()
                .map(university -> new DomainResponse(university.getEmailDomain()))
                .collect(Collectors.toList());

    }


}
