package community.mingle.api.domain.member.service;

import community.mingle.api.domain.auth.controller.response.UniversityResponse;
import community.mingle.api.domain.member.entity.University;
import community.mingle.api.domain.member.repository.UniversityRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.api.global.exception.ErrorCode.UNIVERSITY_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;

    public List<UniversityResponse> getUniversityList(String countryName) {
        List<University> universityList = universityRepository.findAllByCountryName(countryName);
        return universityList.stream()
                .map(university -> new UniversityResponse(university.getId() ,university.getEmailDomain()))
                .collect(Collectors.toList());

    }


    public University getUniversity(int universityId) {
        return universityRepository.findById(universityId).orElseThrow(() -> new CustomException(UNIVERSITY_NOT_FOUND));
    }

}
