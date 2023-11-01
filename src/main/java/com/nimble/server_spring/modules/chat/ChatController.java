//package com.nimble.server_spring.modules.chat;
//
//import com.nimble.server_spring.modules.meet.MeetMemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Controller
//@Slf4j
//@RequiredArgsConstructor
//public class ChatController {
//
//    private final SimpMessageSendingOperations template;
//    private final ChatRepository chatRepository;
//    private final MeetMemberRepository meetMemberRepository;
//
//    @MessageMapping("/chat/enterUser")
//    public void enterUser(@Payload ChatEnterDto chatEnterDto, SimpMessageHeaderAccessor headerAccessor) {
//
//        meetMemberRepository.findBy
//
//        String userUUID = repository.addUser(chat.getRoomId(), chat.getSender());
//
//        headerAccessor.getSessionAttributes().put("senderEmail", chatDto.getSenderEmail());
//        headerAccessor.getSessionAttributes().put("meetId", chatDto.getMeetId());
//
//        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
//    }
//
//    @MessageMapping("/chat/sendMessage")
//    public void sendMessage(@Payload ChatDto chatDto) {
//        Chat chat = chatDto.toChatEntity();
//        chatRepository.save(chat);
//        template.convertAndSend("/sub/chat/room/" + chatDto.getMeetId(), chatDto);
//    }
//
//    //유저 퇴장 시에는 EventListener 를 통해서 유저 퇴장을 확인
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//
//        log.info("DisconnectEvent : {}", event);
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        // stomp 세션에 있던 uuid 와 roomId 를 확인하여 채팅방 유저 리스트와 room에서 해당 유저를 삭제
//        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
//        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
//
//        log.info("headAccessor : {}", headerAccessor);
//
//        // 채팅방 유저 -1
//        repository.decreaseUser(roomId);
//
//        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
//        String userName = repository.getUserName(roomId, userUUID);
//        repository.deleteUser(roomId, userUUID);
//
//        if (userName != null) {
//            log.info("User Disconnected : " + userName);
//        }
//    }
//}
