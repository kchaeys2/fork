package com.umc.i.src.feeds;

import com.umc.i.utils.S3Storage.UploadImageS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedsService {
    @Autowired
    private final UploadImageS3 uploadImageS3;
    @Autowired
    private FeedsDao feedsDao;
}
