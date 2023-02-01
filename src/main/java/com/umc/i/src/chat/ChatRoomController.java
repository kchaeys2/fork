package com.umc.i.src.chat;

import com.umc.i.config.BaseResponse;
import com.umc.i.src.chat.model.get.GetChatRoomRes;
import com.umc.i.src.chat.model.get.GetChatRoomsRes;
import com.umc.i.src.chat.model.post.PostChatRoom;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    @Autowired
    private final com.umc.i.src.chat.ChatRoomRepository chatRoomRepository;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "room";
    }
    // 모든 채팅방 목록 반환-clear
    @GetMapping("/rooms/{memIdx}")
    @ResponseBody
    public BaseResponse<List<GetChatRoomsRes>> room(@PathVariable int memIdx) {
        return new BaseResponse<>(chatRoomRepository.findAllRoom(memIdx));
    }
    // 채팅방 생성-clear
    @PostMapping("/room")
    @ResponseBody
    public PostChatRoom createRoom(@ModelAttribute PostChatRoom postChatRoom) {
        return chatRoomRepository.createChatRoom(postChatRoom);
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomIdx}")
    public String roomDetail(Model model, @PathVariable String roomIdx) {
        model.addAttribute("roomIdx", roomIdx);
        return "roomdetail";
    }
    // 특정 채팅방 조회-clear
    @GetMapping("/room/{roomIdx}")
    @ResponseBody
    public BaseResponse<List<GetChatRoomRes>> roomInfo(@PathVariable int roomIdx) {

        return new BaseResponse<>(chatRoomRepository.getChatRoomIdx(roomIdx));
    }
}