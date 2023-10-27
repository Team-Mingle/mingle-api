package community.mingle.api.security.bean;

import community.mingle.api.dto.security.TokenDto;
import community.mingle.api.security.component.TokenVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenVerifier tokenVerifier;

    @Override
    public void doFilterInternal(
        @NotNull
        HttpServletRequest request,
        @NotNull
        HttpServletResponse response,
        @NotNull
        FilterChain filterChain
    ) throws ServletException, IOException {
        TokenDto verifiedTokenDto = tokenVerifier.verify(request);
        authenticate(verifiedTokenDto);
        filterChain.doFilter(request, response);
    }

    private void authenticate(TokenDto tokenDto) {
        UsernamePasswordAuthenticationToken authenticationToken;
        if (tokenDto != null) {
            authenticationToken = new UsernamePasswordAuthenticationToken(tokenDto, tokenDto.getMemberId(), List.of(new SimpleGrantedAuthority(tokenDto.getMemberRole().toString())));
        } else authenticationToken = null;

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
