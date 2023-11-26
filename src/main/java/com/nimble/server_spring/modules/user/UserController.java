package com.nimble.server_spring.modules.user;

import static com.nimble.server_spring.infra.apidoc.SwaggerConfig.JWT_ACCESS_TOKEN;

import com.nimble.server_spring.infra.apidoc.ApiErrorCodes;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.modules.user.dto.response.SimpleUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User 관련 API")
@SecurityRequirement(name = JWT_ACCESS_TOKEN)
public class UserController {

    private final UserService userService;

    @GetMapping("/{email}")
    @Operation(summary = "유저 조회", description = "이메일로 유저를 조회합니다.")
    @ApiErrorCodes({
        ErrorCode.USER_NOT_FOUND_BY_EMAIL
    })
    public ResponseEntity<SimpleUserResponseDto> getUserByEmail(
        @PathVariable @Parameter(description = "이메일", required = true)
        String email
    ) {
        User user = userService.getUserByEmail(email);
        SimpleUserResponseDto simpleUserResponseDto = SimpleUserResponseDto.fromUser(user);
        return new ResponseEntity<>(simpleUserResponseDto, HttpStatus.OK);
    }
}
