package com.grepp.efy.domain.basedata;

import com.grepp.efy.domain.basedata.firstcategory.*;
import com.grepp.efy.domain.basedata.firstcategory.dto.FirstCategoryRequest;
import com.grepp.efy.domain.basedata.secondcategory.*;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryCreateRequest;
import com.grepp.efy.domain.basedata.secondcategory.dto.SecondCategoryRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class CategoryNameTests {
    @Autowired FirstCategoryService firstService;
    @Autowired SecondCategoryService secondService;
    @Autowired JdbcTemplate jdbcTemplate;
    @MockitoSpyBean FirstCategoryRepository firstRepository;
    @MockitoSpyBean SecondCategoryRepository secondRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String prefix = "check101-" + UUID.randomUUID().toString().substring(0, 12);

    @AfterEach
    void 정리() {
        jdbcTemplate.update("delete from second_category where name_key like ?", prefix + "%");
        jdbcTemplate.update("delete from first_category where name_key like ?", prefix + "%");
    }

    @Test
    void 대분류_동시등록은_대소문자가_달라도_하나만_저장한다() throws Exception {
        String name = prefix + "PPT";
        CountDownLatch checked = new CountDownLatch(2);
        // 두 요청이 사전 중복 검사를 모두 통과한 상황에서도 실제 DB 제약이 막는지 확인한다.
        doAnswer(invocation -> { waitForBoth(checked); return false; })
                .when(firstRepository).existsByNameKey(anyString());
        FirstCategoryRequest a = firstRequest(name);
        FirstCategoryRequest b = firstRequest(name.toLowerCase());
        runTogether(() -> firstService.createFirstCategory(a), () -> firstService.createFirstCategory(b));
        assertEquals(1, count("first_category", name));
    }

    @Test
    void 소분류_동시등록은_같은부모만_중복을_차단한다() throws Exception {
        Long parent = firstService.createFirstCategory(firstRequest(prefix + "parent")).id();
        Long otherParent = firstService.createFirstCategory(firstRequest(prefix + "other")).id();
        String name = prefix + "PPT";
        secondService.createSecondCategory(otherParent, secondCreateRequest(name));
        CountDownLatch checked = new CountDownLatch(2);
        doAnswer(invocation -> { waitForBoth(checked); return false; })
                .when(secondRepository).existsByNameKeyAndFirstCategoryId(anyString(), eq(parent));
        SecondCategoryCreateRequest a = secondCreateRequest(name);
        SecondCategoryCreateRequest b = secondCreateRequest(name.toLowerCase());
        runTogether(() -> secondService.createSecondCategory(parent, a), () -> secondService.createSecondCategory(parent, b));
        assertEquals(2, count("second_category", name));
    }

    @Test
    void 대분류_동시이름수정도_중복을_차단한다() throws Exception {
        Long a = firstService.createFirstCategory(firstRequest(prefix + "a")).id();
        Long b = firstService.createFirstCategory(firstRequest(prefix + "b")).id();
        String name = prefix + "PPT";
        CountDownLatch checked = new CountDownLatch(2);
        doAnswer(invocation -> { waitForBoth(checked); return false; })
                .when(firstRepository).existsByNameKey(anyString());
        FirstCategoryRequest requestA = firstRequest(name);
        FirstCategoryRequest requestB = firstRequest(name.toLowerCase());
        runTogether(() -> firstService.updateFirstCategory(a, requestA), () -> firstService.updateFirstCategory(b, requestB));
        assertEquals(1, count("first_category", name));
    }

    @Test
    void 소분류_동시소속이동도_중복을_차단한다() throws Exception {
        Long sourceA = firstService.createFirstCategory(firstRequest(prefix + "a")).id();
        Long sourceB = firstService.createFirstCategory(firstRequest(prefix + "b")).id();
        Long target = firstService.createFirstCategory(firstRequest(prefix + "target")).id();
        String name = prefix + "PPT";
        Long a = secondService.createSecondCategory(sourceA, secondCreateRequest(name)).id();
        Long b = secondService.createSecondCategory(sourceB, secondCreateRequest(name.toLowerCase())).id();
        CountDownLatch checked = new CountDownLatch(2);
        doAnswer(invocation -> { waitForBoth(checked); return false; })
                .when(secondRepository).existsByNameKeyAndFirstCategoryId(anyString(), eq(target));
        SecondCategoryRequest requestA = secondRequest(name, target);
        SecondCategoryRequest requestB = secondRequest(name.toLowerCase(), target);
        runTogether(() -> secondService.updateSecondCategory(a, requestA), () -> secondService.updateSecondCategory(b, requestB));
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from second_category where first_category_id=? and name_key=?", Integer.class,
                target, name.toLowerCase()));
    }

    @Test
    void 표시이름은_유지하고_수정하면_중복키도_갱신한다() throws Exception {
        String name = prefix + "PPT";
        Long id = firstService.createFirstCategory(firstRequest(name)).id();
        assertEquals(name, firstService.getFirstCategoryEntity(id).getName());
        assertEquals(name.toLowerCase(), firstService.getFirstCategoryEntity(id).getNameKey());
        String changed = prefix + "JAVA";
        firstService.updateFirstCategory(id, firstRequest(changed));
        assertEquals(changed.toLowerCase(), firstService.getFirstCategoryEntity(id).getNameKey());
        assertFalse(mapper.writeValueAsString(firstService.getFirstCategories()).contains("nameKey"));
        Long parent = firstService.createFirstCategory(firstRequest(prefix + "parent")).id();
        Long child = secondService.createSecondCategory(parent, secondCreateRequest(name)).id();
        secondService.updateSecondCategory(child, secondRequest(changed, parent));
        assertEquals(changed, secondService.getSecondCategoryEntity(child).getName());
        assertEquals(changed.toLowerCase(), secondService.getSecondCategoryEntity(child).getNameKey());
        assertFalse(mapper.writeValueAsString(secondService.getSecondCategories(parent)).contains("nameKey"));
    }

    private void waitForBoth(CountDownLatch checked) throws InterruptedException {
        checked.countDown();
        assertTrue(checked.await(10, TimeUnit.SECONDS), "두 요청의 중복 검사 대기 시간 초과");
    }

    private void runTogether(Callable<?> a, Callable<?> b) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(a);
            Future<?> second = executor.submit(b);
            int success = 0;
            int conflict = 0;
            for (Future<?> result : java.util.List.of(first, second)) {
                try {
                    result.get(20, TimeUnit.SECONDS);
                    success++;
                } catch (ExecutionException e) {
                    assertInstanceOf(DataIntegrityViolationException.class, e.getCause());
                    conflict++;
                }
            }
            assertEquals(1, success);
            assertEquals(1, conflict);
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    private int count(String table, String name) {
        return jdbcTemplate.queryForObject("select count(*) from " + table + " where name_key=?",
                Integer.class, name.toLowerCase());
    }

    private FirstCategoryRequest firstRequest(String name) throws Exception {
        return mapper.readValue("{\"name\":\"" + name + "\"}", FirstCategoryRequest.class);
    }

    private SecondCategoryCreateRequest secondCreateRequest(String name) throws Exception {
        return mapper.readValue("{\"name\":\"" + name + "\"}", SecondCategoryCreateRequest.class);
    }

    private SecondCategoryRequest secondRequest(String name, Long parent) throws Exception {
        return mapper.readValue("{\"name\":\"" + name + "\",\"firstCategoryId\":" + parent + "}", SecondCategoryRequest.class);
    }
}
