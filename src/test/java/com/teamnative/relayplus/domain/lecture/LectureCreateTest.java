package com.teamnative.relayplus.domain.lecture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnative.relayplus.domain.auth.entity.Department;
import com.teamnative.relayplus.domain.auth.entity.Generation;
import com.teamnative.relayplus.domain.auth.entity.User;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
import com.teamnative.relayplus.domain.lecture.repository.LectureRepository;
import com.teamnative.relayplus.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LectureCreateTest {

    private static final String EMAIL = "s24001@gsm.hs.kr";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        lectureRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(User.builder()
                .name("test user")
                .email(EMAIL)
                .password(passwordEncoder.encode("relay1234"))
                .generation(Generation.TENTH)
                .department(Department.SW_DEVELOPMENT)
                .build());
    }

    @Test
    @DisplayName("POST /api/lectures/new creates lecture with datetime-local payload")
    void createLecture() throws Exception {
        String token = jwtTokenProvider.generateToken(EMAIL, "USER");

        mockMvc.perform(post("/api/lectures/new")
                        .header(AUTHORIZATION, "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "capacity", 20,
                                "description", "test",
                                "presenter", "이동현",
                                "scheduledAt", "2026-08-12T19:00",
                                "title", "테스트"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("테스트"))
                .andExpect(jsonPath("$.data.scheduledAt").value("2026-08-12T19:00:00"));

        assertThat(lectureRepository.count()).isEqualTo(1);
    }
}
