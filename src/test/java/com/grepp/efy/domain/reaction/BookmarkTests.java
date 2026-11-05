package com.grepp.efy.domain.reaction;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class BookmarkTests {
    private static final LocalDateTime OLD_TIME = LocalDateTime.of(2020, 1, 1, 0, 0);
    private static final String PROFILE_NOT_FOUND = "고수 프로필을 찾을 수 없습니다.";
    private static final String BOOKMARK_NOT_FOUND = "존재하지 않는 북마크입니다.";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();
    // 500개 제한 테스트에서 한꺼번에 만든 회원의 이메일 앞부분
    private final String bulkPrefix = "bm-" + UUID.randomUUID().toString().substring(0, 8) + "-";
    private Long me;
    private Long expertMember;
    private Long profileId;

    @BeforeEach
    void setUp() {
        me = member("USER");
        expertMember = member("EXPERT");
        profileId = expertProfile(expertMember);
    }

    // 북마크 → 고수 프로필 → 회원 순으로 지운다 (외래 키)
    @AfterEach
    void cleanUp() {
        String bulk = "select id from member where email like '" + bulkPrefix + "%'";
        jdbcTemplate.update("delete from bookmark where expert_profile_id in (select id from expert_profile where member_id in (" + bulk + "))");
        jdbcTemplate.update("delete from expert_profile where member_id in (" + bulk + ")");
        jdbcTemplate.update("delete from member where email like '" + bulkPrefix + "%'");
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from bookmark where member_id = ?", id);
            jdbcTemplate.update("delete from bookmark where expert_profile_id in (select id from expert_profile where member_id = ?)", id);
        }
        for (Long id : memberIds) {
            String profile = "(select id from expert_profile where member_id = " + id + ")";
            jdbcTemplate.update("delete from expert_profile_whole_regions where expert_profile_id in " + profile);
            jdbcTemplate.update("delete from expert_profile_regions where expert_profile_id in " + profile);
            jdbcTemplate.update("delete from expert_profile where member_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    // ---------- 공개 고수 목록의 찜 수 ----------

    @Test
    void 공개_고수_목록은_활성_찜의_누적과_최근7일_수를_주고_없으면_0() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Long oldFan = member("USER");
        Long canceledFan = member("USER");
        Long withdrawnFan = member("USER");
        bookmark(me, profileId, true, now.minusDays(1));                // 누적·최근 7일
        bookmark(oldFan, profileId, true, now.minusDays(30));           // 누적만
        bookmark(canceledFan, profileId, false, now.minusDays(1));      // 취소: 제외
        bookmark(withdrawnFan, profileId, true, now.minusDays(1));      // 탈퇴 회원: 제외
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", withdrawnFan);
        Long noBookmarks = expertProfile(member("EXPERT"));

        String entry = "$[?(@.id == " + profileId + ")]";
        String empty = "$[?(@.id == " + noBookmarks + ")]";
        mockMvc.perform(get("/api/public/expert-profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(entry + ".bookmarkCount").value(contains(2)))
                .andExpect(jsonPath(entry + ".weeklyBookmarkCount").value(contains(1)))
                .andExpect(jsonPath(empty + ".bookmarkCount").value(contains(0)))
                .andExpect(jsonPath(empty + ".weeklyBookmarkCount").value(contains(0)));
    }

    // ---------- 목록 ----------

    @Test
    void 목록은_활성_북마크만_최신순으로_주고_취소나_탈퇴한_고수는_뺀다() throws Exception {
        Long older = expertProfile(member("EXPERT"));
        Long canceled = expertProfile(member("EXPERT"));
        Long withdrawnMember = member("EXPERT");
        Long withdrawn = expertProfile(withdrawnMember);
        bookmark(me, older, true, LocalDateTime.of(2026, 1, 1, 0, 0));
        bookmark(me, profileId, true, LocalDateTime.of(2026, 2, 1, 0, 0));
        bookmark(me, canceled, false, LocalDateTime.of(2026, 3, 1, 0, 0));
        bookmark(me, withdrawn, true, LocalDateTime.of(2026, 4, 1, 0, 0));
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", withdrawnMember);
        // 다른 회원의 북마크는 안 보인다
        bookmark(expertMember, older, true, LocalDateTime.of(2026, 5, 1, 0, 0));

        list(me, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].expertProfileId").value(profileId.intValue()))
                .andExpect(jsonPath("$.content[1].expertProfileId").value(older.intValue()))
                .andExpect(jsonPath("$.lastPage").value(true));
    }

    @Test
    void 목록_항목에는_대표_카테고리와_활동_지역이_모두_담기고_리뷰는_null_0이다() throws Exception {
        // 시/도 전체: 서울(1), 개별 시/군/구: 강남구(39)·중구(18)
        jdbcTemplate.update("insert into expert_profile_whole_regions(expert_profile_id, whole_regions_id) values (?, 1)", profileId);
        jdbcTemplate.update("insert into expert_profile_regions(expert_profile_id, regions_id) values (?, 39), (?, 18)", profileId, profileId);
        bookmark(me, profileId, true, LocalDateTime.of(2026, 2, 1, 0, 0));

        list(me, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].expertNickname").exists())
                .andExpect(jsonPath("$.content[0].expertProfileImageUrl").value(nullValue()))
                .andExpect(jsonPath("$.content[0].averageRating").value(nullValue()))
                .andExpect(jsonPath("$.content[0].reviewCount").value(0))
                .andExpect(jsonPath("$.content[0].category.secondCategoryId").value(101))
                .andExpect(jsonPath("$.content[0].category.firstCategoryName").exists())
                .andExpect(jsonPath("$.content[0].nationwide").value(false))
                .andExpect(jsonPath("$.content[0].wholeRegions[0].firstRegionId").value(1))
                .andExpect(jsonPath("$.content[0].regions.length()").value(2))
                .andExpect(jsonPath("$.content[0].regions[0].secondRegionId").value(18))
                .andExpect(jsonPath("$.content[0].regions[1].secondRegionId").value(39))
                .andExpect(jsonPath("$.content[0].regions[1].firstRegionId").value(1))
                .andExpect(jsonPath("$.content[0].bookmarkedAt").exists());
    }

    @Test
    void 북마크가_없거나_범위를_넘으면_빈_목록이고_잘못된_pageNumber는_400이다() throws Exception {
        list(me, 1).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());

        bookmark(me, profileId, true, LocalDateTime.now());
        list(me, 2).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());

        list(me, 0).andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/members/me/bookmarks")).andExpect(status().isUnauthorized());
    }

    // ---------- 등록 ----------

    @Test
    void 등록하면_201이고_활성_기록이_생긴다() throws Exception {
        create(me, profileId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.expertProfileId").value(profileId.intValue()))
                .andExpect(jsonPath("$.bookmarked").value(true))
                .andExpect(jsonPath("$.bookmarkedAt").exists());

        assertTrue(isActive(me, profileId));
    }

    @Test
    void 자신의_고수_프로필도_북마크할_수_있다() throws Exception {
        create(expertMember, profileId).andExpect(status().isCreated());
    }

    @Test
    void 이미_북마크했으면_409다() throws Exception {
        create(me, profileId).andExpect(status().isCreated());

        create(me, profileId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 북마크한 고수입니다."));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from bookmark where member_id = ?", Integer.class, me));
    }

    @Test
    void 취소한_기록이_있으면_새로_만들지_않고_되살리며_bookmarkedAt을_갱신한다() throws Exception {
        jdbcTemplate.update("insert into bookmark(member_id, expert_profile_id, is_active, bookmarked_at) values (?, ?, false, ?)",
                me, profileId, Timestamp.valueOf(OLD_TIME));

        create(me, profileId).andExpect(status().isCreated()).andExpect(jsonPath("$.bookmarked").value(true));

        assertTrue(isActive(me, profileId));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from bookmark where member_id = ?", Integer.class, me));
        assertNotEquals(Timestamp.valueOf(OLD_TIME),
                jdbcTemplate.queryForObject("select bookmarked_at from bookmark where member_id = ?", Timestamp.class, me));
    }

    @Test
    void 없는_고수_프로필이나_탈퇴한_고수는_404다() throws Exception {
        create(me, 999999999L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(PROFILE_NOT_FOUND));

        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", expertMember);
        create(me, profileId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(PROFILE_NOT_FOUND));
    }

    @Test
    void 활성_북마크가_500개면_409이고_탈퇴한_고수의_북마크는_세지_않는다() throws Exception {
        bulkBookmarks(me, 500);

        create(me, profileId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("북마크는 최대 500개까지 등록할 수 있습니다."));

        // 이미 북마크한 고수는 개수보다 중복 오류가 먼저
        Long anyBulk = jdbcTemplate.queryForObject(
                "select min(ep.id) from expert_profile ep join member m on ep.member_id = m.id where m.email like '" + bulkPrefix + "%'", Long.class);
        create(me, anyBulk).andExpect(jsonPath("$.message").value("이미 북마크한 고수입니다."));

        // 그중 한 고수가 탈퇴하면 499개로 세어 등록 가능
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = (select member_id from expert_profile where id = ?)", anyBulk);
        create(me, profileId).andExpect(status().isCreated());
    }

    // ---------- 취소 ----------

    @Test
    void 취소하면_200이고_기록은_비활성으로_남으며_다시_등록할_수_있다() throws Exception {
        create(me, profileId);

        cancel(me, profileId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expertProfileId").value(profileId.intValue()))
                .andExpect(jsonPath("$.bookmarked").value(false));
        assertFalse(isActive(me, profileId));

        create(me, profileId).andExpect(status().isCreated());
        assertTrue(isActive(me, profileId));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from bookmark where member_id = ?", Integer.class, me));
    }

    @Test
    void 북마크하지_않았거나_이미_취소했으면_404다() throws Exception {
        cancel(me, profileId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(BOOKMARK_NOT_FOUND));

        create(me, profileId);
        cancel(me, profileId).andExpect(status().isOk());
        cancel(me, profileId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(BOOKMARK_NOT_FOUND));
    }

    @Test
    void 없는_고수_프로필이나_탈퇴한_고수의_북마크는_취소할_수_없다() throws Exception {
        cancel(me, 999999999L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(PROFILE_NOT_FOUND));

        create(me, profileId);
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", expertMember);
        cancel(me, profileId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(PROFILE_NOT_FOUND));
        assertTrue(isActive(me, profileId));
    }

    @Test
    void 잘못된_id는_400_토큰이_없으면_401이다() throws Exception {
        cancel(me, 0L).andExpect(status().isBadRequest());
        mockMvc.perform(delete(url(profileId))).andExpect(status().isUnauthorized());
        create(me, 0L)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(post("/api/members/me/bookmarks/abc").header("Authorization", "Bearer " + token(me)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
        mockMvc.perform(post(url(profileId))).andExpect(status().isUnauthorized());
    }

    private ResultActions create(Long memberId, Long expertProfileId) throws Exception {
        return mockMvc.perform(post(url(expertProfileId)).header("Authorization", "Bearer " + token(memberId)));
    }

    private ResultActions list(Long memberId, int pageNumber) throws Exception {
        return mockMvc.perform(get("/api/members/me/bookmarks")
                .param("pageNumber", String.valueOf(pageNumber))
                .header("Authorization", "Bearer " + token(memberId)));
    }

    private void bookmark(Long memberId, Long expertProfileId, boolean active, LocalDateTime at) {
        jdbcTemplate.update("insert into bookmark(member_id, expert_profile_id, is_active, bookmarked_at) values (?, ?, ?, ?)",
                memberId, expertProfileId, active, Timestamp.valueOf(at));
    }

    private ResultActions cancel(Long memberId, Long expertProfileId) throws Exception {
        return mockMvc.perform(delete(url(expertProfileId)).header("Authorization", "Bearer " + token(memberId)));
    }

    private static String url(Long expertProfileId) {
        return "/api/members/me/bookmarks/" + expertProfileId;
    }

    private boolean isActive(Long memberId, Long expertProfileId) {
        return jdbcTemplate.queryForObject("select is_active from bookmark where member_id = ? and expert_profile_id = ?",
                Boolean.class, memberId, expertProfileId);
    }

    // 고수 count명을 만들고 memberId가 모두 북마크한 상태로 만든다
    private void bulkBookmarks(Long memberId, int count) {
        Long base = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        jdbcTemplate.batchUpdate(
                "insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, 'EXPERT', CURRENT_TIMESTAMP, 0)",
                IntStream.range(0, count).mapToObj(i -> new Object[]{
                        bulkPrefix + i + "@test.local", bulkPrefix + i, String.format("9%010d", base + i)}).toList());
        jdbcTemplate.update("""
                insert into expert_profile(member_id, short_intro, description, primary_category_id, is_nationwide)
                select id, '소개', '설명', 101, false from member where email like ?
                """, bulkPrefix + "%");
        jdbcTemplate.update("""
                insert into bookmark(member_id, expert_profile_id, is_active, bookmarked_at)
                select ?, ep.id, true, CURRENT_TIMESTAMP from expert_profile ep join member m on ep.member_id = m.id
                where m.email like ?
                """, memberId, bulkPrefix + "%");
    }

    private Long expertProfile(Long memberId) {
        jdbcTemplate.update("""
                insert into expert_profile(member_id, short_intro, description, primary_category_id, is_nationwide)
                values (?, '한 줄 소개', '서비스 설명', 101, false)
                """, memberId);
        return jdbcTemplate.queryForObject("select id from expert_profile where member_id = ?", Long.class, memberId);
    }

    // member(), token()은 MemberImageTests에서 그대로 복사
    private Long member(String role) {
        String suffix = UUID.randomUUID().toString();
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String email = suffix + "@test.local";
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)",
                email, suffix.substring(0, 20), String.format("%011d", nextId), role);
        Long id = jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
        memberIds.add(id);
        return id;
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }
}
