package community.mingle.api.domain.auth.controller;


import community.mingle.api.domain.member.service.UniversityService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import community.mingle.api.domain.member.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class Authcontroller {

    private final CountryService countryService;
    private final UniversityService universityService;

    /**
     * 국가 리스트 api
     */
    @GetMapping("countries")
    public ResponseEntity<List<String>> getCountries() {
        List<String> countries = countryService.getCountries();
        return ResponseEntity.ok().body(countries);
    }

    /**
     * 학교 및 도메인 리스트 불러오기 api
     */
    @GetMapping("/email-domains")
    public ResponseEntity<List<String>> getEmailDomains() {
        List<String> universities = universityService.getEmailDomainsAndNames();
        return ResponseEntity.ok().body(universities);
    }









}
