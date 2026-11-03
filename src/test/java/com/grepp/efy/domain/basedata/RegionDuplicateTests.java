package com.grepp.efy.domain.basedata;

import com.grepp.efy.domain.basedata.firstregion.FirstRegionService;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionController;
import com.grepp.efy.domain.basedata.firstregion.dto.FirstRegionRequest;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionService;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionController;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionCreateRequest;
import com.grepp.efy.domain.basedata.secondregion.dto.SecondRegionRequest;
import com.grepp.efy.global.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
// Controller 공통 잠금과 실제 Service 트랜잭션을 함께 검증한다. 테스트 자체에는 트랜잭션을 걸지 않는다.
class RegionDuplicateTests {
    @Autowired FirstRegionController firstController;
    @Autowired SecondRegionController secondController;
    @Autowired FirstRegionService firstService;
    @Autowired SecondRegionService secondService;
    @Autowired JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String prefix = "check110-" + UUID.randomUUID().toString().substring(0, 12);
    private final long codeBase = 9000000000L + Math.floorMod(UUID.randomUUID().getMostSignificantBits(), 900000000L);

    @AfterEach
    void 정리() {
        jdbcTemplate.update("delete from second_region where name_key like ?", prefix + "%");
        jdbcTemplate.update("delete from first_region where name_key like ?", prefix + "%");
    }

    @Test
    void 시도와시군구에_같은코드를_동시에등록하면_하나만성공한다() throws Exception {
        Long parent = firstController.createFirstRegion(firstRequest(prefix + "parent", code(1))).getBody().id();
        FirstRegionRequest first = firstRequest(prefix + "first", code(2));
        SecondRegionCreateRequest second = secondCreate(prefix + "second", code(2));
        runTogether(() -> firstController.createFirstRegion(first), () -> secondController.createSecondRegion(parent, second));
        assertEquals(1, codeCount(code(2)));
    }

    @Test
    void 시도와시군구의_코드를_동시에수정해도_하나만성공한다() throws Exception {
        Long parent = firstController.createFirstRegion(firstRequest(prefix + "parent", code(1))).getBody().id();
        Long first = firstController.createFirstRegion(firstRequest(prefix + "first", code(2))).getBody().id();
        Long second = secondController.createSecondRegion(parent, secondCreate(prefix + "second", code(3))).getBody().id();
        FirstRegionRequest a = firstRequest(prefix + "first", code(4));
        SecondRegionRequest b = secondRequest(prefix + "second", code(4), parent);
        runTogether(() -> firstController.updateFirstRegion(first, a), () -> secondController.updateSecondRegion(second, b));
        assertEquals(1, codeCount(code(4)));
    }

    @Test
    void 대소문자가다른_지역이름도_동시등록을_막는다() throws Exception {
        FirstRegionRequest a = firstRequest(prefix + "Seoul", code(1));
        FirstRegionRequest b = firstRequest(prefix + "seoul", code(2));
        runTogether(() -> firstController.createFirstRegion(a), () -> firstController.createFirstRegion(b));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from first_region where name_key=?", Integer.class, prefix + "seoul"));
        Long parent = firstController.createFirstRegion(firstRequest(prefix + "parent", code(3))).getBody().id();
        Long other = firstController.createFirstRegion(firstRequest(prefix + "other", code(4))).getBody().id();
        SecondRegionCreateRequest c = secondCreate(prefix + "Gu", code(5));
        SecondRegionCreateRequest d = secondCreate(prefix + "gu", code(6));
        runTogether(() -> secondController.createSecondRegion(parent, c), () -> secondController.createSecondRegion(parent, d));
        secondController.createSecondRegion(other, secondCreate(prefix + "Gu", code(7)));
        assertEquals(2, jdbcTemplate.queryForObject("select count(*) from second_region where name_key=?", Integer.class, prefix + "gu"));
    }

    @Test
    void 같은이름으로_동시에소속을_옮기면_하나만성공한다() throws Exception {
        Long a = firstController.createFirstRegion(firstRequest(prefix + "a", code(1))).getBody().id();
        Long b = firstController.createFirstRegion(firstRequest(prefix + "b", code(2))).getBody().id();
        Long target = firstController.createFirstRegion(firstRequest(prefix + "target", code(3))).getBody().id();
        Long first = secondController.createSecondRegion(a, secondCreate(prefix + "Gu", code(4))).getBody().id();
        Long second = secondController.createSecondRegion(b, secondCreate(prefix + "gu", code(5))).getBody().id();
        SecondRegionRequest x = secondRequest(prefix + "Gu", code(4), target);
        SecondRegionRequest y = secondRequest(prefix + "gu", code(5), target);
        runTogether(() -> secondController.updateSecondRegion(first, x), () -> secondController.updateSecondRegion(second, y));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from second_region where first_region_id=? and name_key=?", Integer.class, target, prefix + "gu"));
    }

    @Test
    void 이름수정은_표기를유지하고_키를갱신하며_응답에는키가없다() throws Exception {
        Long parent = firstController.createFirstRegion(firstRequest(prefix + "Seoul", code(1))).getBody().id();
        firstController.updateFirstRegion(parent, firstRequest(prefix + "Busan", code(1)));
        assertEquals(prefix + "Busan", firstService.getFirstRegionEntity(parent).getName());
        assertEquals(prefix + "busan", firstService.getFirstRegionEntity(parent).getNameKey());
        Long child = secondController.createSecondRegion(parent, secondCreate(prefix + "Gu", code(2))).getBody().id();
        secondController.updateSecondRegion(child, secondRequest(prefix + "NewGu", code(2), parent));
        assertEquals(prefix + "newgu", secondService.getSecondRegionEntity(child).getNameKey());
        assertFalse(mapper.writeValueAsString(firstService.getFirstRegions()).contains("nameKey"));
        assertFalse(mapper.writeValueAsString(secondService.getSecondRegions(parent)).contains("nameKey"));
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "insert into first_region(code,name,name_key) values(?,?,?)", code(3), prefix + "BUSAN", prefix + "busan"));
    }

    @Test
    void 삭제가실패해도_잠금을풀고_다른스레드에서_다음작업을_허용한다() throws Exception {
        Long parent = firstController.createFirstRegion(firstRequest(prefix + "parent", code(1))).getBody().id();
        Long child = secondController.createSecondRegion(parent, secondCreate(prefix + "child", code(2))).getBody().id();
        assertThrows(DataIntegrityViolationException.class, () -> firstController.deleteFirstRegion(parent));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(() -> firstController.createFirstRegion(firstRequest(prefix + "next", code(3)))).get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
        secondController.deleteSecondRegion(child);
        firstController.deleteFirstRegion(parent);
        firstController.createFirstRegion(firstRequest(prefix + "reuse", code(1)));
    }

    private void runTogether(Callable<?> a, Callable<?> b) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        Callable<Object> first = () -> { ready.countDown(); assertTrue(ready.await(10, TimeUnit.SECONDS)); return a.call(); };
        Callable<Object> second = () -> { ready.countDown(); assertTrue(ready.await(10, TimeUnit.SECONDS)); return b.call(); };
        try {
            Future<?> x = executor.submit(first);
            Future<?> y = executor.submit(second);
            int success = 0;
            int conflict = 0;
            for (Future<?> result : java.util.List.of(x, y)) {
                try { result.get(15, TimeUnit.SECONDS); success++; }
                catch (ExecutionException e) {
                    assertInstanceOf(BusinessException.class, e.getCause());
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

    private String code(int offset) { return Long.toString(codeBase + offset); }

    private int codeCount(String code) {
        return jdbcTemplate.queryForObject("select (select count(*) from first_region where code=?) + (select count(*) from second_region where code=?)", Integer.class, code, code);
    }

    private FirstRegionRequest firstRequest(String name, String code) throws Exception {
        return mapper.readValue("{\"name\":\"" + name + "\",\"code\":\"" + code + "\"}", FirstRegionRequest.class);
    }

    private SecondRegionCreateRequest secondCreate(String name, String code) throws Exception {
        return mapper.readValue("{\"name\":\"" + name + "\",\"code\":\"" + code + "\"}", SecondRegionCreateRequest.class);
    }

    private SecondRegionRequest secondRequest(String name, String code, Long parent) throws Exception {
        return mapper.readValue("{\"name\":\"" + name + "\",\"code\":\"" + code + "\",\"firstRegionId\":" + parent + "}", SecondRegionRequest.class);
    }
}
