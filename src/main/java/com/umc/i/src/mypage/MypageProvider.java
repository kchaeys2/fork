package com.umc.i.src.mypage;

import com.umc.i.config.BaseException;
import com.umc.i.src.mypage.model.get.GetMypageMemRes;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.umc.i.config.BaseResponseStatus.INTERNET_ERROR;

@Service
@Slf4j
public class MypageProvider {
    @Autowired
    private MypageDao mypageDao;

    public GetMypageMemRes getMyPMem(int memIdx) throws BaseException {
        try {
            GetMypageMemRes getMypageMemRes = mypageDao.getMyPMem(memIdx);
            getMypageMemRes.setFeedCount(mypageDao.getMyPCountMem(memIdx).get(0));
            getMypageMemRes.setDiaryCount(mypageDao.getMyPCountMem(memIdx).get(1));
            getMypageMemRes.setMarketCount(mypageDao.getMyPCountMem(memIdx).get(2));

            return getMypageMemRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
}
