// ⚠ 시안용 예시 데이터다. 실제 고수·게시글·집계 결과가 아니다.
// 필드는 백엔드 ExpertProfileResponse, PostSummaryResponse, QuoteResponse 구조를 따른다.
// joinedDaysAgo, bookmarkGain7d처럼 백엔드에 아직 없는 값은 주석으로 표시한다.

export const SAMPLE_EXPERTS = [
  {
    id: 101, nickname: '한결이사', shortIntro: '원룸부터 쓰리룸까지 포장이사 전문', careerYears: 9,
    primaryCategoryId: 101, serviceCount: 3, nationwide: false, wholeRegions: [], regions: ['서울 강남구', '서울 서초구', '서울 송파구'],
    workImageCount: 12, joinedDaysAgo: 41, bookmarkGain7d: 38,
  },
  {
    id: 102, nickname: '반짝홈케어', shortIntro: '입주 청소와 이사 청소를 하루에 끝내 드려요', careerYears: 6,
    primaryCategoryId: 102, serviceCount: 4, nationwide: false, wholeRegions: ['서울'], regions: [],
    workImageCount: 24, joinedDaysAgo: 20, bookmarkGain7d: 52,
  },
  {
    id: 103, nickname: '윤선생 영어', shortIntro: '중고등 내신과 수능 영어를 1:1로 봐 드립니다', careerYears: 11,
    primaryCategoryId: 129, serviceCount: 2, nationwide: false, wholeRegions: [], regions: ['서울 마포구', '서울 서대문구'],
    workImageCount: 0, joinedDaysAgo: 120, bookmarkGain7d: 17,
  },
  {
    id: 104, nickname: '소리공방', shortIntro: '취미 피아노와 기타를 처음부터 차근차근', careerYears: 7,
    primaryCategoryId: 165, serviceCount: 2, nationwide: false, wholeRegions: [], regions: ['서울 마포구'],
    workImageCount: 6, joinedDaysAgo: 5, bookmarkGain7d: 9,
  },
  {
    id: 105, nickname: '코드메이트', shortIntro: '쇼핑몰과 예약 서비스 웹을 기획부터 배포까지', careerYears: 8,
    primaryCategoryId: 139, serviceCount: 2, nationwide: true, wholeRegions: [], regions: [],
    workImageCount: 9, joinedDaysAgo: 2, bookmarkGain7d: 44,
  },
  {
    id: 106, nickname: '바른설비', shortIntro: '누수 탐지와 보일러 교체를 당일에 확인합니다', careerYears: 15,
    primaryCategoryId: 109, serviceCount: 3, nationwide: false, wholeRegions: [], regions: ['부산 해운대구', '부산 수영구'],
    workImageCount: 18, joinedDaysAgo: 210, bookmarkGain7d: 26,
  },
  {
    id: 107, nickname: '온집 인테리어', shortIntro: '욕실과 주방 부분 시공을 깔끔하게', careerYears: 12,
    primaryCategoryId: 113, serviceCount: 4, nationwide: false, wholeRegions: ['대구'], regions: [],
    workImageCount: 31, joinedDaysAgo: 8, bookmarkGain7d: 21,
  },
  {
    id: 108, nickname: '담다 스냅', shortIntro: '웨딩 스냅과 가족 사진을 자연광으로 담아요', careerYears: 5,
    primaryCategoryId: 120, serviceCount: 2, nationwide: true, wholeRegions: [], regions: [],
    workImageCount: 40, joinedDaysAgo: 1, bookmarkGain7d: 61,
  },
  {
    id: 109, nickname: '정리하는 하루', shortIntro: '가전 분해 세척과 가구 청소를 함께 맡겨 주세요', careerYears: 4,
    primaryCategoryId: 103, serviceCount: 2, nationwide: false, wholeRegions: [], regions: ['인천 연수구', '인천 남동구'],
    workImageCount: 14, joinedDaysAgo: 6, bookmarkGain7d: 12,
  },
  {
    id: 110, nickname: '김코치 피트니스', shortIntro: '체형 교정 중심의 1:1 퍼스널 트레이닝', careerYears: 10,
    primaryCategoryId: 174, serviceCount: 1, nationwide: false, wholeRegions: [], regions: ['서울 강남구'],
    workImageCount: 7, joinedDaysAgo: 60, bookmarkGain7d: 33,
  },
  {
    id: 111, nickname: '문고리 수리', shortIntro: '도어락 교체와 방충망, 창문 수리', careerYears: 8,
    primaryCategoryId: 108, serviceCount: 3, nationwide: false, wholeRegions: [], regions: ['서울 송파구', '서울 강동구'],
    workImageCount: 11, joinedDaysAgo: 4, bookmarkGain7d: 7,
  },
  {
    id: 112, nickname: '해운대 이사짐센터', shortIntro: '부산 전 지역 소형 이사와 용달', careerYears: 18,
    primaryCategoryId: 101, serviceCount: 2, nationwide: false, wholeRegions: ['부산'], regions: [],
    workImageCount: 10, joinedDaysAgo: 300, bookmarkGain7d: 15,
  },
  {
    id: 113, nickname: '커리어노트', shortIntro: '자소서 첨삭과 직무 면접 준비를 1:1로', careerYears: 9,
    primaryCategoryId: 150, serviceCount: 2, nationwide: false, wholeRegions: [], regions: ['대구 수성구'],
    workImageCount: 0, joinedDaysAgo: 90, bookmarkGain7d: 19,
  },
  {
    id: 114, nickname: '그림숲 아틀리에', shortIntro: '성인 취미 드로잉과 수채화 클래스', careerYears: 6,
    primaryCategoryId: 167, serviceCount: 2, nationwide: false, wholeRegions: [], regions: ['인천 연수구'],
    workImageCount: 22, joinedDaysAgo: 9, bookmarkGain7d: 28,
  },
  {
    id: 115, nickname: '새길 노무', shortIntro: '임금 체불과 근로계약 상담을 쉽게 풀어 드려요', careerYears: 13,
    primaryCategoryId: 187, serviceCount: 2, nationwide: true, wholeRegions: [], regions: [],
    workImageCount: 0, joinedDaysAgo: 2, bookmarkGain7d: 5,
  },
  {
    id: 116, nickname: '마음쉼표 상담', shortIntro: '직장인 스트레스와 관계 고민 상담', careerYears: 10,
    primaryCategoryId: 190, serviceCount: 1, nationwide: false, wholeRegions: ['서울', '경기'], regions: [],
    workImageCount: 0, joinedDaysAgo: 30, bookmarkGain7d: 47,
  },
  {
    id: 117, nickname: '한빛 카케어', shortIntro: '출장 세차와 실내 클리닝, 소모품 교체', careerYears: 16,
    primaryCategoryId: 184, serviceCount: 1, nationwide: false, wholeRegions: [], regions: ['부산 해운대구', '부산 수영구'],
    workImageCount: 18, joinedDaysAgo: 150, bookmarkGain7d: 29,
  },
  {
    id: 118, nickname: '맑은창 줄눈', shortIntro: '욕실 줄눈과 실리콘 재시공을 반나절에', careerYears: 9,
    primaryCategoryId: 113, serviceCount: 2, nationwide: false, wholeRegions: [], regions: ['서울 마포구', '서울 은평구'],
    workImageCount: 27, joinedDaysAgo: 75, bookmarkGain7d: 36,
  },
  {
    id: 119, nickname: '하늘 웨딩플래너', shortIntro: '스드메 견적 비교부터 본식 진행까지', careerYears: 8,
    primaryCategoryId: 118, serviceCount: 3, nationwide: false, wholeRegions: ['부산'], regions: [],
    workImageCount: 18, joinedDaysAgo: 200, bookmarkGain7d: 31,
  },
  {
    id: 120, nickname: '오늘의 요가', shortIntro: '소규모 그룹과 1:1 요가 수업', careerYears: 6,
    primaryCategoryId: 174, serviceCount: 1, nationwide: false, wholeRegions: [], regions: ['대구 수성구'],
    workImageCount: 10, joinedDaysAgo: 45, bookmarkGain7d: 24,
  },
];

// 섹션마다 다른 고수를 보여주기 위해 신규와 급상승 목록을 서로 겹치지 않게 나눈다.
export const NEW_EXPERTS = SAMPLE_EXPERTS.filter((e) => e.joinedDaysAgo <= 14).sort((a, b) => a.joinedDaysAgo - b.joinedDaysAgo);
const NEW_IDS = new Set(NEW_EXPERTS.map((e) => e.id));
// 첫 지역 탭(서울 강남구)에 직접 걸리는 고수도 급상승에서 빼서 첫 화면 흐름에서 같은 사람이 반복되지 않게 한다.
const servesFirstTab = (e) => e.regions.includes('서울 강남구') || e.wholeRegions.includes('서울');
export const RISING_EXPERTS = SAMPLE_EXPERTS.filter((e) => !NEW_IDS.has(e.id) && !servesFirstTab(e))
  .sort((a, b) => b.bookmarkGain7d - a.bookmarkGain7d)
  .slice(0, 6);
export const FEATURED_IDS = new Set([...NEW_IDS, ...RISING_EXPERTS.map((e) => e.id)]);

// 우리 동네 고수 탭. 지역 이름은 실제 시군구다.
export const LOCAL_TABS = ['서울 강남구', '서울 마포구', '부산 해운대구', '대구 수성구', '인천 연수구'];

// 미리보기에 도착하는 예시 견적. 고른 서비스와 같은 대분류의 예시 고수가 보낸 것으로 보여준다.
// QuoteResponse의 expertNickname, price를 흉내 낸다.
const BASE_PRICE = { 1: 450000, 2: 120000, 3: 900000, 4: 350000, 5: 300000, 6: 3500000, 7: 200000, 8: 180000, 9: 150000, 10: 250000, 11: 100000 };
const SPREAD = [1, 0.88, 1.12];
export function sampleQuotesFor(sub, getSub) {
  const same = SAMPLE_EXPERTS.filter((e) => getSub(e.primaryCategoryId).parentId === sub.parentId)
    .sort((a, b) => (a.primaryCategoryId === sub.id ? -1 : 0) - (b.primaryCategoryId === sub.id ? -1 : 0))
    .slice(0, 3);
  const base = BASE_PRICE[sub.parentId] ?? 200000;
  return same.map((expert, i) => ({ expert, price: Math.round((base * SPREAD[i]) / 10000) * 10000 }));
}

// 커뮤니티 공개 게시글 예시. 실제 API: GET /api/public/posts?boardType=TIP|QUESTION
export const SAMPLE_POSTS = {
  TIP: [
    { postId: 1, title: '이사 견적 비교할 때 꼭 확인할 다섯 가지', memberNickname: '한결이사', category: '이사', commentCount: 14, likeCount: 86, thumb: { bg: '#DDEBFF', icons: ['truck', 'package'] } },
    { postId: 2, title: '입주 청소 전 집주인에게 먼저 물어볼 것', memberNickname: '반짝홈케어', category: '입주/집 청소', commentCount: 9, likeCount: 52, thumb: { bg: '#E3F4EC', icons: ['broom', 'package'] } },
    { postId: 3, title: '겨울 오기 전에 보일러 점검하는 순서', memberNickname: '바른설비', category: '수도/보일러/전기', commentCount: 6, likeCount: 41, thumb: { bg: '#FFE9DA', icons: ['toolbox', 'wrench'] } },
    { postId: 4, title: '처음 그림을 배울 때 준비하면 좋은 것', memberNickname: '그림숲 아틀리에', category: '미술/드로잉', commentCount: 3, likeCount: 27, thumb: { bg: '#EEE8FF', icons: ['palette', 'books'] } },
  ],
  QUESTION: [
    { postId: 11, title: '반지하 이사인데 사다리차가 꼭 필요할까요?', memberNickname: '이사준비중', category: '이사', commentCount: 5, likeCount: 8, thumb: { bg: '#FFF1CF', icons: ['package', 'truck'] } },
    { postId: 12, title: '중2 영어 과외 주 몇 회가 적당한가요?', memberNickname: '학부모', category: '영어', commentCount: 7, likeCount: 12, thumb: { bg: '#DDF3EE', icons: ['books', 'laptop'] } },
    { postId: 13, title: '욕실 줄눈만 다시 할 수 있나요?', memberNickname: '셀프수리초보', category: '부분 인테리어', commentCount: 4, likeCount: 6, thumb: { bg: '#E6EEFB', icons: ['paint_roller', 'wrench'] } },
    { postId: 14, title: '외주 개발 계약서에 꼭 넣어야 할 항목', memberNickname: '스타트업대표', category: '개발 외주', commentCount: 11, likeCount: 19, thumb: { bg: '#F1EEE8', icons: ['laptop', 'briefcase'] } },
  ],
};

// 고수들의 작업 소개 (예시). 작업 사진 자리에는 가상 인물의 작업 장면 사진(프로필 v2)을 쓴다.
// 서비스는 그 고수의 대표 서비스를 쓴다.
// 백엔드에는 고수별 작업 사진(workImages)이 있지만 여러 고수의 작업을 모아 보는 공개 목록 API는 아직 없다.
export const WORK_SHOWCASE = [
  { id: 1, expertId: 109, title: '에어컨 분해 세척, 여름 오기 전에 미리 하세요' },
  { id: 2, expertId: 101, title: '투룸 포장이사, 하루 만에 정리까지 끝낸 사례' },
  { id: 3, expertId: 107, title: '20년 된 주방 상판과 타일만 바꾼 부분 시공' },
  { id: 4, expertId: 106, title: '욕실 세면대 누수, 배관 교체로 바로 잡았어요' },
  { id: 5, expertId: 114, title: '첫 수채화 수업, 4주 만에 완성한 풍경 그림' },
  { id: 6, expertId: 118, title: '곰팡이 핀 욕실 줄눈과 실리콘 반나절 재시공' },
  { id: 7, expertId: 117, title: '출장 세차로 실내 시트까지 새 차처럼' },
  { id: 8, expertId: 102, title: '입주 전 주방 기름때와 창틀 먼지 청소' },
];
