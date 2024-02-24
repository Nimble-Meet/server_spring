package com.nimble.server_spring.modules.chat.dto.request;

import com.nimble.server_spring.modules.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetChatListServiceRequest {

    private final User currentUser;
    private final Long meetId;
    private final Integer size;
    private final Integer page;

    @Builder
    private GetChatListServiceRequest(User currentUser, Long meetId, Integer size, Integer page) {
        this.currentUser = currentUser;
        this.meetId = meetId;
        this.size = size;
        this.page = page;
    }

    public static GetChatListServiceRequest create(
        User currentUser,
        Long meetId,
        Integer size,
        Integer page
    ) {
        return GetChatListServiceRequest.builder()
            .currentUser(currentUser)
            .meetId(meetId)
            .size(size)
            .page(page)
            .build();
    }
}
