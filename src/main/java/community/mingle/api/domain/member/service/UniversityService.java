package community.mingle.api.domain.member.service;

import community.mingle.api.domain.member.entity.Country;
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
    public List<String> getEmailDomainsAndNames() {
        List<University> universities = universityRepository.findAll();
        return universities.stream().map(University::getEmailDomain).collect(Collectors.toList());
    }
}
