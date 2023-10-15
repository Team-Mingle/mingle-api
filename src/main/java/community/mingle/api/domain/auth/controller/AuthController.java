package community.mingle.api.domain.auth.controller;


import community.mingle.api.domain.auth.controller.request.UpdatePwdRequest;
import community.mingle.api.domain.auth.facade.AuthFacade;
import community.mingle.api.domain.auth.controller.request.LoginMemberRequest;
import community.mingle.api.domain.auth.controller.response.LoginMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
//@Tag(name = "auth", description = "회원가입 process 관련 API") //TODO swagger 설정후 추가
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<LoginMemberResponse> login(@RequestBody @Validated LoginMemberRequest request) {
        return new ResponseEntity<>(authFacade.login(request), HttpStatus.OK);
    }


    /**
     * 1.10 비밀번호 초기화 API
     */
    @PatchMapping("/pwd")
    public ResponseEntity<String> updatePwd(@RequestBody @Validated UpdatePwdRequest request) {
        return new ResponseEntity<>(authFacade.updatePwd(request), HttpStatus.OK);
    }

}
