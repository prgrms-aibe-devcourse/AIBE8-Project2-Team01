// 표시용 문자열
import type { ExpertProfile, FirstRegion, MemberRole } from '../api/types';

export const roleLabel = (r: MemberRole) => ({ USER: '고객', EXPERT: '고수', ADMIN: '관리자' }[r]);

export function formatPhone(p: string | null | undefined): string {
  if (!p) return '';
  return /^\d{11}$/.test(p) ? `${p.slice(0, 3)}-${p.slice(3, 7)}-${p.slice(7)}` : p;
}

// 활동 지역 표시: 전국 / ○○ 전체 / ○○ ○○구
export function regionLabels(p: Pick<ExpertProfile, 'nationwide' | 'wholeRegions' | 'regions'>, firstRegions: FirstRegion[]): string[] {
  if (p.nationwide) return ['전국'];
  const nameOf = (id: number) => firstRegions.find((f) => f.id === id)?.name ?? '';
  return [
    ...p.wholeRegions.map((w) => `${w.name} 전체`),
    ...p.regions.map((r) => `${nameOf(r.firstRegionId)} ${r.name}`.trim())
  ];
}

export const careerLabel = (years: number | null) => (years === null ? '' : `경력 ${years}년`);
