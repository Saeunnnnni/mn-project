package com.project.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.exception.CustomException.PasswordUpdateException;
import com.project.exception.CustomException.TokenInvalidException;
import com.project.exception.CustomException.UserNotFoundException;
import com.project.user.dto.UserDto;
import com.project.user.service.FollowService;
import com.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    //회원관련 service
    private final UserService userService;
    //프로필 사진 출력
    @Value("${file.location}")
    private String fileLocation;
    @GetMapping(
            value = "/image/{userProfile}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public byte[] getPostImage(@PathVariable("userProfile") String imageName) throws IOException {
        String absolutePath = fileLocation + File.separator + imageName; // fileLocation은 파일 저장 위치
        InputStream is = new FileInputStream(absolutePath);
        return IOUtils.toByteArray(is); // Apache Commons IO 라이브러리의 IOUtils 사용
    }

    //회원정보수정 메서드
    @PostMapping(value = "/updateuser", consumes = {"multipart/form-data"})
    public ResponseEntity<String> updateUser(@ModelAttribute UserDto userDto) {
        try {
            String result = userService.updateUser(userDto);
            return ResponseEntity.ok(result);  // 정상적인 경우 200 OK 상태 코드와 함께 반환
        } catch (IllegalAccessException e) {
            // 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");  // 예외 발생시 500 Internal Server Error 상태 코드와 함께 반환
        }
    }

    //회원 탈퇴 메서드
    @PostMapping("/deactivate")
    public ResponseEntity<String> deactivate(@RequestParam int userNum){
        System.out.println("userNum: " + userNum);
        userService.deactivateUser(userNum);
        return new ResponseEntity<>("회원 탈퇴가 성공적으로 처리되었습니다", HttpStatus.OK);
    }

    //회원 소생 메서드
    @PostMapping("/active")
    public ResponseEntity<String> active(@RequestParam int userNum){
        System.out.println("userNum: " + userNum);
        userService.userActive(userNum);
        return new ResponseEntity<>("회원 복구가 성공적으로 처리되었습니다", HttpStatus.OK);
    }


    //비밀번호 변경 메서드
    @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody UserDto userDto) {
        String message = userService.updatePassword(userDto);
        return ResponseEntity.ok(message);
    }

    //회원의 프로필사진과 한 줄 소개 조회 메서드
    @GetMapping("/profile/{userEmail}")
    public ResponseEntity<UserDto> getUserProfileAndIntroduction(@PathVariable String userEmail) {
        UserDto userDto = userService.getUserProfileAndIntroduction(userEmail);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    //모든 회원정보 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getUserList() {
        List<UserDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


}