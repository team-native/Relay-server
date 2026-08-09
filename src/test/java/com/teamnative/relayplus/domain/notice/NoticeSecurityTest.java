package com.teamnative.relayplus.domain.notice;

import com.teamnative.relayplus.domain.notice.entity.Notice;
import com.teamnative.relayplus.domain.notice.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoticeSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoticeRepository noticeRepository;

    private Notice notice;

    @BeforeEach
    void setUp() {
        noticeRepository.deleteAll();
        notice = noticeRepository.save(Notice.builder()
                .title("Notice title")
                .content("Notice content")
                .build());
    }

    @Test
    @DisplayName("공지사항 목록은 로그인 없이 조회할 수 있다")
    void noticeListIsPublic() throws Exception {
        mockMvc.perform(get("/api/notice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notices[0].id").value(notice.getId()))
                .andExpect(jsonPath("$.notices[0].title").value("Notice title"));
    }

    @Test
    @DisplayName("공지사항 상세는 로그인 없이 조회할 수 있다")
    void noticeDetailIsPublic() throws Exception {
        mockMvc.perform(get("/api/notice/{noticeId}", notice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notice.getId()))
                .andExpect(jsonPath("$.title").value("Notice title"))
                .andExpect(jsonPath("$.content").value("Notice content"));
    }

    @Test
    @DisplayName("Notice list is public even with an invalid Authorization header")
    void noticeListIsPublicWithInvalidAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/notice")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notices[0].id").value(notice.getId()))
                .andExpect(jsonPath("$.notices[0].title").value("Notice title"));
    }

    @Test
    @DisplayName("Notice detail is public even with an invalid Authorization header")
    void noticeDetailIsPublicWithInvalidAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/notice/{noticeId}", notice.getId())
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notice.getId()))
                .andExpect(jsonPath("$.title").value("Notice title"))
                .andExpect(jsonPath("$.content").value("Notice content"));
    }
}
