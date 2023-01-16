package com.umc.i.src.feeds;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.utils.S3Storage.Image;

@Repository
public class FeedsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 이야기방, 일기장 게시글 저장
    public int createFeeds(int boardType, PostFeedsReq postFeedsReq) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String createFeedsQuery = null;

        switch(boardType) {
            case 1: //이야기방
                createFeedsQuery = "insert into Story_feed (story_roomType, mem_idx, story_title, story_content, ";
                createFeedsQuery += "story_image, story_hit, story_comment_count, story_like_count, story_blame, story_created_at)";
                createFeedsQuery += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            case 2: //일기장
                 createFeedsQuery = "insert into Story_feed (story_roomType, mem_idx, story_title, story_content, ";
                createFeedsQuery += "story_image, story_hit, story_comment_count, story_like_count, story_blame, story_created_at)";
                createFeedsQuery += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        Object[] createFeedsParams = new Object[] {postFeedsReq.getRoomType(), postFeedsReq.getUserIdx(), postFeedsReq.getTitle(),
                                postFeedsReq.getContent(), postFeedsReq.getImgCnt(), 0, 0, 0, 0, currentTime};
        this.jdbcTemplate.update(createFeedsQuery, createFeedsParams);  // 게시물 저장

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        int feedsIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
        
        return feedsIdx;
    }

    // 이야기방, 일기장 이미지 정보 저장
    public void createFeedsImage(List<Image> img, int feedsIdx) {
        String createFeedsImageQuery = "insert into Image_url (content_category, content_idx, image_url, image_order)";
        createFeedsImageQuery += " values (?, ?, ?, ?)";
        for (int i = 0; i < img.size(); i++) {
            Object[] createFeedsImageParams = new Object[] {img.get(i).getCategory(), feedsIdx, img.get(i).getUploadFilePath(), i};
            this.jdbcTemplate.update(createFeedsImageQuery, createFeedsImageParams);
        }
    }

    // 이야기방, 일기장 게시글 수정
    public int editFeeds(int boardType, PatchFeedsReq patchFeedsReq) {
        String editFeedsQuery = null;
        switch(boardType) {
            case 1: // 이야기방 수정
                editFeedsQuery = "update Story_feed set story_title=?, story_content=?, story_image=? where story_idx=?";
                break;
            case 2: // 일기장 수정
                editFeedsQuery = "update Diary_feed set diary_title=?, diary_content=?, diary_image=? where diary_idx=?";
                break;
        }

        Object[] editFeedsParams = new Object[] {patchFeedsReq.getTitle(), patchFeedsReq.getContent(), 
            patchFeedsReq.getImgCnt(), patchFeedsReq.getFeedsIdx()};
        this.jdbcTemplate.update(editFeedsQuery, editFeedsParams);
        
        return patchFeedsReq.getFeedsIdx();
    }

    // 이야기방, 일기장 이미지 정보 수정
    public void editFeedsImage(List<Image> img, int feedsIdx) {
        String editFeedsImageQuery = "update Image_url set image_url=?, image_order=? where content_category=? & content_idx=?";
        Object[] editFeedsImageParams;
        for(int i = 0; i < img.size(); i++) {
            editFeedsImageParams = new Object[] {img.get(i).getUploadFilePath(), i, img.get(i).getCategory(), feedsIdx};
            this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
        }
    }

    // // 이야기방, 일기장 이미지 조회
    // public List<Image> getFeedsImage(int boardType, int feedsIdx) {
    //     String getFeedsImageQuery = "select * from Image_url where content_category = ? & content_idx = ?";
    //     Object[] getFeedsImageParams = new Object[] {boardType, feedsIdx};

    // }
        

}
