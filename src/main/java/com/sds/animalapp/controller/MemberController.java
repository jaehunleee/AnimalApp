package com.sds.animalapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sds.animalapp.domain.AdoptAnimal;
import com.sds.animalapp.domain.InterestAnimal;
import com.sds.animalapp.domain.InterestShelter;
import com.sds.animalapp.domain.Member;
import com.sds.animalapp.domain.MemberDetail;
import com.sds.animalapp.domain.Sns;
import com.sds.animalapp.domain.VolunteerApplication;
import com.sds.animalapp.model.animal.AdoptAnimalService;
import com.sds.animalapp.model.animal.InterestAnimalService;
import com.sds.animalapp.model.member.KakaoLoginService;
import com.sds.animalapp.model.member.MemberService;
import com.sds.animalapp.model.member.RoleService;
import com.sds.animalapp.model.member.SnsService;
import com.sds.animalapp.model.shelter.InterestShelterService;
import com.sds.animalapp.model.volunteer.VolunteerApplicationService;
import com.sds.animalapp.model.volunteer.VolunteerService;
import com.sds.animalapp.sns.KaKaoOAuthToken;
import com.sds.animalapp.sns.NaverLogin;
import com.sds.animalapp.sns.NaverOAuthToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {

    @Autowired
    private KakaoLoginService kakaoLoginService;

    @Autowired
    private NaverLogin naverLogin;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SnsService snsService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private VolunteerApplicationService volunteerApplicationService;
    
    @Autowired
    private VolunteerService volunteerService;
    
    @Autowired
    private AdoptAnimalService adoptAnimalService;
    
    @Autowired
    private InterestAnimalService interestAnimalService;
    
    @Autowired 
    private InterestShelterService interestShelterService;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @GetMapping("/member/login")
    public String getLoginForm() {
        return "member/login";
    }

 
    @GetMapping("/member/mypage")
    public String getMyPage(Model model, HttpSession session) {
        // 로그인 값이 없음 못하게
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";
        }

        // Volunteer Applications
        List<VolunteerApplication> volunteerApplications = volunteerApplicationService.getApplicationsByMemberIdx(member.getMember_idx());
        model.addAttribute("volunteerApplications", volunteerApplications);
        
        // Adopt Animals
        List<AdoptAnimal> adoptAnimals = adoptAnimalService.getAdoptByMemberIdx(member.getMember_idx());
        model.addAttribute("adoptAnimals", adoptAnimals);
       
        // Interest Animals 
        List<InterestAnimal> interestAnimals =  interestAnimalService.getInterestByMemberIdx(member.getMember_idx());
        model.addAttribute("interestAnimals", interestAnimals);
        
        // 	Interest Shelters 
        List<InterestShelter> interestShelters =  interestShelterService.getInterestByMemberIdx(member.getMember_idx());
        model.addAttribute("interestShelters", interestShelters);
        
        return "member/mypage";
    }


    
    // 해결 방법 찾음 application.properties에 
    // upload.directory=C:/git_animal/AnimalApp/bin/main/static/mypage 이렇게 자기 
    // 컴퓨터 경로에 맞게 설정해줘야함
    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> pictureFileInput(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                // byte로 읽어오기
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadDirectory, file.getOriginalFilename());

                if (Files.notExists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }

                Files.write(path, bytes);
                String imageUrl = "/mypage/" + file.getOriginalFilename();
                response.put("success", true);
                response.put("imageUrl", imageUrl);

                System.out.println("Image uploaded to: " + path.toString());
                System.out.println("Image URL: " + imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
                response.put("success", false);
                response.put("message", "파일 업로드에 실패했습니다.");
            }
        } else {
            response.put("success", false);
            response.put("message", "업로드할 파일을 선택해주세요.");
        }
        return response;
    }

    // 카카오 로그인 처리
    @GetMapping("/member/sns/kakao/callback")
    public ModelAndView kakaoCallback(HttpServletRequest request) {
        KaKaoOAuthToken oAuthToken = kakaoLoginService.getKakaoAccessToken(request);

        Member dto = kakaoLoginService.registMember(oAuthToken);

        kakaoLoginService.authenticateUser(dto, request);

        ModelAndView mav = new ModelAndView("redirect:/");
        return mav;
    }

    //네이버 로그인 처리
    @GetMapping("/member/sns/naver/callback")
    public ModelAndView naverCallback(HttpServletRequest request, HttpSession session) {
        String code = request.getParameter("code");

        String token_url = naverLogin.getToken_request_url();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", naverLogin.getClient_id());
        params.add("client_secret", naverLogin.getClient_secret());
        params.add("redirect_uri", naverLogin.getRedirect_uri());
        params.add("grant_type", naverLogin.getGrant_type());
        params.add("state", naverLogin.getState());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(token_url, HttpMethod.POST, entity, String.class);

        String body = responseEntity.getBody();
        log.info("네이버가 보낸 인증 완료 정보는 " + body);

        ObjectMapper objectMapper = new ObjectMapper();
        NaverOAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(body, NaverOAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String userinfo_url = naverLogin.getUserinfo_url();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
        HttpEntity<?> entity2 = new HttpEntity<>(headers2);

        ResponseEntity<String> userEntity = restTemplate.exchange(userinfo_url, HttpMethod.GET, entity2, String.class);
        String userBody = userEntity.getBody();
        log.info(userBody);

        ObjectMapper objectMapper2 = new ObjectMapper();
        HashMap<String, Object> userMap = null;

        try {
            userMap = objectMapper2.readValue(userBody, HashMap.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Map<String, Object> response = (Map<String, Object>) userMap.get("response");

        log.debug("response map: " + response);

        String id = (String) response.get("id");
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        String profile_image = (String) response.get("profile_image");

        log.debug("id = " + id);
        log.debug("email = " + email);
        log.debug("name = " + name);
        log.debug("profile_image = " + profile_image);

        Member member = new Member();
        member.setUid(id);
        member.setNickname(name);
        member.setEmail(email);
        member.setProfileImageUrl(profile_image);

        Sns naverSns = snsService.selectByName("naver");
        if (naverSns == null) {
            throw new RuntimeException("SNS 'naver' db에 없어요");
        }
        member.setSns(naverSns);
        member.setRole(roleService.selectByName("USER"));

        Member dto = memberService.selectByUid(id);

        if (dto == null) {
            memberService.regist(member);
            dto = member;
        } else {
            dto.setProfileImageUrl(profile_image);
            memberService.update(dto);
        }

        session.setAttribute("member", dto);
        log.debug("현재 가진 권한은 " + dto.getRole().getRole_name());

        Authentication auth = new UsernamePasswordAuthenticationToken(dto.getNickname(), null, Collections.singletonList(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return new ModelAndView("redirect:/member/mypage");
    }

    
    // 프로필 사진 바꾸기
    // 바꾸는법 파일 선택 -> 선택 후 static/mypage 폴더에서 사진 업로드시 변경 됨
    @PostMapping("/updateProfile")
    @ResponseBody
    public Map<String, Object> updateProfile(@RequestBody Map<String, String> payload, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String name = payload.get("name");
        String phone = payload.get("phone");

        Member member = (Member) session.getAttribute("member");
        if (member != null) {
            member.setNickname(name);
            memberService.update(member);//member 테이블 nickname 커럼값 변경

            MemberDetail memberDetail = member.getMemberDetail();
            if (memberDetail == null) {
                memberDetail = new MemberDetail();
                memberDetail.setMember_idx(member.getMember_idx());
            }
            memberDetail.setPhone(phone);
            
            if(memberService.getMemberDetailByMemberIdx(memberDetail.getMember_idx())==0) {
            	log.info(String.valueOf(memberService.getMemberDetailByMemberIdx(memberDetail.getMember_idx())) );
            	memberService.insertMemberDetail(memberDetail);
            }else {
            	memberService.updateMemberDetail(memberDetail);
            }

            session.setAttribute("member", member);
            response.put("success", true);
        } else {
            response.put("success", false);
            response.put("message", "세션이 만료되었습니다.");
        }
        return response;
    }
}