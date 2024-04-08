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
                //TODO 하나의 대학에 여러 개의 이메일 도메인이 있을 것을 대비해서 List<String> 으로 프론트에 전달
                //현재 하나의 대학에 하나의 도메인만 가능한 테이블 구조를 개선할 필요가 있음
                //아니면 현재 하나의 컬럼에 csv 형태로 저장 후 converter를 이용해 List로 변환
                .map(university -> {
                    List<String> universityEmailDomainList;
                    if (university.getEmailDomain() == null) {
                        universityEmailDomainList = List.of();
                    } else universityEmailDomainList = List.of(university.getEmailDomain());

                    return new UniversityResponse(university.getId(), university.getName(), universityEmailDomainList);
                })
                .collect(Collectors.toList());

    }


    public University getUniversity(int universityId) {
        return universityRepository.findById(universityId).orElseThrow(() -> new CustomException(UNIVERSITY_NOT_FOUND));
    }

}
