import { getSub } from '../data/categoryMeta.js';

const SIDO_SHORT = {
  서울특별시: '서울',
  전남광주통합특별시: '전남광주',
  부산광역시: '부산',
  대구광역시: '대구',
  인천광역시: '인천',
  대전광역시: '대전',
  울산광역시: '울산',
  세종특별자치시: '세종',
  경기도: '경기',
  충청북도: '충북',
  충청남도: '충남',
  경상북도: '경북',
  경상남도: '경남',
  제주특별자치도: '제주',
  강원특별자치도: '강원',
  전북특별자치도: '전북',
};

export const shortSido = (name) => SIDO_SHORT[name] ?? name;

export const regionLabel = (region) => (region ? `${shortSido(region.first.name)} ${region.second.name}` : '');

// 고수 활동 지역 요약. 백엔드의 nationwide, wholeRegions, regions 세 필드를 그대로 따른다.
export function expertRegionSummary(expert) {
  if (expert.nationwide) return '전국';
  const all = [...expert.wholeRegions.map((r) => `${r} 전체`), ...expert.regions];
  if (all.length === 0) return '지역 미등록';
  return all.length === 1 ? all[0] : `${all[0]} 외 ${all.length - 1}곳`;
}

// 지역 일치 정도. 시군구 직접 등록 3, 시도 전체 2, 전국 1, 해당 없음 0
export function regionSpecificity(expert, label) {
  if (expert.regions.includes(label)) return 3;
  if (expert.wholeRegions.includes(label.split(' ')[0])) return 2;
  return expert.nationwide ? 1 : 0;
}

export const primaryOf = (expert) => getSub(expert.primaryCategoryId);

export function formatWon(price) {
  if (price >= 10000) {
    const man = price / 10000;
    return `${Number.isInteger(man) ? man.toLocaleString('ko-KR') : man.toFixed(1)}만 원`;
  }
  return `${price.toLocaleString('ko-KR')}원`;
}
