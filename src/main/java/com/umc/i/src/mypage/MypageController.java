package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    @Autowired
    private final MypageProvider mypageProvider;

    //마이 홈페이지 시작창 조회-clear
    @ResponseBody
    @GetMapping("/{memIdx}")
    public BaseResponse<GetMypageMemRes> getMyPMem(@PathVariable("memIdx") int memIdx) throws BaseException {
        try {
            GetMypageMemRes getMypageMemRes = mypageProvider.getMyPMem(memIdx);
            return new BaseResponse<>(getMypageMemRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
