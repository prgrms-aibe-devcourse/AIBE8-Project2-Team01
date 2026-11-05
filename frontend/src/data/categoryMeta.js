import { FIRST_CATEGORIES } from './baseData.js';

// 대분류마다 입체 오브젝트를 하나씩 정한다.
// 아이콘은 public/efy-assets/icons의 생성 이미지를 쓴다. 화면에는 256px 사본(icons-256)을 쓴다.
const META = {
  1: { object: 'truck', short: '이사·청소' },
  2: { object: 'toolbox', short: '설치·수리' },
  3: { object: 'paint_roller', short: '인테리어' },
  4: { object: 'balloons', short: '이벤트·뷰티' },
  5: { object: 'books', short: '과외' },
  6: { object: 'laptop', short: '외주' },
  7: { object: 'briefcase', short: '취업·직무' },
  8: { object: 'palette', short: '취미·자기계발' },
  9: { object: 'car', short: '자동차' },
  10: { object: 'scale', short: '법률·금융' },
  11: { object: 'shapes', short: '기타' },
};

// 기존 슬롯 이름과 efy-assets 파일 이름 연결 (efy-assets/README.md 표 기준)
const ICON_FILE = { truck: 'moving-truck', paint_roller: 'paint-roller' };
export const objectSrc = (name) => `/efy-assets/icons-256/${ICON_FILE[name] ?? name}.png`;
// 시안용 가상 인물 사진 (AI 생성). 실제 고수 사진이 아니다.
export const expertImage = (id) => `/efy-assets/portraits/expert-${id}.jpg`;
// 작은 원형 아바타용 얼굴 사진. 프로필 v2는 작업 장면이라 원본을 줄이면 얼굴이 작아서 얼굴 주변을 따로 잘랐다.
export const expertFace = (id) => `/efy-assets/faces/expert-${id}.jpg`;

export const CATEGORIES = FIRST_CATEGORIES.map((c) => ({ ...c, ...META[c.id] }));

// 소분류 중 대분류 오브젝트보다 더 구체적인 오브젝트가 있는 경우
const SUB_OBJECT = {
  102: 'broom', 103: 'broom', 104: 'broom', 105: 'broom', 106: 'package',
  107: 'wrench', 108: 'wrench', 109: 'wrench', 110: 'wrench', 111: 'wrench',
  118: 'gift', 120: 'camera', 122: 'gift', 136: 'camera', 169: 'camera',
};

const SUB_INDEX = new Map();
CATEGORIES.forEach((c) =>
  c.subs.forEach((s) =>
    SUB_INDEX.set(s.id, { ...s, parentId: c.id, parentName: c.name, object: SUB_OBJECT[s.id] ?? c.object }),
  ),
);

export const ALL_SUBS = [...SUB_INDEX.values()];
export const getSub = (id) => SUB_INDEX.get(id);

// 히어로의 빠른 선택. 실제 소분류 ID만 사용한다. 이용량 기준이 아니라 편집 선택이다.
export const QUICK_PICKS = [101, 102, 129, 165, 139, 109];

// 상황별 묶음. 소분류는 모두 실제 데이터이고 묶음은 편집 기준이다.
export const COLLECTIONS = [
  {
    id: 'moving',
    title: '이사를 앞두고 있다면',
    body: '짐을 옮기기 전후로 함께 맡기는 일을 모았습니다.',
    objects: ['package', 'truck', 'broom'],
    subs: [101, 102, 103, 106],
  },
  {
    id: 'repair',
    title: '집에 손볼 곳이 생겼다면',
    body: '물이 새거나 문이 뻑뻑할 때 바로 찾을 수 있습니다.',
    objects: ['wrench', 'toolbox', 'paint_roller'],
    subs: [109, 108, 113, 115],
  },
  {
    id: 'learn',
    title: '새로 배워 보고 싶다면',
    body: '시간과 장소를 맞춰 주는 1:1 레슨부터 시작해 보세요.',
    objects: ['books', 'palette', 'laptop'],
    subs: [129, 165, 167, 174],
  },
  {
    id: 'event',
    title: '결혼과 행사를 준비한다면',
    body: '촬영과 메이크업부터 행사 진행까지 나눠서 맡기세요.',
    objects: ['gift', 'camera', 'balloons'],
    subs: [118, 120, 119, 124],
  },
];

// 기능 화면용: 서버가 준 소분류 ID나 이름으로 입체 아이콘을 찾는다. 못 찾으면 기타 아이콘을 쓴다.
const SUB_BY_NAME = new Map(ALL_SUBS.map((s) => [s.name, s]));
const FIRST_BY_NAME = new Map(CATEGORIES.map((c) => [c.name, c]));
export function categoryIconSrc({ secondId, secondName, firstName } = {}) {
  const sub = (secondId && getSub(secondId)) || (secondName && SUB_BY_NAME.get(secondName));
  if (sub) return objectSrc(sub.object);
  const first = firstName && FIRST_BY_NAME.get(firstName);
  return objectSrc(first ? first.object : 'shapes');
}
