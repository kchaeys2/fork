package com.umc.i.src.member;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umc.i.src.member.model.get.GetMemRes;
import com.umc.i.src.member.model.patch.PatchMemReq;
import com.umc.i.src.member.model.post.PostJoinReq;
import com.umc.i.src.member.model.post.PostJoinRes;
import com.umc.i.utils.S3Storage.UploadImageS3;

import com.umc.i.utils.UserSha256;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.member.model.post.PostAuthRes;

import lombok.RequiredArgsConstructor;

import static com.umc.i.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private final UploadImageS3 uploadImageS3;

    private final AmazonS3 amazonS3;

    //의존성 주입 -> 필요한 객체 가져오기
    private final JavaMailSender eMailSender;
    // 타임리프를 사용하기 위한 객체
    private final SpringTemplateEngine templateEngine;

    @Autowired
    private MemberDao memberDao;
    private String authCode; //인증코드

    // 랜덤 인증코드 생성
    public void createCode() {
        Random random = new Random();
        int checkNum = random.nextInt(899999) + 100000; // 인증번호 범위: 100000 ~ 999999
        System.out.println("인증번호: " + checkNum);
        authCode = Integer.toString(checkNum);
    }

    // 메일 양식 작성
    public MimeMessage createEmailForm(String email, String authCode) throws BaseException {
        String setFrom = "amanda010926@gmail.com";  // 보내는 사람 이메일
        String toEmail = email; //받는 사람
        String title = "아이 - 아름답게 이별하는 법 본인 인증 코드";    // 메일 제목

        try {
            MimeMessage message = eMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, toEmail);     //보낼 이메일 설정
            message.setSubject(title);      // 제목 설정
            message.setFrom(setFrom);       // 보내는 이메일
            message.setText(setContext(authCode), "utf-8", "html");
            return message;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_AUTH_SEND_FAIL);
        }
    }

    // 메일 전송
    public PostAuthRes sendEmail(String toEmail) throws BaseException {
        // 메일 중복 확인
        if(memberDao.checkEmail(toEmail) == 0) {
            createCode();   // 인증코드 생성

            // 메일 전송에 필요한 정보 설정
            MimeMessage emailForm = createEmailForm(toEmail, authCode);

            // 실제 메일 전송
            eMailSender.send(emailForm);
            int autoIdx = memberDao.createAuth(1, authCode);

            return new PostAuthRes(autoIdx);
        }
        return null;
    }

    // 타임리프를 이용한 context 설정
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("signup", context);     // signup.html
    }

    // 전화번호 인증
    @SuppressWarnings("unchecked")
	public PostAuthRes send_msg(String tel) throws BaseException {
        //전화번호 중복 확인
        if(memberDao.checkPhone(tel) == 0) {
            // 인증코드 생성
            createCode();

            String hostNameUrl = "https://sens.apigw.ntruss.com";     		// 호스트 URL
            String requestUrl= "/sms/v2/services/";                   		// 요청 URL
            String requestUrlType = "/messages";                      		// 요청 URL
            String accessKey = "WmOvHZnmnkfla7ApfDhy";                     	// 개인 인증키
            String secretKey = "IPPjyEWY0gITTQVhNNs7TeeGzF9lGXs1IC2ZghAs";  // 2차 인증을 위해 서비스마다 할당되는 service secret
            String serviceId = "ncp:sms:kr:299805410270:umc-i";        		// 프로젝트에 할당된 SMS 서비스 ID
            String method = "POST";											// 요청 method
            String timestamp = Long.toString(System.currentTimeMillis()); 	// current timestamp (epoch)
            requestUrl += serviceId + requestUrlType;
            String apiUrl = hostNameUrl + requestUrl;

            // JSON 을 활용한 body data 생성
            JSONObject bodyJson = new JSONObject();
            JSONObject toJson = new JSONObject();
            JSONArray  toArr = new JSONArray();

            toJson.put("to",tel);
            toArr.add(toJson);

            bodyJson.put("type","sms");	// 메시지 Type (sms | lms)
            bodyJson.put("contentType","COMM");
            bodyJson.put("countryCode","82");
            bodyJson.put("from","");	// 발신번호 * 사전에 인증/등록된 번호만 사용할 수 있습니다.
            bodyJson.put("messages", toArr);
            bodyJson.put("content", "아이 - 아름답게 이별하는 법\n본인인증 코드는 [" + authCode + "] 입니다.");

            String body = bodyJson.toJSONString();

            try {
                URL url = new URL(apiUrl);

                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("content-type", "application/json");
                con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
                con.setRequestProperty("x-ncp-iam-access-key", accessKey);
                con.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature(requestUrl, timestamp, method, accessKey, secretKey));
                con.setRequestMethod(method);
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());

                wr.write(body.getBytes("UTF-8"));
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                BufferedReader br;
                System.out.println("responseCode" +" " + responseCode);
                if(responseCode==202) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                }

                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

            } catch (Exception e) {
                throw new BaseException(BaseResponseStatus.POST_AUTH_SEND_FAIL);
            }

            int autoIdx = memberDao.createAuth(2, authCode);

            return new PostAuthRes(autoIdx);
        }

        return null;
    }

    public static String makeSignature(String url, String timestamp, String method, String accessKey, String secretKey) throws BaseException {
        String space = " ";
        String newLine = "\n";

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey;
        String encodeBase64String;
        try {
            signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.POST_AUTH_SEND_FAIL);
        }

        return encodeBase64String;
    }


    //회원가입
    public BaseResponseStatus createMem(PostJoinReq postJoinReq, MultipartFile profile) throws BaseException {
        try {
            //중복 닉네임 확인(0,1)
            int checkNick = memberDao.checkNick(postJoinReq.getNick());
            if(checkNick != 0){return PATCH_MEMBER_NICK_DOUBLE;}

            String saveFilePath = null;
            if(!profile.getOriginalFilename().equals("basic.jpg")) {  //기본 프로필이 아니면 + 기본 프로필 사진 이름으로 변경하기
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                // 저장할 새 이름
                long time = System.currentTimeMillis();
                String originalFilename = profile.getOriginalFilename();
                String saveFileName = String.format("%d_%s", time, originalFilename.replaceAll(" ", ""));

                // 이미지 업로드
                saveFilePath = File.separator + uploadImageS3.upload(profile, fileName, saveFileName);
            }

            //암호화
            String encryptPwd = UserSha256.encrypt(postJoinReq.getPw());
            postJoinReq.setPw(encryptPwd);

            return memberDao.createMem(postJoinReq, saveFilePath);

        } catch (Exception exception) { // 회원가입 실패시
            exception.printStackTrace();
            throw new BaseException(POST_MEMBER_JOIN);
        }
    }

    //회원 정보 수정
    public BaseResponseStatus editMem(int memIdx,PatchMemReq patchMemReq,MultipartFile profile) throws BaseException, IOException {
        try {
            int editNickNum = memberDao.editNickNum(memIdx);
            if(editNickNum > 2){
                return PATCH_MEMBER_NICKNUM_OVER;
            }

            int checkNick = memberDao.checkNick(patchMemReq.getNick());
            if(checkNick != 0){return PATCH_MEMBER_NICK_DOUBLE;}

            //이미지 수정
            String saveFilePath = "";
            if(!profile.getOriginalFilename().equals("basic.jpg")) {  //기본 프로필이 아니면 + 기본 프로필 사진 이름으로 변경하기
                String fileName = "image" + File.separator + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                // 저장할 새 이름
                long time = System.currentTimeMillis();
                String originalFilename = profile.getOriginalFilename();
                String saveFileName = String.format("%d_%s", time, originalFilename.replaceAll(" ", ""));

                // 이미지 업로드
                saveFilePath = File.separator + uploadImageS3.upload(profile, fileName, saveFileName);
            }

            memberDao.editMem(memIdx,patchMemReq,saveFilePath); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            return SUCCESS;
        } catch (Exception exception) { // 인터넷 오류
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //비밀번호 변경
    public BaseResponseStatus editPw(int memIdx,String pw)throws BaseException{
        try {
            //암호화
            String encryptPwd = UserSha256.encrypt(pw);

            memberDao.editPw(encryptPwd,memIdx);
            return SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
    //유저 조회
    public GetMemRes getMem(int memIdx)throws BaseException{
        try {
            GetMemRes getMemRes = memberDao.getMem(memIdx);
            return getMemRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(INTERNET_ERROR);
        }
    }
}