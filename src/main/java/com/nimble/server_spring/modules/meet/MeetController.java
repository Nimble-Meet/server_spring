package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.auth.AuthService;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponseDto;
import com.nimble.server_spring.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/meet")
@RequiredArgsConstructor
public class MeetController {
    private final AuthService authService;
    private final MeetService meetService;

    @GetMapping
    public ResponseEntity<List<MeetResponseDto>> getMeets() {
        User currentUser = authService.getCurrentUser();
        List<Meet> meetList = meetService.getHostedOrInvitedMeets(currentUser);
        System.out.println(meetList.toString());
        List<MeetResponseDto> meetResponseDtoList = meetList.stream().map(MeetResponseDto::fromMeet).toList();
        return new ResponseEntity<>(meetResponseDtoList, HttpStatus.OK);
    }
}
