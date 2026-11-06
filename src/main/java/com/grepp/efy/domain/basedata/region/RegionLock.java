package com.grepp.efy.domain.basedata.region;

import org.springframework.stereotype.Component;

// 시도·시군구 Controller가 같은 객체에 synchronized를 걸도록 하나의 Bean을 공유한다.
// shortcut: 단일 서버의 Controller 경로만 보호한다. 서버 확장·다른 저장 경로 추가 전 DB 잠금 등을 적용해야 한다.
@Component
public class RegionLock {
}
