-- 카테고리 초기 데이터: 대분류 11개, 소분류 108개
-- 로컬 H2의 새 테이블에 한 번 입력하는 테스트 전용 시드입니다. 전체 스크립트 재실행은 지원하지 않습니다.
-- 기존 6개 회원·요청서 1~10·견적 1~3의 ID와 예외 검증 조건을 유지합니다.

-- 대분류
INSERT INTO first_category (id, name, name_key)
VALUES (1, '이사/청소', '이사/청소'),
       (2, '설치/수리', '설치/수리'),
       (3, '인테리어', '인테리어'),
       (4, '이벤트/뷰티', '이벤트/뷰티'),
       (5, '과외', '과외'),
       (6, '외주', '외주'),
       (7, '취업/직무', '취업/직무'),
       (8, '취미/자기계발', '취미/자기계발'),
       (9, '자동차', '자동차'),
       (10, '법률/금융', '법률/금융'),
       (11, '기타', '기타');

-- 소분류: 이사/청소
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (101, '이사', '이사', 1),
       (102, '입주/집 청소', '입주/집 청소', 1),
       (103, '가전/가구 청소', '가전/가구 청소', 1),
       (104, '사업장 청소', '사업장 청소', 1),
       (105, '특수 청소', '특수 청소', 1),
       (106, '철거/폐기', '철거/폐기', 1);

-- 소분류: 설치/수리
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (107, '가전제품', '가전제품', 2),
       (108, '문/창문', '문/창문', 2),
       (109, '수도/보일러/전기', '수도/보일러/전기', 2),
       (110, '가구', '가구', 2),
       (111, '기타 설치/수리', '기타 설치/수리', 2);

-- 소분류: 인테리어
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (112, '종합 인테리어', '종합 인테리어', 3),
       (113, '부분 인테리어', '부분 인테리어', 3),
       (114, '벽/천장 시공', '벽/천장 시공', 3),
       (115, '바닥 시공', '바닥 시공', 3),
       (116, '야외 시공', '야외 시공', 3),
       (117, '기타 시공', '기타 시공', 3);

-- 소분류: 이벤트/뷰티
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (118, '웨딩', '웨딩', 4),
       (119, '뷰티', '뷰티', 4),
       (120, '촬영 및 편집', '촬영 및 편집', 4),
       (121, '음향 및 편집', '음향 및 편집', 4),
       (122, '대여/대관', '대여/대관', 4),
       (123, '음식', '음식', 4),
       (124, '행사 준비', '행사 준비', 4),
       (125, '행사 섭외', '행사 섭외', 4),
       (126, '행사 인력', '행사 인력', 4);

-- 소분류: 과외
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (127, '국내 입시', '국내 입시', 5),
       (128, '유학 준비', '유학 준비', 5),
       (129, '영어', '영어', 5),
       (130, '제2외국어', '제2외국어', 5),
       (131, '미술', '미술', 5),
       (132, '음악', '음악', 5),
       (133, '체육', '체육', 5),
       (134, '무용/댄스', '무용/댄스', 5),
       (135, '공연', '공연', 5),
       (136, '영상/사진', '영상/사진', 5),
       (137, '아동 교육', '아동 교육', 5);

-- 소분류: 외주
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (138, '디자인 외주', '디자인 외주', 6),
       (139, '개발 외주', '개발 외주', 6),
       (140, '데이터 외주', '데이터 외주', 6),
       (141, '기타 외주', '기타 외주', 6),
       (142, '마케팅', '마케팅', 6),
       (143, '문서', '문서', 6),
       (144, '번역', '번역', 6),
       (145, '통역', '통역', 6),
       (146, '음향', '음향', 6),
       (147, '인쇄/제작', '인쇄/제작', 6),
       (148, '컨설팅', '컨설팅', 6),
       (149, '인사', '인사', 6);

-- 소분류: 취업/직무
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (150, '취업 준비', '취업 준비', 7),
       (151, '창업 준비', '창업 준비', 7),
       (152, '시험/자격증', '시험/자격증', 7),
       (153, '음식/음료', '음식/음료', 7),
       (154, '기초 학문', '기초 학문', 7),
       (155, '외국어-영어', '외국어-영어', 7),
       (156, '외국어-아시아', '외국어-아시아', 7),
       (157, '외국어-유럽', '외국어-유럽', 7),
       (158, '외국어-기타', '외국어-기타', 7),
       (159, '컴퓨터 실무', '컴퓨터 실무', 7),
       (160, '디자인 실무', '디자인 실무', 7),
       (161, '마케팅 실무', '마케팅 실무', 7),
       (162, '패션/뷰티 실무', '패션/뷰티 실무', 7),
       (163, '기타 실무', '기타 실무', 7),
       (164, '인사 관리', '인사 관리', 7);

-- 소분류: 취미/자기계발
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (165, '악기', '악기', 8),
       (166, '음악이론/보컬', '음악이론/보컬', 8),
       (167, '미술/드로잉', '미술/드로잉', 8),
       (168, '취업 준비 컨설팅', '취업 준비 컨설팅', 8),
       (169, '사진/영상', '사진/영상', 8),
       (170, '구기 스포츠', '구기 스포츠', 8),
       (171, '댄스', '댄스', 8),
       (172, '투자/N잡', '투자/n잡', 8),
       (173, '연기/마술', '연기/마술', 8),
       (174, '피트니스', '피트니스', 8),
       (175, '스포츠', '스포츠', 8),
       (176, '취미/생활', '취미/생활', 8),
       (177, '요리/조리', '요리/조리', 8),
       (178, '공예', '공예', 8),
       (179, '계절 스포츠', '계절 스포츠', 8),
       (180, '패션/미용', '패션/미용', 8),
       (181, '격투 스포츠', '격투 스포츠', 8),
       (182, '국악', '국악', 8),
       (183, '기타 취미/자기계발', '기타 취미/자기계발', 8);

-- 소분류: 자동차
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (184, '차량 관리', '차량 관리', 9),
       (185, '자동차 매매', '자동차 매매', 9);

-- 소분류: 법률/금융
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (186, '금융', '금융', 10),
       (187, '법률', '법률', 10),
       (188, '부동산', '부동산', 10);

-- 소분류: 기타
INSERT INTO second_category (id, name, name_key, first_category_id)
VALUES (189, '렌탈', '렌탈', 11),
       (190, '심리', '심리', 11),
       (191, '펫/반려동물', '펫/반려동물', 11),
       (192, '공예 제작', '공예 제작', 11),
       (193, '번역 작업', '번역 작업', 11),
       (194, '통역 작업', '통역 작업', 11),
       (195, '의류/잡화', '의류/잡화', 11),
       (196, '여행', '여행', 11),
       (197, '심부름', '심부름', 11),
       (198, '도우미', '도우미', 11),
       (199, '알바-운전·배달·유통', '알바-운전·배달·유통', 11),
       (200, '알바-서빙·주방', '알바-서빙·주방', 11),
       (201, '알바-서비스·행사', '알바-서비스·행사', 11),
       (202, '알바-생산·기능·노무', '알바-생산·기능·노무', 11),
       (203, '알바-매장관리·판매', '알바-매장관리·판매', 11),
       (204, '알바-사무직', '알바-사무직', 11),
       (205, '알바-방송·미디어', '알바-방송·미디어', 11),
       (206, '알바-문화·여가·생활', '알바-문화·여가·생활', 11),
       (207, '알바-병원·간호·연구', '알바-병원·간호·연구', 11),
       (208, '알바-그 외', '알바-그 외', 11);

-- 로컬 테스트 회원: 공통 비밀번호 1234 (BCrypt 암호화)
-- 기존 6개 회원의 입력 순서와 ID를 유지합니다. expert3만 프로필 없는 예외 검증 계정입니다.

INSERT INTO member (email, password, nickname, phone, role, created_at, token_version)
VALUES ('customer1@example.com',
        '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C',
        '고객하나', '01012345678', 'USER', CURRENT_TIMESTAMP, 0),

       ('customer2@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C',
        '고객둘', '01056789012', 'USER', CURRENT_TIMESTAMP, 0),

       ('expert1@example.com',
        '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C',
        '매칭고수', '01023456789', 'EXPERT', CURRENT_TIMESTAMP, 0),

       ('expert2@example.com',
        '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C',
        '미매칭고수', '01045678901', 'EXPERT', CURRENT_TIMESTAMP, 0),

       ('admin1@example.com',
        '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C',
        '관리자', '01034567890', 'ADMIN', CURRENT_TIMESTAMP, 0),

       ('expert3@example.com',
        '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C',
        '프로필없는고수', '01067890123', 'EXPERT', CURRENT_TIMESTAMP, 0);

-- 기존 고수 프로필: expert1 이사·청소 / expert2 개발 외주·이사. expert3는 프로필 없음.
INSERT INTO expert_profile (member_id,
                            short_intro,
                            description,
                            career_years,
                            career_description,
                            primary_category_id,
                            is_nationwide)
VALUES ((SELECT id FROM member WHERE email = 'expert1@example.com'),
        '원룸 이사 전문 고수입니다.',
        '강남구에서 원룸 이사와 소형 이사 서비스를 제공합니다.',
        5,
        '이사 서비스 경력 5년',
        101,
        FALSE),
       ((SELECT id FROM member WHERE email = 'expert2@example.com'),
        '백엔드 개발 전문 고수입니다.',
        '서초구에서 Spring Boot 기반 개발 외주 서비스를 제공합니다.',
        3,
        'Java/Spring 백엔드 개발 경력 3년',
        139,
        FALSE);

-- 기존 고수 제공 서비스: expert1 이사(101)·입주 청소(102), expert2 개발 외주(139)·이사(101)
INSERT INTO expert_profile_services (expert_profile_id, services_id)
VALUES ((SELECT ep.id
         FROM expert_profile ep
                  JOIN member m ON ep.member_id = m.id
         WHERE m.email = 'expert1@example.com'), 101),
       ((SELECT ep.id
         FROM expert_profile ep
                  JOIN member m ON ep.member_id = m.id
         WHERE m.email = 'expert1@example.com'), 102),
       ((SELECT ep.id
         FROM expert_profile ep
                  JOIN member m ON ep.member_id = m.id
         WHERE m.email = 'expert2@example.com'), 139),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id
         WHERE m.email = 'expert2@example.com'), 101);

-- 지역 초기 데이터
-- 출처: https://www.code.go.kr/stdcode/regCodeL.do
-- 다운로드: 2026-10-26, 원본의 존재 항목만 사용
-- 시·도 및 시·군·구를 2단계로 연결합니다. 일반구에는 시 이름을 보존합니다.
-- 세종은 하위 시·군·구 없이 최상위 항목으로 저장합니다.
-- code 컬럼에는 공식 10자리 코드를 문자열로 보존합니다.
INSERT INTO first_region (id, code, name, name_key)
VALUES (1, '1100000000', '서울특별시', '서울특별시'),
       (2, '1200000000', '전남광주통합특별시', '전남광주통합특별시'),
       (3, '2600000000', '부산광역시', '부산광역시'),
       (4, '2700000000', '대구광역시', '대구광역시'),
       (5, '2800000000', '인천광역시', '인천광역시'),
       (6, '3000000000', '대전광역시', '대전광역시'),
       (7, '3100000000', '울산광역시', '울산광역시'),
       (8, '3611000000', '세종특별자치시', '세종특별자치시'),
       (9, '4100000000', '경기도', '경기도'),
       (10, '4300000000', '충청북도', '충청북도'),
       (11, '4400000000', '충청남도', '충청남도'),
       (12, '4700000000', '경상북도', '경상북도'),
       (13, '4800000000', '경상남도', '경상남도'),
       (14, '5000000000', '제주특별자치도', '제주특별자치도'),
       (15, '5100000000', '강원특별자치도', '강원특별자치도'),
       (16, '5200000000', '전북특별자치도', '전북특별자치도');

INSERT INTO second_region (id, code, name, name_key, first_region_id)
VALUES (17, '1111000000', '종로구', '종로구', 1),
       (18, '1114000000', '중구', '중구', 1),
       (19, '1117000000', '용산구', '용산구', 1),
       (20, '1120000000', '성동구', '성동구', 1),
       (21, '1121500000', '광진구', '광진구', 1),
       (22, '1123000000', '동대문구', '동대문구', 1),
       (23, '1126000000', '중랑구', '중랑구', 1),
       (24, '1129000000', '성북구', '성북구', 1),
       (25, '1130500000', '강북구', '강북구', 1),
       (26, '1132000000', '도봉구', '도봉구', 1),
       (27, '1135000000', '노원구', '노원구', 1),
       (28, '1138000000', '은평구', '은평구', 1),
       (29, '1141000000', '서대문구', '서대문구', 1),
       (30, '1144000000', '마포구', '마포구', 1),
       (31, '1147000000', '양천구', '양천구', 1),
       (32, '1150000000', '강서구', '강서구', 1),
       (33, '1153000000', '구로구', '구로구', 1),
       (34, '1154500000', '금천구', '금천구', 1),
       (35, '1156000000', '영등포구', '영등포구', 1),
       (36, '1159000000', '동작구', '동작구', 1),
       (37, '1162000000', '관악구', '관악구', 1),
       (38, '1165000000', '서초구', '서초구', 1),
       (39, '1168000000', '강남구', '강남구', 1),
       (40, '1171000000', '송파구', '송파구', 1),
       (41, '1174000000', '강동구', '강동구', 1),
       (42, '1211000000', '목포시', '목포시', 2),
       (43, '1213000000', '여수시', '여수시', 2),
       (44, '1215000000', '순천시', '순천시', 2),
       (45, '1217000000', '나주시', '나주시', 2),
       (46, '1219000000', '광양시', '광양시', 2),
       (47, '1221000000', '동구', '동구', 2),
       (48, '1224000000', '서구', '서구', 2),
       (49, '1227000000', '남구', '남구', 2),
       (50, '1230000000', '북구', '북구', 2),
       (51, '1233000000', '광산구', '광산구', 2),
       (52, '1271000000', '담양군', '담양군', 2),
       (53, '1272000000', '곡성군', '곡성군', 2),
       (54, '1273000000', '구례군', '구례군', 2),
       (55, '1274000000', '고흥군', '고흥군', 2),
       (56, '1275000000', '보성군', '보성군', 2),
       (57, '1276000000', '화순군', '화순군', 2),
       (58, '1277000000', '장흥군', '장흥군', 2),
       (59, '1278000000', '강진군', '강진군', 2),
       (60, '1279000000', '해남군', '해남군', 2),
       (61, '1280000000', '영암군', '영암군', 2),
       (62, '1281000000', '무안군', '무안군', 2),
       (63, '1282000000', '함평군', '함평군', 2),
       (64, '1283000000', '영광군', '영광군', 2),
       (65, '1284000000', '장성군', '장성군', 2),
       (66, '1285000000', '완도군', '완도군', 2),
       (67, '1286000000', '진도군', '진도군', 2),
       (68, '1287000000', '신안군', '신안군', 2),
       (69, '2611000000', '중구', '중구', 3),
       (70, '2614000000', '서구', '서구', 3),
       (71, '2617000000', '동구', '동구', 3),
       (72, '2620000000', '영도구', '영도구', 3),
       (73, '2623000000', '부산진구', '부산진구', 3),
       (74, '2626000000', '동래구', '동래구', 3),
       (75, '2629000000', '남구', '남구', 3),
       (76, '2632000000', '북구', '북구', 3),
       (77, '2635000000', '해운대구', '해운대구', 3),
       (78, '2638000000', '사하구', '사하구', 3),
       (79, '2641000000', '금정구', '금정구', 3),
       (80, '2644000000', '강서구', '강서구', 3),
       (81, '2647000000', '연제구', '연제구', 3),
       (82, '2650000000', '수영구', '수영구', 3),
       (83, '2653000000', '사상구', '사상구', 3),
       (84, '2671000000', '기장군', '기장군', 3),
       (85, '2711000000', '중구', '중구', 4),
       (86, '2714000000', '동구', '동구', 4),
       (87, '2717000000', '서구', '서구', 4),
       (88, '2720000000', '남구', '남구', 4),
       (89, '2723000000', '북구', '북구', 4),
       (90, '2726000000', '수성구', '수성구', 4),
       (91, '2729000000', '달서구', '달서구', 4),
       (92, '2771000000', '달성군', '달성군', 4),
       (93, '2772000000', '군위군', '군위군', 4),
       (94, '2812500000', '제물포구', '제물포구', 5),
       (95, '2815500000', '영종구', '영종구', 5),
       (96, '2817700000', '미추홀구', '미추홀구', 5),
       (97, '2818500000', '연수구', '연수구', 5),
       (98, '2820000000', '남동구', '남동구', 5),
       (99, '2823700000', '부평구', '부평구', 5),
       (100, '2824500000', '계양구', '계양구', 5),
       (101, '2827500000', '서해구', '서해구', 5),
       (102, '2829000000', '검단구', '검단구', 5),
       (103, '2871000000', '강화군', '강화군', 5),
       (104, '2872000000', '옹진군', '옹진군', 5),
       (105, '3011000000', '동구', '동구', 6),
       (106, '3014000000', '중구', '중구', 6),
       (107, '3017000000', '서구', '서구', 6),
       (108, '3020000000', '유성구', '유성구', 6),
       (109, '3023000000', '대덕구', '대덕구', 6),
       (110, '3111000000', '중구', '중구', 7),
       (111, '3114000000', '남구', '남구', 7),
       (112, '3117000000', '동구', '동구', 7),
       (113, '3120000000', '북구', '북구', 7),
       (114, '3171000000', '울주군', '울주군', 7),
       (115, '4111000000', '수원시', '수원시', 9),
       (116, '4111100000', '수원시 장안구', '수원시 장안구', 9),
       (117, '4111300000', '수원시 권선구', '수원시 권선구', 9),
       (118, '4111500000', '수원시 팔달구', '수원시 팔달구', 9),
       (119, '4111700000', '수원시 영통구', '수원시 영통구', 9),
       (120, '4113000000', '성남시', '성남시', 9),
       (121, '4113100000', '성남시 수정구', '성남시 수정구', 9),
       (122, '4113300000', '성남시 중원구', '성남시 중원구', 9),
       (123, '4113500000', '성남시 분당구', '성남시 분당구', 9),
       (124, '4115000000', '의정부시', '의정부시', 9),
       (125, '4117000000', '안양시', '안양시', 9),
       (126, '4117100000', '안양시 만안구', '안양시 만안구', 9),
       (127, '4117300000', '안양시 동안구', '안양시 동안구', 9),
       (128, '4119000000', '부천시', '부천시', 9),
       (129, '4119200000', '부천시 원미구 ', '부천시 원미구 ', 9),
       (130, '4119400000', '부천시 소사구 ', '부천시 소사구 ', 9),
       (131, '4119600000', '부천시 오정구 ', '부천시 오정구 ', 9),
       (132, '4121000000', '광명시', '광명시', 9),
       (133, '4122000000', '평택시', '평택시', 9),
       (134, '4125000000', '동두천시', '동두천시', 9),
       (135, '4127000000', '안산시', '안산시', 9),
       (136, '4127100000', '안산시 상록구', '안산시 상록구', 9),
       (137, '4127300000', '안산시 단원구', '안산시 단원구', 9),
       (138, '4128000000', '고양시', '고양시', 9),
       (139, '4128100000', '고양시 덕양구', '고양시 덕양구', 9),
       (140, '4128500000', '고양시 일산동구', '고양시 일산동구', 9),
       (141, '4128700000', '고양시 일산서구', '고양시 일산서구', 9),
       (142, '4129000000', '과천시', '과천시', 9),
       (143, '4131000000', '구리시', '구리시', 9),
       (144, '4136000000', '남양주시', '남양주시', 9),
       (145, '4137000000', '오산시', '오산시', 9),
       (146, '4139000000', '시흥시', '시흥시', 9),
       (147, '4141000000', '군포시', '군포시', 9),
       (148, '4143000000', '의왕시', '의왕시', 9),
       (149, '4145000000', '하남시', '하남시', 9),
       (150, '4146000000', '용인시', '용인시', 9),
       (151, '4146100000', '용인시 처인구', '용인시 처인구', 9),
       (152, '4146300000', '용인시 기흥구', '용인시 기흥구', 9),
       (153, '4146500000', '용인시 수지구', '용인시 수지구', 9),
       (154, '4148000000', '파주시', '파주시', 9),
       (155, '4150000000', '이천시', '이천시', 9),
       (156, '4155000000', '안성시', '안성시', 9),
       (157, '4157000000', '김포시', '김포시', 9),
       (158, '4159000000', '화성시', '화성시', 9),
       (159, '4159100000', '화성시 만세구', '화성시 만세구', 9),
       (160, '4159300000', '화성시 효행구', '화성시 효행구', 9),
       (161, '4159500000', '화성시 병점구', '화성시 병점구', 9),
       (162, '4159700000', '화성시 동탄구', '화성시 동탄구', 9),
       (163, '4161000000', '광주시', '광주시', 9),
       (164, '4163000000', '양주시', '양주시', 9),
       (165, '4165000000', '포천시', '포천시', 9),
       (166, '4167000000', '여주시', '여주시', 9),
       (167, '4180000000', '연천군', '연천군', 9),
       (168, '4182000000', '가평군', '가평군', 9),
       (169, '4183000000', '양평군', '양평군', 9),
       (170, '4311000000', '청주시', '청주시', 10),
       (171, '4311100000', '청주시 상당구', '청주시 상당구', 10),
       (172, '4311200000', '청주시 서원구', '청주시 서원구', 10),
       (173, '4311300000', '청주시 흥덕구', '청주시 흥덕구', 10),
       (174, '4311400000', '청주시 청원구', '청주시 청원구', 10),
       (175, '4313000000', '충주시', '충주시', 10),
       (176, '4315000000', '제천시', '제천시', 10),
       (177, '4372000000', '보은군', '보은군', 10),
       (178, '4373000000', '옥천군', '옥천군', 10),
       (179, '4374000000', '영동군', '영동군', 10),
       (180, '4374500000', '증평군', '증평군', 10),
       (181, '4375000000', '진천군', '진천군', 10),
       (182, '4376000000', '괴산군', '괴산군', 10),
       (183, '4377000000', '음성군', '음성군', 10),
       (184, '4380000000', '단양군', '단양군', 10),
       (185, '4413000000', '천안시', '천안시', 11),
       (186, '4413100000', '천안시 동남구', '천안시 동남구', 11),
       (187, '4413300000', '천안시 서북구', '천안시 서북구', 11),
       (188, '4415000000', '공주시', '공주시', 11),
       (189, '4418000000', '보령시', '보령시', 11),
       (190, '4420000000', '아산시', '아산시', 11),
       (191, '4421000000', '서산시', '서산시', 11),
       (192, '4423000000', '논산시', '논산시', 11),
       (193, '4425000000', '계룡시', '계룡시', 11),
       (194, '4427000000', '당진시', '당진시', 11),
       (195, '4471000000', '금산군', '금산군', 11),
       (196, '4476000000', '부여군', '부여군', 11),
       (197, '4477000000', '서천군', '서천군', 11),
       (198, '4479000000', '청양군', '청양군', 11),
       (199, '4480000000', '홍성군', '홍성군', 11),
       (200, '4481000000', '예산군', '예산군', 11),
       (201, '4482500000', '태안군', '태안군', 11),
       (202, '4711000000', '포항시', '포항시', 12),
       (203, '4711100000', '포항시 남구', '포항시 남구', 12),
       (204, '4711300000', '포항시 북구', '포항시 북구', 12),
       (205, '4713000000', '경주시', '경주시', 12),
       (206, '4715000000', '김천시', '김천시', 12),
       (207, '4717000000', '안동시', '안동시', 12),
       (208, '4719000000', '구미시', '구미시', 12),
       (209, '4721000000', '영주시', '영주시', 12),
       (210, '4723000000', '영천시', '영천시', 12),
       (211, '4725000000', '상주시', '상주시', 12),
       (212, '4728000000', '문경시', '문경시', 12),
       (213, '4729000000', '경산시', '경산시', 12),
       (214, '4773000000', '의성군', '의성군', 12),
       (215, '4775000000', '청송군', '청송군', 12),
       (216, '4776000000', '영양군', '영양군', 12),
       (217, '4777000000', '영덕군', '영덕군', 12),
       (218, '4782000000', '청도군', '청도군', 12),
       (219, '4783000000', '고령군', '고령군', 12),
       (220, '4784000000', '성주군', '성주군', 12),
       (221, '4785000000', '칠곡군', '칠곡군', 12),
       (222, '4790000000', '예천군', '예천군', 12),
       (223, '4792000000', '봉화군', '봉화군', 12),
       (224, '4793000000', '울진군', '울진군', 12),
       (225, '4794000000', '울릉군', '울릉군', 12),
       (226, '4812000000', '창원시', '창원시', 13),
       (227, '4812100000', '창원시 의창구', '창원시 의창구', 13),
       (228, '4812300000', '창원시 성산구', '창원시 성산구', 13),
       (229, '4812500000', '창원시 마산합포구', '창원시 마산합포구', 13),
       (230, '4812700000', '창원시 마산회원구', '창원시 마산회원구', 13),
       (231, '4812900000', '창원시 진해구', '창원시 진해구', 13),
       (232, '4817000000', '진주시', '진주시', 13),
       (233, '4822000000', '통영시', '통영시', 13),
       (234, '4824000000', '사천시', '사천시', 13),
       (235, '4825000000', '김해시', '김해시', 13),
       (236, '4827000000', '밀양시', '밀양시', 13),
       (237, '4831000000', '거제시', '거제시', 13),
       (238, '4833000000', '양산시', '양산시', 13),
       (239, '4872000000', '의령군', '의령군', 13),
       (240, '4873000000', '함안군', '함안군', 13),
       (241, '4874000000', '창녕군', '창녕군', 13),
       (242, '4882000000', '고성군', '고성군', 13),
       (243, '4884000000', '남해군', '남해군', 13),
       (244, '4885000000', '하동군', '하동군', 13),
       (245, '4886000000', '산청군', '산청군', 13),
       (246, '4887000000', '함양군', '함양군', 13),
       (247, '4888000000', '거창군', '거창군', 13),
       (248, '4889000000', '합천군', '합천군', 13),
       (249, '5011000000', '제주시', '제주시', 14),
       (250, '5013000000', '서귀포시', '서귀포시', 14),
       (251, '5111000000', '춘천시', '춘천시', 15),
       (252, '5113000000', '원주시', '원주시', 15),
       (253, '5115000000', '강릉시', '강릉시', 15),
       (254, '5117000000', '동해시', '동해시', 15),
       (255, '5119000000', '태백시', '태백시', 15),
       (256, '5121000000', '속초시', '속초시', 15),
       (257, '5123000000', '삼척시', '삼척시', 15),
       (258, '5172000000', '홍천군', '홍천군', 15),
       (259, '5173000000', '횡성군', '횡성군', 15),
       (260, '5175000000', '영월군', '영월군', 15),
       (261, '5176000000', '평창군', '평창군', 15),
       (262, '5177000000', '정선군', '정선군', 15),
       (263, '5178000000', '철원군', '철원군', 15),
       (264, '5179000000', '화천군', '화천군', 15),
       (265, '5180000000', '양구군', '양구군', 15),
       (266, '5181000000', '인제군', '인제군', 15),
       (267, '5182000000', '고성군', '고성군', 15),
       (268, '5183000000', '양양군', '양양군', 15),
       (269, '5211000000', '전주시', '전주시', 16),
       (270, '5211100000', '전주시 완산구', '전주시 완산구', 16),
       (271, '5211300000', '전주시 덕진구', '전주시 덕진구', 16),
       (272, '5213000000', '군산시', '군산시', 16),
       (273, '5214000000', '익산시', '익산시', 16),
       (274, '5218000000', '정읍시', '정읍시', 16),
       (275, '5219000000', '남원시', '남원시', 16),
       (276, '5221000000', '김제시', '김제시', 16),
       (277, '5271000000', '완주군', '완주군', 16),
       (278, '5272000000', '진안군', '진안군', 16),
       (279, '5273000000', '무주군', '무주군', 16),
       (280, '5274000000', '장수군', '장수군', 16),
       (281, '5275000000', '임실군', '임실군', 16),
       (282, '5277000000', '순창군', '순창군', 16),
       (283, '5279000000', '고창군', '고창군', 16),
       (284, '5280000000', '부안군', '부안군', 16);

-- 기존 고수 활동 지역: expert1 강남구(39)·서초구(38), expert2 서초구(38)
INSERT INTO expert_profile_regions (expert_profile_id, regions_id)
VALUES ((SELECT ep.id
         FROM expert_profile ep
                  JOIN member m ON ep.member_id = m.id
         WHERE m.email = 'expert1@example.com'), 39),
       ((SELECT ep.id
         FROM expert_profile ep
                  JOIN member m ON ep.member_id = m.id
         WHERE m.email = 'expert1@example.com'), 38),
       ((SELECT ep.id
         FROM expert_profile ep
                  JOIN member m ON ep.member_id = m.id
         WHERE m.email = 'expert2@example.com'), 38);

-- 요청서 고정 질문 모든 서비스 카테고리에서 동일하게 사용
INSERT INTO question (content, is_required, display_order)
VALUES ('어떤 서비스가 필요하신가요?', TRUE, 1),
       ('언제 서비스를 받고 싶으신가요?', TRUE, 2),
       ('생각하고 계신 예산이 있나요?', FALSE, 3),
       ('고수에게 전달하고 싶은 추가 요청사항이 있나요?', FALSE, 4);

-- 서비스 요청서 3개
INSERT INTO service_request (member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'customer1@example.com'),
        101, 39, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),

       ((SELECT id FROM member WHERE email = 'customer1@example.com'),
        102, 38, 'OPEN', DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),

       ((SELECT id FROM member WHERE email = 'customer1@example.com'),
        139, 30, 'OPEN', DATEADD('HOUR', -3, CURRENT_TIMESTAMP));

-- 요청서 1: 이사
INSERT INTO request_answer (service_request_id, question_id, question_content, content)
SELECT sr.id,
       q.id,
       q.content,
       CASE q.display_order
           WHEN 1 THEN '원룸 이사를 요청합니다.'
           WHEN 2 THEN '11월 10일 오전을 희망합니다.'
           WHEN 3 THEN '예산은 30만 원입니다.'
           WHEN 4 THEN '침대와 책상 운반이 필요합니다.'
           END
FROM service_request sr
         JOIN member m ON sr.member_id = m.id
         JOIN question q ON q.display_order BETWEEN 1 AND 4
WHERE m.email = 'customer1@example.com'
  AND sr.second_category_id = 101
  AND sr.second_region_id = 39;

-- 요청서 2: 입주 청소
INSERT INTO request_answer
    (service_request_id, question_id, question_content, content)
SELECT sr.id,
       q.id,
       q.content,
       CASE q.display_order
           WHEN 1 THEN '20평 아파트 입주 청소가 필요합니다.'
           WHEN 2 THEN '11월 15일 오후를 희망합니다.'
           WHEN 3 THEN '예산은 25만 원입니다.'
           WHEN 4 THEN '창틀과 화장실을 집중적으로 청소해 주세요.'
           END
FROM service_request sr
         JOIN member m ON sr.member_id = m.id
         JOIN question q ON q.display_order BETWEEN 1 AND 4
WHERE m.email = 'customer1@example.com'
  AND sr.second_category_id = 102
  AND sr.second_region_id = 38;

-- 요청서 3: 개발 외주
INSERT INTO request_answer
    (service_request_id, question_id, question_content, content)
SELECT sr.id,
       q.id,
       q.content,
       CASE q.display_order
           WHEN 1 THEN 'Spring Boot 기반 REST API 개발이 필요합니다.'
           WHEN 2 THEN '11월 말까지 완료하고 싶습니다.'
           WHEN 3 THEN '예산은 100만 원입니다.'
           WHEN 4 THEN 'JPA와 MySQL을 사용해 주세요.'
           END
FROM service_request sr
         JOIN member m ON sr.member_id = m.id
         JOIN question q ON q.display_order BETWEEN 1 AND 4
WHERE m.email = 'customer1@example.com'
  AND sr.second_category_id = 139
  AND sr.second_region_id = 30;

-- 요청서 4: 이사 / 서초구. 지역 매칭과 견적 발송 테스트
INSERT INTO service_request
(member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'customer1@example.com'),
        101, 38, 'OPEN',
        DATEADD('MINUTE', -10, CURRENT_TIMESTAMP));

-- 요청서 5: 이사 / 강남구. MATCHED 상태도 받은 요청에 포함
INSERT INTO service_request
(member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'customer1@example.com'),
        101, 39, 'MATCHED',
        DATEADD('MINUTE', -20, CURRENT_TIMESTAMP));

-- 요청서 6: 이사 / 마포구. CLOSED 상태는 받은 요청에서 제외
INSERT INTO service_request
(member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'customer1@example.com'),
        101, 30, 'CLOSED',
        DATEADD('MINUTE', -30, CURRENT_TIMESTAMP));

-- 요청서 7: 입주 청소 / 강남구. 다중 서비스 매칭 테스트
INSERT INTO service_request
(member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'customer1@example.com'),
        102, 39, 'OPEN',
        DATEADD('MINUTE', -40, CURRENT_TIMESTAMP));

-- 요청서 8: expert1이 USER였을 때 작성한 요청. 본인 요청 제외 테스트
INSERT INTO service_request
(member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'expert1@example.com'),
        101, 39, 'OPEN',
        DATEADD('MINUTE', -50, CURRENT_TIMESTAMP));

-- 요청서 9: 이사 / 강남구. 등록 25시간 경과로 견적 접수 기간 만료
INSERT INTO service_request (member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'customer2@example.com'),
        101, 39, 'OPEN', DATEADD('HOUR', -25, CURRENT_TIMESTAMP));

-- 요청서 10: 이사 / 강남구. 등록 80시간 경과로 만료 견적 테스트
INSERT INTO service_request (member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email = 'customer2@example.com'),
        101, 39, 'OPEN', DATEADD('HOUR', -80, CURRENT_TIMESTAMP));

-- 기존 견적 1~3: 발송 중·73시간 만료·크레딧 부족 조건을 유지합니다.
INSERT INTO quote (expert_id, service_request_id, description, price, quote_status, sent_at)
VALUES ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id
         WHERE m.email='expert1@example.com'), 1, '이사 작업 전체 진행 가능합니다.', 300000, 'SENT', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id
         WHERE m.email='expert1@example.com'), 10, '만료 테스트용 견적입니다.', 200000, 'SENT', DATEADD('HOUR', -73, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id
         WHERE m.email='expert2@example.com'), 4, '서초구 이사 작업 가능합니다.', 280000, 'SENT', DATEADD('HOUR', -2, CURRENT_TIMESTAMP));

-- 기존 지갑: expert1 보유 10·예약 4, expert2 보유 2·예약 2. 만료 처리 전 초기값입니다.
INSERT INTO credit_wallet (member_id, balance, created_at, updated_at)
VALUES ((SELECT id FROM member WHERE email='expert1@example.com'), 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert2@example.com'), 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 시드 견적의 크레딧 예약 (견적 발송 시 예약되는 규칙과 맞춤)
INSERT INTO credit_reservation (credit_wallet_id, quote_id, amount, reservation_status, created_at)
SELECT w.id, q.id, 2, 'RESERVED', q.sent_at
FROM quote q
         JOIN expert_profile ep ON q.expert_id = ep.id
         JOIN credit_wallet w ON w.member_id = ep.member_id;

-- H2 로컬: 고정 ID 시드 다음 번호부터 새 항목을 등록합니다.
ALTER TABLE first_category
    ALTER COLUMN id RESTART WITH 12;
ALTER TABLE second_category
    ALTER COLUMN id RESTART WITH 209;
ALTER TABLE first_region
    ALTER COLUMN id RESTART WITH 17;
ALTER TABLE second_region
    ALTER COLUMN id RESTART WITH 285;

-- 크레딧 충전 상품: 1크레딧 = 1,000원, 추가 크레딧은 MVP에서 지급하지 않음
INSERT INTO credit_product (id, name, price, credit_amount, bonus_credit, is_active, created_at)
VALUES (1, '크레딧 10개', 10000, 10, 0, TRUE, CURRENT_TIMESTAMP),
       (2, '크레딧 30개', 30000, 30, 0, TRUE, CURRENT_TIMESTAMP),
       (3, '크레딧 50개', 50000, 50, 0, TRUE, CURRENT_TIMESTAMP),
       (4, '크레딧 100개', 100000, 100, 0, TRUE, CURRENT_TIMESTAMP);

-- 팀원별 10번대~50번대 계정: 각 고객·고수·관리자 2개씩. 기존 회원 ID 보존을 위해 마지막에 입력합니다.
-- 각 번호대 끝자리 9번 고객은 탈퇴 테스트 전용이며 초기 상태는 정상 회원입니다.
INSERT INTO member (email, password, nickname, phone, role, created_at, token_version)
VALUES ('customer11@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원1고객1', '01000001011', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer12@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원1고객2', '01000001012', 'USER', CURRENT_TIMESTAMP, 0),
       ('expert11@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원1고수1', '01000002011', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('expert12@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원1고수2', '01000002012', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('admin11@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원1관리자1', '01000003011', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('admin12@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원1관리자2', '01000003012', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('customer21@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원2고객1', '01000001021', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer22@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원2고객2', '01000001022', 'USER', CURRENT_TIMESTAMP, 0),
       ('expert21@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원2고수1', '01000002021', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('expert22@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원2고수2', '01000002022', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('admin21@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원2관리자1', '01000003021', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('admin22@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원2관리자2', '01000003022', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('customer31@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원3고객1', '01000001031', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer32@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원3고객2', '01000001032', 'USER', CURRENT_TIMESTAMP, 0),
       ('expert31@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원3고수1', '01000002031', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('expert32@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원3고수2', '01000002032', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('admin31@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원3관리자1', '01000003031', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('admin32@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원3관리자2', '01000003032', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('customer41@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원4고객1', '01000001041', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer42@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원4고객2', '01000001042', 'USER', CURRENT_TIMESTAMP, 0),
       ('expert41@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원4고수1', '01000002041', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('expert42@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원4고수2', '01000002042', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('admin41@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원4관리자1', '01000003041', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('admin42@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원4관리자2', '01000003042', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('customer51@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원5고객1', '01000001051', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer52@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원5고객2', '01000001052', 'USER', CURRENT_TIMESTAMP, 0),
       ('expert51@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원5고수1', '01000002051', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('expert52@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원5고수2', '01000002052', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('admin51@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원5관리자1', '01000003051', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('admin52@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원5관리자2', '01000003052', 'ADMIN', CURRENT_TIMESTAMP, 0),
       ('customer19@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원1탈퇴테스트', '01000009019', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer29@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원2탈퇴테스트', '01000009029', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer39@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원3탈퇴테스트', '01000009039', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer49@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원4탈퇴테스트', '01000009049', 'USER', CURRENT_TIMESTAMP, 0),
       ('customer59@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '팀원5탈퇴테스트', '01000009059', 'USER', CURRENT_TIMESTAMP, 0);

-- 팀원별 지역을 분리합니다. 끝자리 1번은 개별 시군구 이사·입주 청소. 2번은 해당 시도 전체 이사·개발 외주.
INSERT INTO expert_profile (member_id, short_intro, description, career_years, career_description, primary_category_id, is_nationwide)
VALUES ((SELECT id FROM member WHERE email = 'expert11@example.com'), '서울 강남구 테스트 고수입니다.', '서울 강남구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 101, FALSE),
       ((SELECT id FROM member WHERE email = 'expert12@example.com'), '서울 강남구 테스트 고수입니다.', '서울 강남구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 139, FALSE),
       ((SELECT id FROM member WHERE email = 'expert21@example.com'), '부산 중구 테스트 고수입니다.', '부산 중구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 101, FALSE),
       ((SELECT id FROM member WHERE email = 'expert22@example.com'), '부산 중구 테스트 고수입니다.', '부산 중구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 139, FALSE),
       ((SELECT id FROM member WHERE email = 'expert31@example.com'), '대구 중구 테스트 고수입니다.', '대구 중구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 101, FALSE),
       ((SELECT id FROM member WHERE email = 'expert32@example.com'), '대구 중구 테스트 고수입니다.', '대구 중구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 139, FALSE),
       ((SELECT id FROM member WHERE email = 'expert41@example.com'), '인천 제물포구 테스트 고수입니다.', '인천 제물포구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 101, FALSE),
       ((SELECT id FROM member WHERE email = 'expert42@example.com'), '인천 제물포구 테스트 고수입니다.', '인천 제물포구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 139, FALSE),
       ((SELECT id FROM member WHERE email = 'expert51@example.com'), '대전 동구 테스트 고수입니다.', '대전 동구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 101, FALSE),
       ((SELECT id FROM member WHERE email = 'expert52@example.com'), '대전 동구 테스트 고수입니다.', '대전 동구 활동과 견적 발송을 확인하는 테스트 프로필입니다.', 3, '테스트용 경력', 139, FALSE);

INSERT INTO expert_profile_services (expert_profile_id, services_id)
VALUES ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert11@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert11@example.com'), 102),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert12@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert12@example.com'), 139),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert21@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert21@example.com'), 102),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert22@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert22@example.com'), 139),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert31@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert31@example.com'), 102),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert32@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert32@example.com'), 139),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert41@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert41@example.com'), 102),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert42@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert42@example.com'), 139),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert51@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert51@example.com'), 102),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert52@example.com'), 101),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id = m.id WHERE m.email = 'expert52@example.com'), 139);

INSERT INTO expert_profile_regions (expert_profile_id, regions_id)
VALUES ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert11@example.com'), 39),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert21@example.com'), 69),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert31@example.com'), 85),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert41@example.com'), 94),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert51@example.com'), 105);

INSERT INTO expert_profile_whole_regions (expert_profile_id, whole_regions_id)
VALUES ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert12@example.com'), 1),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert22@example.com'), 3),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert32@example.com'), 4),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert42@example.com'), 5),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert52@example.com'), 6);

-- 신규 고수는 20크레딧에서 시작합니다. 아래 의뢰 요청됨 시드에만 2크레딧 사용을 반영합니다.
INSERT INTO credit_wallet (member_id, balance, created_at, updated_at)
SELECT id, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM member
WHERE email IN ('expert11@example.com', 'expert12@example.com', 'expert21@example.com', 'expert22@example.com', 'expert31@example.com', 'expert32@example.com', 'expert41@example.com', 'expert42@example.com', 'expert51@example.com', 'expert52@example.com');

-- 팀원별 요청서·견적 화면 시나리오: 원본 요청서 1~10과 원본 견적은 유지합니다.
INSERT INTO service_request (member_id, second_category_id, second_region_id, request_status, created_at)
VALUES ((SELECT id FROM member WHERE email='customer11@example.com'), 101, 39, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer11@example.com'), 139, 39, 'CLOSED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer12@example.com'), 102, 39, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer12@example.com'), 139, 39, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer21@example.com'), 101, 69, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer21@example.com'), 139, 69, 'CLOSED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer22@example.com'), 102, 69, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer22@example.com'), 139, 69, 'MATCHED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer31@example.com'), 101, 85, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer31@example.com'), 139, 85, 'CLOSED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer32@example.com'), 102, 85, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer32@example.com'), 139, 85, 'MATCHED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer41@example.com'), 101, 94, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer41@example.com'), 139, 94, 'CLOSED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer42@example.com'), 102, 94, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer42@example.com'), 139, 94, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer51@example.com'), 101, 105, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer51@example.com'), 139, 105, 'CLOSED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer52@example.com'), 102, 105, 'OPEN', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
       ((SELECT id FROM member WHERE email='customer52@example.com'), 139, 105, 'MATCHED', DATEADD('HOUR', -1, CURRENT_TIMESTAMP));

-- 새 고객의 요청서에는 필수·선택 질문 답변을 모두 저장합니다.
INSERT INTO request_answer (service_request_id, question_id, question_content, content)
SELECT sr.id, q.id, q.content,
       CASE q.display_order WHEN 1 THEN '시드 요청서의 서비스 견적을 받고 싶습니다.'
                            WHEN 2 THEN '고수와 일정 협의 예정입니다.'
                            WHEN 3 THEN '예산은 견적 확인 후 협의합니다.'
                            ELSE '팀원별 화면 확인용 테스트 요청서입니다.' END
FROM service_request sr JOIN member m ON sr.member_id=m.id CROSS JOIN question q
WHERE m.email IN ('customer11@example.com', 'customer12@example.com', 'customer21@example.com', 'customer22@example.com', 'customer31@example.com', 'customer32@example.com', 'customer41@example.com', 'customer42@example.com', 'customer51@example.com', 'customer52@example.com');

-- 팀원별 견적: 반복 INSERT를 하나로 묶고 기존 입력 순서를 유지합니다.
INSERT INTO quote (expert_id, service_request_id, description, price, quote_status, sent_at)
VALUES ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert12@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer11@example.com' AND sr.second_category_id=101 AND sr.request_status<>'CLOSED'),
        'expert12의 팀원별 SENT 견적입니다.', 112000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert21@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer22@example.com' AND sr.second_category_id=102 AND sr.request_status<>'CLOSED'),
        'expert21의 팀원별 SENT 견적입니다.', 121000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert22@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer22@example.com' AND sr.second_category_id=139 AND sr.request_status<>'CLOSED'),
        'expert22의 팀원별 REQUESTED 견적입니다.', 122000, 'REQUESTED', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert31@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer31@example.com' AND sr.second_category_id=101 AND sr.request_status<>'CLOSED'),
        'expert31의 팀원별 SENT 견적입니다.', 131000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert31@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer32@example.com' AND sr.second_category_id=102 AND sr.request_status<>'CLOSED'),
        'expert31의 팀원별 SENT 견적입니다.', 131000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert32@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer32@example.com' AND sr.second_category_id=139 AND sr.request_status<>'CLOSED'),
        'expert32의 팀원별 REQUESTED 견적입니다.', 132000, 'REQUESTED', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert32@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer31@example.com' AND sr.second_category_id=101 AND sr.request_status<>'CLOSED'),
        'expert32의 팀원별 SENT 견적입니다.', 132000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert41@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer42@example.com' AND sr.second_category_id=102 AND sr.request_status<>'CLOSED'),
        'expert41의 팀원별 SENT 견적입니다.', 141000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert51@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer51@example.com' AND sr.second_category_id=101 AND sr.request_status<>'CLOSED'),
        'expert51의 팀원별 SENT 견적입니다.', 151000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert51@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer52@example.com' AND sr.second_category_id=102 AND sr.request_status<>'CLOSED'),
        'expert51의 팀원별 SENT 견적입니다.', 151000, 'SENT', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
       ((SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert52@example.com'),
        (SELECT sr.id FROM service_request sr JOIN member m ON sr.member_id=m.id
         WHERE m.email='customer52@example.com' AND sr.second_category_id=139 AND sr.request_status<>'CLOSED'),
        'expert52의 팀원별 REQUESTED 견적입니다.', 152000, 'REQUESTED', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP));

-- SENT는 예약만 반영하고 REQUESTED는 실제 의뢰 요청처럼 사용 처리·잔액·내역·채팅방을 맞춥니다.
INSERT INTO credit_reservation (credit_wallet_id, quote_id, amount, reservation_status, created_at)
SELECT w.id, q.id, 2, CASE WHEN q.quote_status='REQUESTED' THEN 'USED' ELSE 'RESERVED' END, q.sent_at
FROM quote q JOIN expert_profile ep ON q.expert_id=ep.id
JOIN member m ON ep.member_id=m.id JOIN credit_wallet w ON w.member_id=m.id
WHERE m.email IN ('expert11@example.com', 'expert12@example.com', 'expert21@example.com', 'expert22@example.com', 'expert31@example.com', 'expert32@example.com', 'expert41@example.com', 'expert42@example.com', 'expert51@example.com', 'expert52@example.com');

UPDATE credit_wallet SET balance=18
WHERE member_id IN (SELECT id FROM member WHERE email IN ('expert22@example.com', 'expert32@example.com', 'expert52@example.com'));

INSERT INTO credit_history (credit_wallet_id, history_type, amount, balance_after, quote_id, created_at)
SELECT w.id, 'USE', -2, 18, q.id, q.sent_at
FROM quote q JOIN expert_profile ep ON q.expert_id=ep.id
JOIN member m ON ep.member_id=m.id JOIN credit_wallet w ON w.member_id=m.id
WHERE m.email IN ('expert11@example.com', 'expert12@example.com', 'expert21@example.com', 'expert22@example.com', 'expert31@example.com', 'expert32@example.com', 'expert41@example.com', 'expert42@example.com', 'expert51@example.com', 'expert52@example.com') AND q.quote_status='REQUESTED';

INSERT INTO chat_room (quote_id, created_at)
SELECT q.id, q.sent_at FROM quote q JOIN expert_profile ep ON q.expert_id=ep.id
JOIN member m ON ep.member_id=m.id
WHERE m.email IN ('expert11@example.com', 'expert12@example.com', 'expert21@example.com', 'expert22@example.com', 'expert31@example.com', 'expert32@example.com', 'expert41@example.com', 'expert42@example.com', 'expert51@example.com', 'expert52@example.com') AND q.quote_status='REQUESTED';

INSERT INTO chat_participant (chat_room_id, member_id)
SELECT cr.id, ep.member_id FROM chat_room cr JOIN quote q ON cr.quote_id=q.id
JOIN expert_profile ep ON q.expert_id=ep.id JOIN member m ON ep.member_id=m.id
WHERE m.email IN ('expert11@example.com', 'expert12@example.com', 'expert21@example.com', 'expert22@example.com', 'expert31@example.com', 'expert32@example.com', 'expert41@example.com', 'expert42@example.com', 'expert51@example.com', 'expert52@example.com')
UNION ALL
SELECT cr.id, sr.member_id FROM chat_room cr JOIN quote q ON cr.quote_id=q.id
JOIN service_request sr ON q.service_request_id=sr.id JOIN expert_profile ep ON q.expert_id=ep.id
JOIN member m ON ep.member_id=m.id WHERE m.email IN ('expert11@example.com', 'expert12@example.com', 'expert21@example.com', 'expert22@example.com', 'expert31@example.com', 'expert32@example.com', 'expert41@example.com', 'expert42@example.com', 'expert51@example.com', 'expert52@example.com');

-- H2의 고정 ID 상품 1~4 다음부터 신규 상품을 등록합니다.
ALTER TABLE credit_product ALTER COLUMN id RESTART WITH 5;

-- 팀원별 커뮤니티: 게시판별 1개씩. 탈퇴 전용 계정은 연결하지 않습니다.

INSERT INTO post (member_id, second_category_id, second_region_id, board_type, title, content, view_count, comment_count, like_count, is_visible, created_at, updated_at)
VALUES ((SELECT id FROM member WHERE email='customer11@example.com'), 101, 39, 'QUESTION', '팀원1 이사 준비 질문', '이사 전에 미리 준비할 사항을 알려 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert11@example.com'), 102, 39, 'TIP', '팀원1 입주 청소 준비 팁', '청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer12@example.com'), 139, 39, 'RECRUIT', '팀원1 개발 작업 고수 구해요', '작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert12@example.com'), 139, 39, 'PROMOTION', '팀원1 개발 상담 안내', '개발 외주 작업 범위와 일정을 함께 상담해 드립니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer21@example.com'), 101, 69, 'QUESTION', '팀원2 이사 준비 질문', '이사 전에 미리 준비할 사항을 알려 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert21@example.com'), 102, 69, 'TIP', '팀원2 입주 청소 준비 팁', '청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer22@example.com'), 139, 69, 'RECRUIT', '팀원2 개발 작업 고수 구해요', '작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert22@example.com'), 139, 69, 'PROMOTION', '팀원2 개발 상담 안내', '개발 외주 작업 범위와 일정을 함께 상담해 드립니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer31@example.com'), 101, 85, 'QUESTION', '팀원3 이사 준비 질문', '이사 전에 미리 준비할 사항을 알려 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert31@example.com'), 102, 85, 'TIP', '팀원3 입주 청소 준비 팁', '청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer32@example.com'), 139, 85, 'RECRUIT', '팀원3 개발 작업 고수 구해요', '작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert32@example.com'), 139, 85, 'PROMOTION', '팀원3 개발 상담 안내', '개발 외주 작업 범위와 일정을 함께 상담해 드립니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer41@example.com'), 101, 94, 'QUESTION', '팀원4 이사 준비 질문', '이사 전에 미리 준비할 사항을 알려 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert41@example.com'), 102, 94, 'TIP', '팀원4 입주 청소 준비 팁', '청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer42@example.com'), 139, 94, 'RECRUIT', '팀원4 개발 작업 고수 구해요', '작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert42@example.com'), 139, 94, 'PROMOTION', '팀원4 개발 상담 안내', '개발 외주 작업 범위와 일정을 함께 상담해 드립니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer51@example.com'), 101, 105, 'QUESTION', '팀원5 이사 준비 질문', '이사 전에 미리 준비할 사항을 알려 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert51@example.com'), 102, 105, 'TIP', '팀원5 입주 청소 준비 팁', '청소 전에는 작은 짐을 정리하고 작업 공간을 확보해 주세요.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer52@example.com'), 139, 105, 'RECRUIT', '팀원5 개발 작업 고수 구해요', '작은 웹 서비스 개발 일정과 비용을 상담하고 싶습니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='expert52@example.com'), 139, 105, 'PROMOTION', '팀원5 개발 상담 안내', '개발 외주 작업 범위와 일정을 함께 상담해 드립니다.', 0, 2, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 게시글마다 같은 팀의 고객·고수가 댓글을 하나씩 작성합니다.
INSERT INTO post_comment (post_id, member_id, content, like_count, created_at, updated_at)
SELECT p.id, commenter.id, '궁금한 점은 댓글로 함께 이야기해 주세요.', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM post p JOIN member author ON p.member_id=author.id
JOIN member commenter ON commenter.email=CONCAT('expert', SUBSTRING(author.email, LOCATE('@', author.email)-2, 1), '1@example.com');

INSERT INTO post_comment (post_id, member_id, content, like_count, created_at, updated_at)
SELECT p.id, commenter.id, '안내 감사합니다. 일정과 작업 범위를 확인해 보겠습니다.', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM post p JOIN member author ON p.member_id=author.id
JOIN member commenter ON commenter.email=CONCAT('customer', SUBSTRING(author.email, LOCATE('@', author.email)-2, 1), '2@example.com');

INSERT INTO bookmark (member_id, expert_profile_id, is_active, bookmarked_at)
VALUES ((SELECT id FROM member WHERE email='customer11@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert11@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer11@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert12@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer12@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert12@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer21@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert21@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer21@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert22@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer22@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert22@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer31@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert31@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer31@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert32@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer32@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert32@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer41@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert41@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer41@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert42@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer42@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert42@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer51@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert51@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer51@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert52@example.com'), TRUE, CURRENT_TIMESTAMP),
       ((SELECT id FROM member WHERE email='customer52@example.com'), (SELECT ep.id FROM expert_profile ep JOIN member m ON ep.member_id=m.id WHERE m.email='expert52@example.com'), TRUE, CURRENT_TIMESTAMP);

-- 기존 채팅방 3개의 참여자가 대화합니다. 목록 미리보기도 마지막 메시지와 맞춥니다.
INSERT INTO chat_message (chat_room_id, sender_id, type, content, created_at)
SELECT cr.id, sr.member_id, 'TEXT', '안녕하세요. 작업 일정 상담을 부탁드립니다.', DATEADD('MINUTE', -10, CURRENT_TIMESTAMP)
FROM chat_room cr JOIN quote q ON cr.quote_id=q.id JOIN service_request sr ON q.service_request_id=sr.id;

INSERT INTO chat_message (chat_room_id, sender_id, type, content, created_at)
SELECT cr.id, ep.member_id, 'TEXT', '안녕하세요. 가능한 일정과 작업 범위를 알려 주세요.', DATEADD('MINUTE', -9, CURRENT_TIMESTAMP)
FROM chat_room cr JOIN quote q ON cr.quote_id=q.id JOIN expert_profile ep ON q.expert_id=ep.id;

UPDATE chat_room SET last_message_preview='안녕하세요. 가능한 일정과 작업 범위를 알려 주세요.',
                     last_message_at=DATEADD('MINUTE', -9, CURRENT_TIMESTAMP);


-- =====================================================================
-- 커뮤니티 시드 데이터 (메인 페이지 시연용)
-- 백엔드 src/main/resources/data.sql 맨 끝에 그대로 붙여 넣는다.
-- H2(로컬) 문법 기준. 기존 시드의 member·second_category·second_region 데이터가 먼저 들어가 있어야 한다.
--
-- 들어가는 것
--   1) 시드용 고수 6명 · 고객 2명 (비밀번호는 기존 시드 계정과 같음, 고수 프로필은 없음)
--   2) 게시글 27개: 홍보 8 · 팁 8 · 질문 8 · 모집 3
--   3) 사진 파일 20개 (file 테이블 행) + 게시글 연결(post_images)
--   4) 질문 글 댓글, 좋아요, 조회 수
--
-- 사진: Unsplash 사진 20장을 Supabase public 버킷(efy-public)에 올려 두었다. 출처는 docs/seed/README.md 참고.
-- =====================================================================

-- 1) 시드 회원 -----------------------------------------------------------
INSERT INTO member (email, password, nickname, phone, role, created_at, token_version)
VALUES ('seed-clean@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '깔끔클린', '01070000001', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('seed-plumb@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '바른설비', '01070000002', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('seed-interior@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '온공간디자인', '01070000003', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('seed-photo@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '스냅작가하루', '01070000004', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('seed-english@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '제니영어', '01070000005', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('seed-dev@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '코드메이트', '01070000006', 'EXPERT', CURRENT_TIMESTAMP, 0),
       ('seed-user1@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '이사준비중', '01070000007', 'USER', CURRENT_TIMESTAMP, 0),
       ('seed-user2@example.com', '$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C', '새내기집사', '01070000008', 'USER', CURRENT_TIMESTAMP, 0);

-- 2) 게시글 --------------------------------------------------------------
-- 지역 ID: 19 용산 · 20 성동 · 27 노원 · 30 마포 · 35 영등포 · 37 관악 · 38 서초 · 39 강남 · 40 송파 (모두 서울)
INSERT INTO post (member_id, second_category_id, second_region_id, board_type, title, content,
                  view_count, comment_count, like_count, created_at, updated_at, is_visible)
VALUES
-- 홍보 (고수)
((SELECT id FROM member WHERE email = 'expert1@example.com'), 101, 38, 'PROMOTION',
 '원룸·투룸 이사 전문, 3년간 파손 0건',
 '서초·강남 위주로 원룸, 투룸 이사를 하고 있어요.
가구는 모서리 보호대를 씌우고, 그릇은 한 장씩 따로 싸서 옮깁니다.
평일 오전 이사는 조금 더 저렴하게 진행해요. 요청서 남겨 주시면 사진 보고 정확히 견적 드릴게요.',
 412, 0, 0, DATEADD('DAY', -6, CURRENT_TIMESTAMP), DATEADD('DAY', -6, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-clean@example.com'), 102, 39, 'PROMOTION',
 '입주청소 전후 사진 보여드려요',
 '34평 아파트 입주청소 사례예요. 2인 1조로 6시간 작업했습니다.
창틀, 베란다 배수구, 주방 후드 안쪽까지 분해해서 닦아요.
작업 후 사진으로 구석구석 보여 드리고, 하루 안에 다시 봐 드립니다.',
 538, 0, 0, DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-plumb@example.com'), 109, 30, 'PROMOTION',
 '보일러 고장, 방문 진단부터 받아보세요',
 '마포·서대문 지역 보일러, 수도 수리합니다.
에러코드만 알려 주셔도 대략적인 원인과 비용을 미리 말씀드려요.
부품 교체가 필요 없으면 출장비만 받습니다.',
 267, 0, 0, DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-interior@example.com'), 113, 20, 'PROMOTION',
 '15평 구축 부분 리모델링 사례',
 '주방 상판과 욕실 타일, 거실 조명만 바꾼 부분 리모델링이에요.
전체 공사보다 기간은 5일, 비용은 절반 정도였어요.
살면서 공사가 가능한지, 어디까지 바꿀지 현장에서 같이 정해 드립니다.',
 691, 0, 0, DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-photo@example.com'), 120, 19, 'PROMOTION',
 '웨딩·가족 스냅 포트폴리오',
 '야외 웨딩 스냅과 가족사진을 주로 찍어요.
촬영 전 장소와 시간대를 같이 정하고, 보정본은 2주 안에 보내 드립니다.
원본 전체도 함께 드려요.',
 355, 0, 0, DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-english@example.com'), 129, 40, 'PROMOTION',
 '중고등 영어 내신 1:1 관리',
 '송파 지역 중고등학생 영어 내신을 1:1로 봐요.
학교 기출을 분석해서 시험 4주 전부터 맞춤 자료로 준비합니다.
첫 수업은 레벨 확인과 상담으로 진행해요.',
 289, 0, 0, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-dev@example.com'), 139, 35, 'PROMOTION',
 'MVP 웹·앱 외주 개발 사례',
 '초기 창업팀의 MVP를 6주 안에 만들어 드려요.
기획서가 없어도 괜찮아요. 첫 미팅에서 꼭 필요한 기능만 같이 추립니다.
최근에는 예약 서비스와 사내 관리 도구를 만들었어요.',
 198, 0, 0, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'expert2@example.com'), 103, 27, 'PROMOTION',
 '에어컨 분해청소, 겨울에 미리 하세요',
 '벽걸이, 스탠드, 시스템 에어컨 모두 분해해서 고압 세척해요.
겨울에는 예약이 여유로워서 원하시는 날짜에 맞춰 드릴 수 있어요.
곰팡이 냄새가 났다면 꼭 한번 맡겨 보세요.',
 174, 0, 0, DATEADD('HOUR', -20, CURRENT_TIMESTAMP), DATEADD('HOUR', -20, CURRENT_TIMESTAMP), TRUE),

-- 팁 (고수)
((SELECT id FROM member WHERE email = 'seed-clean@example.com'), 102, 39, 'TIP',
 '입주청소 전에 꼭 확인할 5가지',
 '1. 청소 범위에 베란다, 창틀, 붙박이장 안쪽이 들어가는지 확인하세요.
2. 새집증후군 시공은 청소와 별도인지 물어보세요.
3. 작업 인원과 예상 시간을 미리 받아 두세요.
4. 끝나고 함께 둘러보는 시간을 꼭 잡으세요.
5. 다시 봐 주는 기간(A/S)이 며칠인지 확인하세요.',
 1204, 0, 0, DATEADD('DAY', -9, CURRENT_TIMESTAMP), DATEADD('DAY', -9, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-plumb@example.com'), 109, 30, 'TIP',
 '보일러 동파 막는 겨울 습관',
 '영하로 내려가는 날에는 외출 모드로 두고 나가세요. 보일러를 끄면 배관이 얼 수 있어요.
베란다에 보일러가 있다면 배관을 보온재로 감싸 두세요.
수도는 아주 가늘게 틀어 두면 동파를 막을 수 있어요.',
 876, 0, 0, DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', -7, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'expert1@example.com'), 101, 38, 'TIP',
 '이삿짐 쌀 때 깨지는 물건 포장법',
 '접시는 눕히지 말고 세워서 넣으세요. 세워야 충격에 덜 깨져요.
컵 안에는 신문지를 채우고, 상자 빈 곳은 수건으로 메우세요.
상자 겉에 "깨짐 주의"와 들어갈 방 이름을 적어 두면 정리가 빨라요.',
 943, 0, 0, DATEADD('DAY', -6, CURRENT_TIMESTAMP), DATEADD('DAY', -6, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-interior@example.com'), 115, 20, 'TIP',
 '장판 vs 강마루, 이렇게 고르세요',
 '반려동물이 있거나 물을 자주 쓰면 장판이 관리하기 편해요.
찍힘에 강하고 고급스러운 느낌을 원하면 강마루가 좋아요.
비용은 강마루가 평당 2~3배 정도 더 들어요.',
 1532, 0, 0, DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-photo@example.com'), 120, 19, 'TIP',
 '셀프 웨딩 촬영 장소 고르는 법',
 '해가 지기 1시간 전, 골든아워에 찍으면 보정 없이도 색이 예뻐요.
사람이 적은 평일 오전도 좋아요.
공원은 촬영 허가가 필요한 곳이 있으니 미리 확인하세요.',
 702, 0, 0, DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-english@example.com'), 129, 40, 'TIP',
 '영어 단어, 하루 30개 외우는 법',
 '새 단어 30개보다 어제 외운 단어 다시 보기가 먼저예요.
1일, 3일, 7일 뒤에 다시 보는 순서로 복습해 보세요.
예문을 소리 내어 읽으면 오래 기억에 남아요.',
 1088, 0, 0, DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-dev@example.com'), 139, 35, 'TIP',
 '외주 맡기기 전 정리할 문서 3가지',
 '1. 누가, 어떤 순서로 쓰는지 적은 사용 흐름
2. 꼭 있어야 하는 기능과 나중에 넣을 기능 목록
3. 참고하는 서비스 2~3개와 마음에 드는 이유
이 세 가지만 있어도 견적이 훨씬 정확해져요.',
 655, 0, 0, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'expert2@example.com'), 103, 27, 'TIP',
 '에어컨 청소 주기, 이렇게 보세요',
 '필터는 2주에 한 번 직접 물로 씻어 주세요.
분해청소는 1~2년에 한 번이면 충분해요.
켤 때 쉰내가 나거나 바람이 약해졌다면 그때가 분해청소할 때예요.',
 487, 0, 0, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), TRUE),

-- 질문 (고객)
((SELECT id FROM member WHERE email = 'seed-user1@example.com'), 101, 38, 'QUESTION',
 '이사 견적이 업체마다 왜 이렇게 달라요?',
 '투룸 이사인데 견적이 45만 원부터 80만 원까지 나왔어요.
어떤 부분을 비교해야 할지 모르겠어요.',
 321, 0, 0, DATEADD('HOUR', -30, CURRENT_TIMESTAMP), DATEADD('HOUR', -30, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'customer2@example.com'), 102, 39, 'QUESTION',
 '입주청소 몇 평부터 2명이 와요?',
 '24평 아파트인데 1명이 와도 괜찮을까요?',
 188, 0, 0, DATEADD('HOUR', -26, CURRENT_TIMESTAMP), DATEADD('HOUR', -26, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'customer1@example.com'), 109, 30, 'QUESTION',
 '보일러에 E-03 에러가 계속 떠요',
 '껐다 켜면 잠깐 되다가 다시 에러가 나요. 직접 해볼 수 있는 게 있을까요?',
 265, 0, 0, DATEADD('HOUR', -20, CURRENT_TIMESTAMP), DATEADD('HOUR', -20, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-user2@example.com'), 114, 37, 'QUESTION',
 '원룸 도배, 셀프로 해도 될까요?',
 '6평 원룸이에요. 셀프 도배 해보신 분 계신가요? 업체 비용이랑 차이가 궁금해요.',
 143, 0, 0, DATEADD('HOUR', -16, CURRENT_TIMESTAMP), DATEADD('HOUR', -16, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'customer2@example.com'), 129, 40, 'QUESTION',
 '초등 영어 과외 몇 학년부터 하나요?',
 '초3 아이인데 학원이 맞을지 1:1 과외가 맞을지 고민이에요.',
 97, 0, 0, DATEADD('HOUR', -12, CURRENT_TIMESTAMP), DATEADD('HOUR', -12, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-user1@example.com'), 120, 19, 'QUESTION',
 '웨딩 스냅 원본도 다 받을 수 있나요?',
 '보정본만 주는 곳이 있다고 들었어요. 보통 어떻게 하나요?',
 76, 0, 0, DATEADD('HOUR', -9, CURRENT_TIMESTAMP), DATEADD('HOUR', -9, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-user2@example.com'), 191, 30, 'QUESTION',
 '강아지 산책 도우미 써보신 분?',
 '출근 시간이 길어져서 낮에 산책을 부탁하고 싶어요. 비용이 어느 정도인가요?',
 58, 0, 0, DATEADD('HOUR', -6, CURRENT_TIMESTAMP), DATEADD('HOUR', -6, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'customer1@example.com'), 139, 35, 'QUESTION',
 '앱 외주 견적은 뭘 기준으로 받아요?',
 '예약 기능이 있는 간단한 앱을 만들고 싶어요. 기능 목록만 있으면 될까요?',
 41, 0, 0, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), DATEADD('HOUR', -3, CURRENT_TIMESTAMP), TRUE),

-- 모집
((SELECT id FROM member WHERE email = 'seed-clean@example.com'), 102, 39, 'RECRUIT',
 '입주청소 팀원 모집합니다 (경력 무관)',
 '주 3~4회 함께 일할 팀원을 찾아요. 처음이어도 2주 동안 같이 다니며 알려 드려요.',
 233, 0, 0, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-user1@example.com'), 155, 40, 'RECRUIT',
 '평일 저녁 토익 스터디원 구해요',
 '송파역 근처 카페에서 화, 목 저녁 8시에 모여요. 목표 점수는 850점이에요.',
 112, 0, 0, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), TRUE),
((SELECT id FROM member WHERE email = 'seed-photo@example.com'), 126, 19, 'RECRUIT',
 '연말 행사 촬영 보조 2명 구해요',
 '12월 둘째 주 토요일, 기업 행사 촬영 보조를 구해요. 장비는 제가 준비해요.',
 89, 0, 0, DATEADD('HOUR', -10, CURRENT_TIMESTAMP), DATEADD('HOUR', -10, CURRENT_TIMESTAMP), TRUE);

-- 3) 사진 파일 + 게시글 연결 ----------------------------------------------
-- storage_path는 Supabase public 버킷(efy-public)에 이미 올라가 있는 사진 경로다 (docs/seed/README.md)
INSERT INTO file (uploader_id, storage_path, original_name, content_type, size, visibility, created_at)
SELECT m.id, v.path, v.name, 'image/jpeg', 300000, 'PUBLIC', CURRENT_TIMESTAMP
FROM (VALUES ('expert1@example.com', '5daa003e-1e20-4848-81d7-c3a418cecf91.jpg', 'promo-moving-1.jpg'),
             ('expert1@example.com', '50347254-5e8f-4b63-a33b-082d42401d16.jpg', 'promo-moving-2.jpg'),
             ('seed-clean@example.com', '064360f9-82e8-4e0c-b723-a2062cc1a375.jpg', 'promo-clean-1.jpg'),
             ('seed-clean@example.com', '50f09811-0f6c-4624-a85a-6f2f95294a83.jpg', 'promo-clean-2.jpg'),
             ('seed-plumb@example.com', 'aa76c2cf-be28-4f5c-99cc-197b0e69c1d4.jpg', 'promo-boiler-1.jpg'),
             ('seed-interior@example.com', '83d5c398-973d-497f-b61e-b9e2c0188532.jpg', 'promo-interior-1.jpg'),
             ('seed-interior@example.com', 'a5cd84ed-18ac-48e1-9a90-71e60abcaf05.jpg', 'promo-interior-2.jpg'),
             ('seed-photo@example.com', '10cd17fb-a6bc-456e-919d-3f8270397af3.jpg', 'promo-snap-1.jpg'),
             ('seed-photo@example.com', 'ab8834a5-3e84-4704-8069-ea8b9233458e.jpg', 'promo-snap-2.jpg'),
             ('seed-english@example.com', 'abd56987-aebe-47d2-8f7c-48df5f14bb94.jpg', 'promo-english-1.jpg'),
             ('seed-dev@example.com', '0f11d56a-67be-49b3-8573-7a97700a7f4c.jpg', 'promo-dev-1.jpg'),
             ('expert2@example.com', '12904168-fe15-4dc8-9df6-ddc507020606.jpg', 'promo-aircon-1.jpg'),
             ('seed-clean@example.com', 'd8c3a907-ba6a-469d-8093-4e69d9cf5284.jpg', 'tip-clean.jpg'),
             ('seed-plumb@example.com', 'ebea7963-5a18-4ac7-87db-f75fc6cfb701.jpg', 'tip-boiler.jpg'),
             ('expert1@example.com', '73c6b070-7b0c-493b-8af0-894c14c0c78c.jpg', 'tip-packing.jpg'),
             ('seed-interior@example.com', '74c3a957-9638-4644-962c-3eb84f6d090b.jpg', 'tip-floor.jpg'),
             ('seed-photo@example.com', '2f5d05bc-bad6-42c7-bdb6-dbae87d162e8.jpg', 'tip-wedding.jpg'),
             ('seed-english@example.com', 'd40e132c-5fe4-4565-8427-859a847dca8a.jpg', 'tip-words.jpg'),
             ('seed-dev@example.com', '434ee595-4316-4306-8b89-be7d42425ab9.jpg', 'tip-docs.jpg'),
             ('expert2@example.com', '797b95bd-3896-4109-bbf3-4912b12e72b1.jpg', 'tip-aircon.jpg')) AS v(email, path, name)
         JOIN member m ON m.email = v.email;

-- sort_order 0번 사진이 목록 썸네일이 된다
INSERT INTO post_images (post_id, file_id, sort_order)
SELECT p.id, f.id, v.ord
FROM (VALUES ('원룸·투룸 이사 전문, 3년간 파손 0건', '5daa003e-1e20-4848-81d7-c3a418cecf91.jpg', 0),
             ('원룸·투룸 이사 전문, 3년간 파손 0건', '50347254-5e8f-4b63-a33b-082d42401d16.jpg', 1),
             ('입주청소 전후 사진 보여드려요', '064360f9-82e8-4e0c-b723-a2062cc1a375.jpg', 0),
             ('입주청소 전후 사진 보여드려요', '50f09811-0f6c-4624-a85a-6f2f95294a83.jpg', 1),
             ('보일러 고장, 방문 진단부터 받아보세요', 'aa76c2cf-be28-4f5c-99cc-197b0e69c1d4.jpg', 0),
             ('15평 구축 부분 리모델링 사례', '83d5c398-973d-497f-b61e-b9e2c0188532.jpg', 0),
             ('15평 구축 부분 리모델링 사례', 'a5cd84ed-18ac-48e1-9a90-71e60abcaf05.jpg', 1),
             ('웨딩·가족 스냅 포트폴리오', '10cd17fb-a6bc-456e-919d-3f8270397af3.jpg', 0),
             ('웨딩·가족 스냅 포트폴리오', 'ab8834a5-3e84-4704-8069-ea8b9233458e.jpg', 1),
             ('중고등 영어 내신 1:1 관리', 'abd56987-aebe-47d2-8f7c-48df5f14bb94.jpg', 0),
             ('MVP 웹·앱 외주 개발 사례', '0f11d56a-67be-49b3-8573-7a97700a7f4c.jpg', 0),
             ('에어컨 분해청소, 겨울에 미리 하세요', '12904168-fe15-4dc8-9df6-ddc507020606.jpg', 0),
             ('입주청소 전에 꼭 확인할 5가지', 'd8c3a907-ba6a-469d-8093-4e69d9cf5284.jpg', 0),
             ('보일러 동파 막는 겨울 습관', 'ebea7963-5a18-4ac7-87db-f75fc6cfb701.jpg', 0),
             ('이삿짐 쌀 때 깨지는 물건 포장법', '73c6b070-7b0c-493b-8af0-894c14c0c78c.jpg', 0),
             ('장판 vs 강마루, 이렇게 고르세요', '74c3a957-9638-4644-962c-3eb84f6d090b.jpg', 0),
             ('셀프 웨딩 촬영 장소 고르는 법', '2f5d05bc-bad6-42c7-bdb6-dbae87d162e8.jpg', 0),
             ('영어 단어, 하루 30개 외우는 법', 'd40e132c-5fe4-4565-8427-859a847dca8a.jpg', 0),
             ('외주 맡기기 전 정리할 문서 3가지', '434ee595-4316-4306-8b89-be7d42425ab9.jpg', 0),
             ('에어컨 청소 주기, 이렇게 보세요', '797b95bd-3896-4109-bbf3-4912b12e72b1.jpg', 0)) AS v(title, path, ord)
         JOIN post p ON p.title = v.title
         JOIN file f ON f.storage_path = v.path;

-- 4) 댓글 ----------------------------------------------------------------
INSERT INTO post_comment (post_id, member_id, content, like_count, created_at, updated_at)
SELECT p.id, m.id, v.content, 0, DATEADD('HOUR', -v.hrs, CURRENT_TIMESTAMP), DATEADD('HOUR', -v.hrs, CURRENT_TIMESTAMP)
FROM (VALUES ('이사 견적이 업체마다 왜 이렇게 달라요?', 'expert1@example.com', '사다리차 포함 여부, 포장 범위, 작업 인원을 먼저 비교해 보세요. 이 세 가지에서 차이가 가장 커요.', 28),
             ('이사 견적이 업체마다 왜 이렇게 달라요?', 'expert2@example.com', '추가 요금 조건(층수, 엘리베이터, 주말)도 견적서에 적혀 있는지 확인하세요.', 27),
             ('이사 견적이 업체마다 왜 이렇게 달라요?', 'customer1@example.com', '저도 비슷했는데 포장 범위 차이였어요!', 25),
             ('입주청소 몇 평부터 2명이 와요?', 'seed-clean@example.com', '보통 20평대부터 2명이 가요. 1명이면 시간이 두 배 가까이 걸려요.', 24),
             ('입주청소 몇 평부터 2명이 와요?', 'seed-user1@example.com', '저는 25평에 2명 오셨어요.', 22),
             ('보일러에 E-03 에러가 계속 떠요', 'seed-plumb@example.com', '대부분 점화 불량이에요. 가스 밸브가 열려 있는지 먼저 확인해 보시고, 계속 뜨면 점검받으세요.', 18),
             ('보일러에 E-03 에러가 계속 떠요', 'seed-user2@example.com', '저희 집도 밸브가 반쯤 잠겨 있었어요.', 17),
             ('원룸 도배, 셀프로 해도 될까요?', 'seed-interior@example.com', '6평이면 셀프도 가능해요. 다만 천장은 혼자 하기 어려워서 벽만 셀프로 하시는 분이 많아요.', 14),
             ('초등 영어 과외 몇 학년부터 하나요?', 'seed-english@example.com', '초3이면 1:1로 기초 읽기부터 잡기 좋은 시기예요. 아이 성향에 따라 소그룹도 괜찮아요.', 10),
             ('웨딩 스냅 원본도 다 받을 수 있나요?', 'seed-photo@example.com', '저는 원본 전체를 드려요. 계약 전에 원본 제공 여부를 꼭 물어보세요.', 7),
             ('앱 외주 견적은 뭘 기준으로 받아요?', 'seed-dev@example.com', '기능 목록에 화면 개수와 로그인 방식까지 적어 주시면 견적이 정확해져요.', 2)) AS v(title, email, content, hrs)
         JOIN post p ON p.title = v.title
         JOIN member m ON m.email = v.email;

-- 5) 좋아요 --------------------------------------------------------------
INSERT INTO post_like (member_id, post_id)
SELECT m.id, p.id
FROM (VALUES ('입주청소 전에 꼭 확인할 5가지', 'customer1@example.com'), ('입주청소 전에 꼭 확인할 5가지', 'customer2@example.com'),
             ('입주청소 전에 꼭 확인할 5가지', 'seed-user1@example.com'), ('입주청소 전에 꼭 확인할 5가지', 'seed-user2@example.com'),
             ('입주청소 전에 꼭 확인할 5가지', 'expert1@example.com'),
             ('장판 vs 강마루, 이렇게 고르세요', 'customer1@example.com'), ('장판 vs 강마루, 이렇게 고르세요', 'customer2@example.com'),
             ('장판 vs 강마루, 이렇게 고르세요', 'seed-user1@example.com'), ('장판 vs 강마루, 이렇게 고르세요', 'seed-user2@example.com'),
             ('영어 단어, 하루 30개 외우는 법', 'customer2@example.com'), ('영어 단어, 하루 30개 외우는 법', 'seed-user1@example.com'),
             ('영어 단어, 하루 30개 외우는 법', 'seed-user2@example.com'),
             ('보일러 동파 막는 겨울 습관', 'customer1@example.com'), ('보일러 동파 막는 겨울 습관', 'seed-user2@example.com'),
             ('이삿짐 쌀 때 깨지는 물건 포장법', 'seed-user1@example.com'), ('이삿짐 쌀 때 깨지는 물건 포장법', 'customer2@example.com'),
             ('15평 구축 부분 리모델링 사례', 'customer1@example.com'), ('15평 구축 부분 리모델링 사례', 'seed-user2@example.com'),
             ('15평 구축 부분 리모델링 사례', 'customer2@example.com'),
             ('입주청소 전후 사진 보여드려요', 'seed-user1@example.com'), ('입주청소 전후 사진 보여드려요', 'customer2@example.com'),
             ('이사 견적이 업체마다 왜 이렇게 달라요?', 'customer1@example.com'), ('이사 견적이 업체마다 왜 이렇게 달라요?', 'customer2@example.com'),
             ('이사 견적이 업체마다 왜 이렇게 달라요?', 'seed-user2@example.com'),
             ('보일러에 E-03 에러가 계속 떠요', 'seed-user1@example.com'), ('보일러에 E-03 에러가 계속 떠요', 'customer2@example.com'),
             ('입주청소 몇 평부터 2명이 와요?', 'seed-user2@example.com')) AS v(title, email)
         JOIN post p ON p.title = v.title
         JOIN member m ON m.email = v.email;

-- 6) 댓글 수·좋아요 수를 실제 행 개수와 맞춘다
UPDATE post p
SET comment_count = (SELECT COUNT(*) FROM post_comment c WHERE c.post_id = p.id AND c.deleted_at IS NULL),
    like_count    = (SELECT COUNT(*) FROM post_like l WHERE l.post_id = p.id);

-- 홈 시안의 가상 고수 20명. 기존 계정·프로필 ID는 바꾸지 않습니다.
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile101@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','한결이사','01080000101','EXPERT',DATEADD('DAY', -41, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'원룸부터 쓰리룸까지 포장이사 전문','원룸부터 쓰리룸까지 포장이사 전문. 작업 범위와 일정은 상담 후 안내해 드립니다.',9,'관련 서비스 경력 9년입니다.',101,FALSE FROM member WHERE email='profile101@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile101@example.com')),101);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile101@example.com')),102);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile101@example.com')),103);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile101@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='강남구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile101@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='서초구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile101@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='송파구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile101@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile101@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile101@example.com'),'seed/expert-profiles/4bcfa1945637f3065184a2298d80337309f6ec70e83c0dc2e3e3257b341fd18d.jpg','expert-101.jpg','image/jpeg',8312,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/4bcfa1945637f3065184a2298d80337309f6ec70e83c0dc2e3e3257b341fd18d.jpg') WHERE email='profile101@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile102@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','반짝홈케어','01080000102','EXPERT',DATEADD('DAY', -20, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'입주 청소와 이사 청소를 하루에 끝내 드려요','입주 청소와 이사 청소를 하루에 끝내 드려요. 작업 범위와 일정은 상담 후 안내해 드립니다.',6,'관련 서비스 경력 6년입니다.',102,FALSE FROM member WHERE email='profile102@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile102@example.com')),102);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile102@example.com')),101);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile102@example.com')),103);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile102@example.com')),104);
INSERT INTO expert_profile_whole_regions (expert_profile_id,whole_regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile102@example.com')),1);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile102@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile102@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile102@example.com'),'seed/expert-profiles/4d9e4f1b2017b2029e351c445dec43f44864b4da4be785c88dc7d95e5210d202.jpg','expert-102.jpg','image/jpeg',8908,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/4d9e4f1b2017b2029e351c445dec43f44864b4da4be785c88dc7d95e5210d202.jpg') WHERE email='profile102@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile103@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','윤선생 영어','01080000103','EXPERT',DATEADD('DAY', -120, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'중고등 내신과 수능 영어를 1:1로 봐 드립니다','중고등 내신과 수능 영어를 1:1로 봐 드립니다. 작업 범위와 일정은 상담 후 안내해 드립니다.',11,'관련 서비스 경력 11년입니다.',129,FALSE FROM member WHERE email='profile103@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile103@example.com')),129);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile103@example.com')),127);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile103@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='마포구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile103@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='서대문구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile103@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile103@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile103@example.com'),'seed/expert-profiles/7a298dc757041f1c6cbf78295263cc0e25c906a28a86e0c10b0dc9381a68b098.jpg','expert-103.jpg','image/jpeg',8790,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/7a298dc757041f1c6cbf78295263cc0e25c906a28a86e0c10b0dc9381a68b098.jpg') WHERE email='profile103@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile104@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','소리공방','01080000104','EXPERT',DATEADD('DAY', -5, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'취미 피아노와 기타를 처음부터 차근차근','취미 피아노와 기타를 처음부터 차근차근. 작업 범위와 일정은 상담 후 안내해 드립니다.',7,'관련 서비스 경력 7년입니다.',165,FALSE FROM member WHERE email='profile104@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile104@example.com')),165);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile104@example.com')),166);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile104@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='마포구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile104@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile104@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile104@example.com'),'seed/expert-profiles/7ea0e5cf6d09e16e8f06e4e328e3a816d3b767a3f84bfba5eb33fe86025fb87a.jpg','expert-104.jpg','image/jpeg',9337,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/7ea0e5cf6d09e16e8f06e4e328e3a816d3b767a3f84bfba5eb33fe86025fb87a.jpg') WHERE email='profile104@example.com';
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'쇼핑몰과 예약 서비스 웹을 기획부터 배포까지','쇼핑몰과 예약 서비스 웹을 기획부터 배포까지. 작업 범위와 일정은 상담 후 안내해 드립니다.',8,'관련 서비스 경력 8년입니다.',139,TRUE FROM member WHERE email='seed-dev@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='seed-dev@example.com')),139);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='seed-dev@example.com')),138);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='seed-dev@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='seed-dev@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='seed-dev@example.com'),'seed/expert-profiles/31055f32bcdc8c99452edee7f3b116878cba7d3369df93d3c90c765e92b3e99f.jpg','expert-105.jpg','image/jpeg',9162,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/31055f32bcdc8c99452edee7f3b116878cba7d3369df93d3c90c765e92b3e99f.jpg') WHERE email='seed-dev@example.com';
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'누수 탐지와 보일러 교체를 당일에 확인합니다','누수 탐지와 보일러 교체를 당일에 확인합니다. 작업 범위와 일정은 상담 후 안내해 드립니다.',15,'관련 서비스 경력 15년입니다.',109,FALSE FROM member WHERE email='seed-plumb@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='seed-plumb@example.com')),109);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='seed-plumb@example.com')),107);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='seed-plumb@example.com')),108);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='seed-plumb@example.com')),(SELECT id FROM second_region WHERE first_region_id=3 AND name='해운대구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='seed-plumb@example.com')),(SELECT id FROM second_region WHERE first_region_id=3 AND name='수영구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='seed-plumb@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='seed-plumb@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='seed-plumb@example.com'),'seed/expert-profiles/11971b4bd9d62252bb8fb03646c3bbe99cfe86660147b984c65ac082d5acbfb8.jpg','expert-106.jpg','image/jpeg',9729,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/11971b4bd9d62252bb8fb03646c3bbe99cfe86660147b984c65ac082d5acbfb8.jpg') WHERE email='seed-plumb@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile107@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','온집 인테리어','01080000107','EXPERT',DATEADD('DAY', -8, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'욕실과 주방 부분 시공을 깔끔하게','욕실과 주방 부분 시공을 깔끔하게. 작업 범위와 일정은 상담 후 안내해 드립니다.',12,'관련 서비스 경력 12년입니다.',113,FALSE FROM member WHERE email='profile107@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile107@example.com')),113);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile107@example.com')),112);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile107@example.com')),114);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile107@example.com')),115);
INSERT INTO expert_profile_whole_regions (expert_profile_id,whole_regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile107@example.com')),4);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile107@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile107@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile107@example.com'),'seed/expert-profiles/5c8c6e0c8a0ad75febadbc88b59c9a13ae1525e06a564c21ac368085f840b557.jpg','expert-107.jpg','image/jpeg',8976,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/5c8c6e0c8a0ad75febadbc88b59c9a13ae1525e06a564c21ac368085f840b557.jpg') WHERE email='profile107@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile108@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','담다 스냅','01080000108','EXPERT',DATEADD('DAY', -1, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'웨딩 스냅과 가족 사진을 자연광으로 담아요','웨딩 스냅과 가족 사진을 자연광으로 담아요. 작업 범위와 일정은 상담 후 안내해 드립니다.',5,'관련 서비스 경력 5년입니다.',120,TRUE FROM member WHERE email='profile108@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile108@example.com')),120);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile108@example.com')),118);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile108@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile108@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile108@example.com'),'seed/expert-profiles/0b9ff5c15aa191c59cdb086076497351af362e3c358f63eb6e255730e3e8db7f.jpg','expert-108.jpg','image/jpeg',8154,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/0b9ff5c15aa191c59cdb086076497351af362e3c358f63eb6e255730e3e8db7f.jpg') WHERE email='profile108@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile109@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','정리하는 하루','01080000109','EXPERT',DATEADD('DAY', -6, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'가전 분해 세척과 가구 청소를 함께 맡겨 주세요','가전 분해 세척과 가구 청소를 함께 맡겨 주세요. 작업 범위와 일정은 상담 후 안내해 드립니다.',4,'관련 서비스 경력 4년입니다.',103,FALSE FROM member WHERE email='profile109@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile109@example.com')),103);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile109@example.com')),101);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile109@example.com')),(SELECT id FROM second_region WHERE first_region_id=5 AND name='연수구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile109@example.com')),(SELECT id FROM second_region WHERE first_region_id=5 AND name='남동구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile109@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile109@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile109@example.com'),'seed/expert-profiles/f0282303613ec56e913cf46ea95548d59df5102b0d368d8e9c3be025e32af0d7.jpg','expert-109.jpg','image/jpeg',8651,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/f0282303613ec56e913cf46ea95548d59df5102b0d368d8e9c3be025e32af0d7.jpg') WHERE email='profile109@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile110@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','김코치 피트니스','01080000110','EXPERT',DATEADD('DAY', -60, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'체형 교정 중심의 1:1 퍼스널 트레이닝','체형 교정 중심의 1:1 퍼스널 트레이닝. 작업 범위와 일정은 상담 후 안내해 드립니다.',10,'관련 서비스 경력 10년입니다.',174,FALSE FROM member WHERE email='profile110@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile110@example.com')),174);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile110@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='강남구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile110@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile110@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile110@example.com'),'seed/expert-profiles/d6df33572f3f4c3a8168af7114cc989710c9f32d62f10d5238be01f235b8542e.jpg','expert-110.jpg','image/jpeg',7671,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/d6df33572f3f4c3a8168af7114cc989710c9f32d62f10d5238be01f235b8542e.jpg') WHERE email='profile110@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile111@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','문고리 수리','01080000111','EXPERT',DATEADD('DAY', -4, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'도어락 교체와 방충망, 창문 수리','도어락 교체와 방충망, 창문 수리. 작업 범위와 일정은 상담 후 안내해 드립니다.',8,'관련 서비스 경력 8년입니다.',108,FALSE FROM member WHERE email='profile111@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile111@example.com')),108);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile111@example.com')),107);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile111@example.com')),109);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile111@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='송파구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile111@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='강동구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile111@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile111@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile111@example.com'),'seed/expert-profiles/e6c78e0d7451448ed80ea8b6c53b8642838b6c93c77b8cf55af4abd470351533.jpg','expert-111.jpg','image/jpeg',8103,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/e6c78e0d7451448ed80ea8b6c53b8642838b6c93c77b8cf55af4abd470351533.jpg') WHERE email='profile111@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile112@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','해운대 이사짐센터','01080000112','EXPERT',DATEADD('DAY', -300, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'부산 전 지역 소형 이사와 용달','부산 전 지역 소형 이사와 용달. 작업 범위와 일정은 상담 후 안내해 드립니다.',18,'관련 서비스 경력 18년입니다.',101,FALSE FROM member WHERE email='profile112@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile112@example.com')),101);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile112@example.com')),102);
INSERT INTO expert_profile_whole_regions (expert_profile_id,whole_regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile112@example.com')),3);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile112@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile112@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile112@example.com'),'seed/expert-profiles/add13d2afd287a6e256ff4a48bbd03790caf5439232620b17e44f7460541c5ad.jpg','expert-112.jpg','image/jpeg',10970,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/add13d2afd287a6e256ff4a48bbd03790caf5439232620b17e44f7460541c5ad.jpg') WHERE email='profile112@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile113@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','커리어노트','01080000113','EXPERT',DATEADD('DAY', -90, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'자소서 첨삭과 직무 면접 준비를 1:1로','자소서 첨삭과 직무 면접 준비를 1:1로. 작업 범위와 일정은 상담 후 안내해 드립니다.',9,'관련 서비스 경력 9년입니다.',150,FALSE FROM member WHERE email='profile113@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile113@example.com')),150);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile113@example.com')),151);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile113@example.com')),(SELECT id FROM second_region WHERE first_region_id=4 AND name='수성구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile113@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile113@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile113@example.com'),'seed/expert-profiles/51a78cd14d3261add2fad59530b0856e14c62824e4e85577e0d91879bb25f0c7.jpg','expert-113.jpg','image/jpeg',8494,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/51a78cd14d3261add2fad59530b0856e14c62824e4e85577e0d91879bb25f0c7.jpg') WHERE email='profile113@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile114@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','그림숲 아틀리에','01080000114','EXPERT',DATEADD('DAY', -9, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'성인 취미 드로잉과 수채화 클래스','성인 취미 드로잉과 수채화 클래스. 작업 범위와 일정은 상담 후 안내해 드립니다.',6,'관련 서비스 경력 6년입니다.',167,FALSE FROM member WHERE email='profile114@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile114@example.com')),167);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile114@example.com')),165);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile114@example.com')),(SELECT id FROM second_region WHERE first_region_id=5 AND name='연수구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile114@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile114@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile114@example.com'),'seed/expert-profiles/a09c9e34c10da8def776d94ac1f0b0d677124888accc6086e99f55914cf794f8.jpg','expert-114.jpg','image/jpeg',11083,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/a09c9e34c10da8def776d94ac1f0b0d677124888accc6086e99f55914cf794f8.jpg') WHERE email='profile114@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile115@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','새길 노무','01080000115','EXPERT',DATEADD('DAY', -2, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'임금 체불과 근로계약 상담을 쉽게 풀어 드려요','임금 체불과 근로계약 상담을 쉽게 풀어 드려요. 작업 범위와 일정은 상담 후 안내해 드립니다.',13,'관련 서비스 경력 13년입니다.',187,TRUE FROM member WHERE email='profile115@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile115@example.com')),187);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile115@example.com')),186);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile115@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile115@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile115@example.com'),'seed/expert-profiles/e863f6c662a4aacfc0a8615e58956e0c14fa0f3fadbd49205a4b133b585f5064.jpg','expert-115.jpg','image/jpeg',8021,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/e863f6c662a4aacfc0a8615e58956e0c14fa0f3fadbd49205a4b133b585f5064.jpg') WHERE email='profile115@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile116@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','마음쉼표 상담','01080000116','EXPERT',DATEADD('DAY', -30, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'직장인 스트레스와 관계 고민 상담','직장인 스트레스와 관계 고민 상담. 작업 범위와 일정은 상담 후 안내해 드립니다.',10,'관련 서비스 경력 10년입니다.',190,FALSE FROM member WHERE email='profile116@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile116@example.com')),190);
INSERT INTO expert_profile_whole_regions (expert_profile_id,whole_regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile116@example.com')),1);
INSERT INTO expert_profile_whole_regions (expert_profile_id,whole_regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile116@example.com')),9);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile116@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile116@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile116@example.com'),'seed/expert-profiles/c898b516436f0720d021db2b5ab3021a033f26ccc490c82a93bf01403b30ce3a.jpg','expert-116.jpg','image/jpeg',8183,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/c898b516436f0720d021db2b5ab3021a033f26ccc490c82a93bf01403b30ce3a.jpg') WHERE email='profile116@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile117@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','한빛 카케어','01080000117','EXPERT',DATEADD('DAY', -150, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'출장 세차와 실내 클리닝, 소모품 교체','출장 세차와 실내 클리닝, 소모품 교체. 작업 범위와 일정은 상담 후 안내해 드립니다.',16,'관련 서비스 경력 16년입니다.',184,FALSE FROM member WHERE email='profile117@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile117@example.com')),184);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile117@example.com')),(SELECT id FROM second_region WHERE first_region_id=3 AND name='해운대구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile117@example.com')),(SELECT id FROM second_region WHERE first_region_id=3 AND name='수영구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile117@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile117@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile117@example.com'),'seed/expert-profiles/7cdeb1c265b796af7accd53ebc2b95e23f15951040c23fa2660042a9c2f66511.jpg','expert-117.jpg','image/jpeg',9512,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/7cdeb1c265b796af7accd53ebc2b95e23f15951040c23fa2660042a9c2f66511.jpg') WHERE email='profile117@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile118@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','맑은창 줄눈','01080000118','EXPERT',DATEADD('DAY', -75, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'욕실 줄눈과 실리콘 재시공을 반나절에','욕실 줄눈과 실리콘 재시공을 반나절에. 작업 범위와 일정은 상담 후 안내해 드립니다.',9,'관련 서비스 경력 9년입니다.',113,FALSE FROM member WHERE email='profile118@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile118@example.com')),113);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile118@example.com')),112);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile118@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='마포구'));
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile118@example.com')),(SELECT id FROM second_region WHERE first_region_id=1 AND name='은평구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile118@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile118@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile118@example.com'),'seed/expert-profiles/cac85a3ea10030c3209c530950e0af9b8fcec2e578fd0ef0c2ac9df7ed101551.jpg','expert-118.jpg','image/jpeg',9628,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/cac85a3ea10030c3209c530950e0af9b8fcec2e578fd0ef0c2ac9df7ed101551.jpg') WHERE email='profile118@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile119@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','하늘 웨딩플래너','01080000119','EXPERT',DATEADD('DAY', -200, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'스드메 견적 비교부터 본식 진행까지','스드메 견적 비교부터 본식 진행까지. 작업 범위와 일정은 상담 후 안내해 드립니다.',8,'관련 서비스 경력 8년입니다.',118,FALSE FROM member WHERE email='profile119@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile119@example.com')),118);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile119@example.com')),119);
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile119@example.com')),120);
INSERT INTO expert_profile_whole_regions (expert_profile_id,whole_regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile119@example.com')),3);
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile119@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile119@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile119@example.com'),'seed/expert-profiles/c0c93434c9ea6be62031a86f8b56ddb7d5eeb245a31b2aff54859409483b7e1f.jpg','expert-119.jpg','image/jpeg',10016,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/c0c93434c9ea6be62031a86f8b56ddb7d5eeb245a31b2aff54859409483b7e1f.jpg') WHERE email='profile119@example.com';
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version) VALUES ('profile120@example.com','$2a$10$N8on.wPrFPr5oauwmXJQeeZoSnGoNxkC6q4I7eHMxNy88fcZdaO6C','오늘의 요가','01080000120','EXPERT',DATEADD('DAY', -45, CURRENT_TIMESTAMP),0);
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide) SELECT id,'소규모 그룹과 1:1 요가 수업','소규모 그룹과 1:1 요가 수업. 작업 범위와 일정은 상담 후 안내해 드립니다.',6,'관련 서비스 경력 6년입니다.',174,FALSE FROM member WHERE email='profile120@example.com';
INSERT INTO expert_profile_services (expert_profile_id,services_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile120@example.com')),174);
INSERT INTO expert_profile_regions (expert_profile_id,regions_id) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE email='profile120@example.com')),(SELECT id FROM second_region WHERE first_region_id=4 AND name='수성구'));
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at) SELECT id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM member WHERE email='profile120@example.com' AND NOT EXISTS (SELECT 1 FROM credit_wallet WHERE member_id=(SELECT id FROM member WHERE email='profile120@example.com'));
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE email='profile120@example.com'),'seed/expert-profiles/56fb326aeb68bf352bafa49e107b0a5294f796ecc5e87c89eda1bbaca96a1866.jpg','expert-120.jpg','image/jpeg',9064,'PUBLIC',CURRENT_TIMESTAMP);
UPDATE member SET image_id=(SELECT id FROM file WHERE storage_path='seed/expert-profiles/56fb326aeb68bf352bafa49e107b0a5294f796ecc5e87c89eda1bbaca96a1866.jpg') WHERE email='profile120@example.com';


-- 기존 Supabase 커뮤니티 사진의 서비스별 작업 사례. 파일 소유자는 해당 고수입니다.
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='한결이사'),'seed/expert-work/16de8671c1456f24e95274b607c9bbbe-101-0.jpg','promo-moving-1.jpg','image/jpeg',194916,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='한결이사')),(SELECT id FROM file WHERE storage_path='seed/expert-work/16de8671c1456f24e95274b607c9bbbe-101-0.jpg'),'포장이사 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='한결이사'),'seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-101-1.jpg','promo-moving-2.jpg','image/jpeg',183928,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='한결이사')),(SELECT id FROM file WHERE storage_path='seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-101-1.jpg'),'이삿짐 운반 작업 사례',1);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='반짝홈케어'),'seed/expert-work/bfd792b1d7fa53d5c2190c964aa29798-102-0.jpg','promo-clean-1.jpg','image/jpeg',142658,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='반짝홈케어')),(SELECT id FROM file WHERE storage_path='seed/expert-work/bfd792b1d7fa53d5c2190c964aa29798-102-0.jpg'),'입주 청소 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='반짝홈케어'),'seed/expert-work/ac44f8883c34a4deae39fc8b6cb82da0-102-1.jpg','promo-clean-2.jpg','image/jpeg',138881,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='반짝홈케어')),(SELECT id FROM file WHERE storage_path='seed/expert-work/ac44f8883c34a4deae39fc8b6cb82da0-102-1.jpg'),'청소 마무리 작업 사례',1);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='윤선생 영어'),'seed/expert-work/216cbc29b8a476ed0a26d81d837f46df-103-0.jpg','promo-english-1.jpg','image/jpeg',192334,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='윤선생 영어')),(SELECT id FROM file WHERE storage_path='seed/expert-work/216cbc29b8a476ed0a26d81d837f46df-103-0.jpg'),'영어 수업 진행 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='코드메이트'),'seed/expert-work/f6d0aa6e187ba38f665fa545f92fa073-105-0.jpg','promo-dev-1.jpg','image/jpeg',139255,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='코드메이트')),(SELECT id FROM file WHERE storage_path='seed/expert-work/f6d0aa6e187ba38f665fa545f92fa073-105-0.jpg'),'웹 개발 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='바른설비'),'seed/expert-work/40c7ffe8fa9ddd4c1bfe38372c9648f7-106-0.jpg','promo-boiler-1.jpg','image/jpeg',178861,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='바른설비')),(SELECT id FROM file WHERE storage_path='seed/expert-work/40c7ffe8fa9ddd4c1bfe38372c9648f7-106-0.jpg'),'보일러 설비 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='온집 인테리어'),'seed/expert-work/3d3327ec26e5d74025e457aeca358912-107-0.jpg','promo-interior-1.jpg','image/jpeg',209409,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='온집 인테리어')),(SELECT id FROM file WHERE storage_path='seed/expert-work/3d3327ec26e5d74025e457aeca358912-107-0.jpg'),'부분 인테리어 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='온집 인테리어'),'seed/expert-work/fe32925474575a143b11465fc473b193-107-1.jpg','promo-interior-2.jpg','image/jpeg',126840,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='온집 인테리어')),(SELECT id FROM file WHERE storage_path='seed/expert-work/fe32925474575a143b11465fc473b193-107-1.jpg'),'실내 시공 작업 사례',1);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='담다 스냅'),'seed/expert-work/e1b4e7d25f66716ef603bffdce076b86-108-0.jpg','promo-snap-1.jpg','image/jpeg',94193,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='담다 스냅')),(SELECT id FROM file WHERE storage_path='seed/expert-work/e1b4e7d25f66716ef603bffdce076b86-108-0.jpg'),'스냅 촬영 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='담다 스냅'),'seed/expert-work/e5d6e1eaa9c945df8921783233acafee-108-1.jpg','promo-snap-2.jpg','image/jpeg',89410,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='담다 스냅')),(SELECT id FROM file WHERE storage_path='seed/expert-work/e5d6e1eaa9c945df8921783233acafee-108-1.jpg'),'인물 촬영 작업 사례',1);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='정리하는 하루'),'seed/expert-work/692893d76be445e26009371fd2909ae9-109-0.jpg','promo-aircon-1.jpg','image/jpeg',175712,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='정리하는 하루')),(SELECT id FROM file WHERE storage_path='seed/expert-work/692893d76be445e26009371fd2909ae9-109-0.jpg'),'에어컨 청소 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='해운대 이사짐센터'),'seed/expert-work/16de8671c1456f24e95274b607c9bbbe-112-0.jpg','promo-moving-1.jpg','image/jpeg',194916,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='해운대 이사짐센터')),(SELECT id FROM file WHERE storage_path='seed/expert-work/16de8671c1456f24e95274b607c9bbbe-112-0.jpg'),'포장이사 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='해운대 이사짐센터'),'seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-112-1.jpg','promo-moving-2.jpg','image/jpeg',183928,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='해운대 이사짐센터')),(SELECT id FROM file WHERE storage_path='seed/expert-work/008a9a87a2c15102ef303eb2c31bf770-112-1.jpg'),'이삿짐 운반 작업 사례',1);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='맑은창 줄눈'),'seed/expert-work/3d3327ec26e5d74025e457aeca358912-118-0.jpg','promo-interior-1.jpg','image/jpeg',209409,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='맑은창 줄눈')),(SELECT id FROM file WHERE storage_path='seed/expert-work/3d3327ec26e5d74025e457aeca358912-118-0.jpg'),'부분 인테리어 작업 사례',0);
INSERT INTO file (uploader_id,storage_path,original_name,content_type,size,visibility,created_at) VALUES ((SELECT id FROM member WHERE nickname='맑은창 줄눈'),'seed/expert-work/fe32925474575a143b11465fc473b193-118-1.jpg','promo-interior-2.jpg','image/jpeg',126840,'PUBLIC',CURRENT_TIMESTAMP);
INSERT INTO work_image (expert_profile_id,file_id,description,display_order) VALUES ((SELECT id FROM expert_profile WHERE member_id=(SELECT id FROM member WHERE nickname='맑은창 줄눈')),(SELECT id FROM file WHERE storage_path='seed/expert-work/fe32925474575a143b11465fc473b193-118-1.jpg'),'실내 시공 작업 사례',1);
