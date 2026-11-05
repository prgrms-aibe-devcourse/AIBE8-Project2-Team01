// 이슈 #92에서 쓰는 API (실제 Controller 경로 기준)
import { request, tokenStore } from './client';
import type {
  ExpertProfile, ExpertProfileCreateRequest, ExpertProfileUpdateRequest, FileUploadResponse,
  FirstCategory, FirstRegion, LoginResponse, Member, MemberRole, MemberUpdateRequest,
  PublicExpertSummary, SecondCategory, SecondRegion
} from './types';

// ── 인증 (AuthController) ──
export async function login(email: string, password: string): Promise<void> {
  const data = await request<LoginResponse>('/api/public/auth/login', {
    method: 'POST', body: { email, password }, auth: false
  });
  tokenStore.set(data.accessToken);
}
export async function logout(): Promise<void> {
  try {
    await request<void>('/api/auth/logout', { method: 'POST', retry: false });
  } finally {
    tokenStore.clear();
  }
}

// ── 회원 (MemberController) ──
export const getMe = () => request<Member>('/api/members/me');
export const updateMe = (body: MemberUpdateRequest) =>
  request<Member>('/api/members/me', { method: 'PUT', body });
export const setMyImage = (imageId: number) =>
  request<Member>('/api/members/me/image', { method: 'PUT', body: { imageId } });
// 회원과 사진의 연결만 해제한다 (공통 파일 자체를 지우지 않음) · 204
export const unlinkMyImage = () => request<void>('/api/members/me/image', { method: 'DELETE' });
export const changeMyRole = (role: Exclude<MemberRole, 'ADMIN'>) =>
  request<Member>('/api/members/me/role', { method: 'PATCH', body: { role } });

// ── 공통 파일 (UploadFileController) ──
export async function uploadPublicImage(file: File): Promise<FileUploadResponse> {
  const form = new FormData();
  form.append('files', file);
  form.append('visibility', 'PUBLIC');
  const list = await request<FileUploadResponse[]>('/api/files', { method: 'POST', form });
  if (!list || list.length === 0) throw new Error('empty upload response');
  return list[0];
}

// ── 고수 프로필 (ExpertProfileController) ──
export const getMyExpertProfile = () => request<ExpertProfile>('/api/members/me/expert-profile');
export const createExpertProfile = (body: ExpertProfileCreateRequest) =>
  request<ExpertProfile>('/api/members/me/expert-profile', { method: 'POST', body });
export const updateExpertProfile = (body: ExpertProfileUpdateRequest) =>
  request<ExpertProfile>('/api/members/me/expert-profile', { method: 'PATCH', body });
export const getPublicExperts = () =>
  request<PublicExpertSummary[]>('/api/public/expert-profiles', { auth: false });
export const getPublicExpertProfile = (expertProfileId: number) =>
  request<ExpertProfile>(`/api/public/expert-profiles/${expertProfileId}`, { auth: false });

// ── 기준 데이터 (공개) ──
export const getFirstCategories = () =>
  request<FirstCategory[]>('/api/public/first-categories', { auth: false });
export const getSecondCategories = (firstCategoryId: number) =>
  request<SecondCategory[]>(`/api/public/first-categories/${firstCategoryId}/second-categories`, { auth: false });
export const getFirstRegions = () =>
  request<FirstRegion[]>('/api/public/first-regions', { auth: false });
export const getSecondRegions = (firstRegionId: number) =>
  request<SecondRegion[]>(`/api/public/first-regions/${firstRegionId}/second-regions`, { auth: false });
