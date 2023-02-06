package com.umc.i.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ValidationRegex {
    // 이메일 형식 체크
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 핸드폰 번호 형식 체크
    public static boolean isRegexPhone(String target) {
        Pattern pattern = Pattern.compile("\\d{11}");
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    //특수문자 형식 체크
    public static boolean isRegexNick(String target){
        String regex = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|\\s]*$";
        if (!Pattern.matches(regex, target)) {
            return true;
        }
        return false;
    }

    public static boolean isRegexPw(String target){
        Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$"); //8자 영문+특문+숫자
        Matcher passMatcher = passPattern1.matcher(target);

        if (!passMatcher.find()) {
            return true;
        }
        return false;
    }

}
