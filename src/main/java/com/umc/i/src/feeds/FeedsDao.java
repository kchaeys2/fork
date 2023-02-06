package com.umc.i.src.feeds;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.utils.S3Storage.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;


@Repository
public class FeedsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 이야기방, 일기장 게시글 저장
    public int createFeeds(PostFeedsReq postFeedsReq) throws BaseException{
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        String createFeedsQuery = null;

        try {
            switch(postFeedsReq.getBoardIdx()) {
                case 1: //이야기방
                    createFeedsQuery = "insert into Story_feed (board_idx, story_roomType, mem_idx, story_title, story_content, ";
                    createFeedsQuery += "story_image, story_hit, story_comment_count, story_like_count, story_blame, story_created_at)";
                    createFeedsQuery += "values (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    break;
                case 2: //일기장
                    createFeedsQuery = "insert into Diary_feed (board_idx, diary_roomType, mem_idx, diary_title, diary_content, ";
                    createFeedsQuery += "diary_image, diary_hit, diary_comment_count, diary_like_count, diary_blame, diary_created_at)";
                    createFeedsQuery += "values (2, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    break;
            }
    
            Object[] createFeedsParams = new Object[] {postFeedsReq.getRoomType(), postFeedsReq.getUserIdx(), postFeedsReq.getTitle(),
                                    postFeedsReq.getContent(), postFeedsReq.getImgCnt(), 0, 0, 0, 0, currentTime};
            this.jdbcTemplate.update(createFeedsQuery, createFeedsParams);  // 게시물 저장
    
            String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
            int feedsIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
            
            return feedsIdx;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_FEEDS_UPLOAD_FAIL);
        }
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
    public int editFeeds(PatchFeedsReq patchFeedsReq) throws BaseException {
        String editFeedsQuery = null;
        switch(patchFeedsReq.getBoardIdx()) {
            case 1: // 이야기방 수정
                editFeedsQuery = "update Story_feed set story_title=?, story_content=?, story_image=? where story_idx=?";
                break;
            case 2: // 일기장 수정
                editFeedsQuery = "update Diary_feed set diary_title=?, diary_content=?, diary_image=? where diary_idx=?";
                break;
        }

        try {
            Object[] editFeedsParams = new Object[] {patchFeedsReq.getTitle(), patchFeedsReq.getContent(), 
                patchFeedsReq.getImgCnt(), patchFeedsReq.getFeedsIdx()};
            this.jdbcTemplate.update(editFeedsQuery, editFeedsParams);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.PATCH_EDIT_FEEDS_FAIL);
        }        
        
        return patchFeedsReq.getFeedsIdx();
    }

    // 이야기방, 일기장 이미지 정보 수정
    public void editFeedsImage(List<Image> img, int boardType, int feedsIdx) {
        String editFeedsImageQuery = "update Image_url set image_url=? where content_category=? && content_idx=? && image_order=?";
        Object[] editFeedsImageParams;
        int oldImgCnt = getFeedsImage(boardType, feedsIdx).size();

        if(oldImgCnt <= img.size()) {   // 이미지 교체, 추가
            int i;
            for(i = 0; i < img.size() && i < oldImgCnt; i++) {
                editFeedsImageParams = new Object[] {img.get(i).getUploadFilePath(), boardType, feedsIdx, i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
            }
            editFeedsImageQuery = "insert into Image_url (content_category, content_idx, image_url, image_order)";
            editFeedsImageQuery += " values (?, ?, ?, ?)";
            while(i < img.size()) {
                editFeedsImageParams = new Object[] {boardType, feedsIdx, img.get(i).getUploadFilePath(), i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
                i++;
            }
        } else {    // 이미지 교체, 삭제
            int i;
            for(i = 0; i < img.size(); i++) {
                editFeedsImageParams = new Object[] {img.get(i).getUploadFilePath(), boardType, feedsIdx, i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
            }
            editFeedsImageQuery = "delete from Image_url where content_category = ? && content_idx = ? && image_order = ?";
            while(i < oldImgCnt) {
                editFeedsImageParams = new Object[] {boardType, feedsIdx, i};
                this.jdbcTemplate.update(editFeedsImageQuery, editFeedsImageParams);
                i++;
            }
        }
    }

    // 이야기방, 일기장 이미지 조회
    public List<Image> getFeedsImage(int boardType, int feedsIdx) {
        String getFeedsImageQuery = "select * from Image_url where content_category = ? && content_idx = ?";
        Object[] getFeedsImageParams = new Object[] {boardType, feedsIdx};
        
        return this.jdbcTemplate.query(getFeedsImageQuery, 
        (rs, rowNum) -> new Image(
            rs.getString("image_url"), 
            rs.getString("image_url"),
            rs.getInt("content_category"), 
            rs.getInt("content_idx")), 
            getFeedsImageParams);
    }
        

    // 이야기방, 일기장 게시글 삭제
    public void deleteFeeds(int boardType, int feedsIdx) throws BaseException {
        String deleteFeedsQuery = null;

        try {  
            // feed table 삭제
            if(boardType == 1) {    // 이야기방
                deleteFeedsQuery = "delete from Story_feed where story_idx = ?";
            } else if(boardType == 2) {     // 일기장
                deleteFeedsQuery = "delete from Diary_feed where diary_idx = ?";
            }
            this.jdbcTemplate.update(deleteFeedsQuery, feedsIdx);   

            // image_url table 삭제
            deleteFeedsQuery = "delete from Image_url where content_category = ? && content_idx = ?";
            Object[] deleteFeedsParams = new Object[] {boardType, feedsIdx};
            this.jdbcTemplate.update(deleteFeedsQuery, deleteFeedsParams);

        } catch (Exception e) {
            e.printStackTrace();
             throw new BaseException(BaseResponseStatus.PATCH_DELETE_FEEDS_FAIL);
        }
        return;
    }

}
