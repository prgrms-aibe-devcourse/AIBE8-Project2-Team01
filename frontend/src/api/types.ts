// 백엔드 DTO와 같은 모양의 타입 (origin/dev 9806806 기준)

export type MemberRole = 'USER' | 'EXPERT' | 'ADMIN';

// MemberResponse
export interface Member {
  id: number;
  email: string;
  nickname: string;
  phone: string;
  role: MemberRole;
  imageId: number | null;
  imageUrl: string | null;
}

// MemberUpdateRequest: 보내지 않은 필드는 기존 값 유지
export interface MemberUpdateRequest {
  password?: string;
  nickname?: string;
  phone?: string;
}

// LoginResponse
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
}

// ErrorResponse: errors는 필드별 메시지 (없으면 응답에서 빠짐)
export interface ErrorBody {
  message?: string;
  errors?: Record<string, string>;
}

// FileUploadResponse
export interface FileUploadResponse {
  fileId: number;
  originalName: string;
  contentType: string;
  size: number;
  visibility: 'PUBLIC' | 'PRIVATE';
  url: string | null;
  uploadedAt: string;
}

// 기준 데이터
export interface FirstCategory { id: number; name: string }
export interface SecondCategory { id: number; name: string; firstCategoryId: number }
export interface FirstRegion { id: number; code: string; name: string }
export interface SecondRegion { id: number; code: string; name: string; firstRegionId: number }

// ExpertProfileResponse (작업 사진은 이번 화면에서 쓰지 않음)
export interface ExpertProfile {
  id: number;
  nickname: string;
  shortIntro: string;
  description: string;
  careerYears: number | null;
  careerDescription: string | null;
  imageId: number | null;
  imageUrl: string | null;
  services: SecondCategory[];
  primaryCategory: SecondCategory | null;
  nationwide: boolean;
  wholeRegions: FirstRegion[];
  regions: SecondRegion[];
  // 작업 사진 (WorkImageResponse). 공개 파일만 내려온다
  workImages?: WorkImage[];
}

export interface WorkImage { fileId: number; url: string; description: string | null }
// WorkImageRequest: 업로드한 공개 파일 번호와 설명(200자 이내)
export interface WorkImageRequest { fileId: number; description?: string }

// ExpertProfileCreateRequest (imageId는 보내지 않음 · 프로필 사진은 회원 사진을 쓴다)
export interface ExpertProfileCreateRequest {
  shortIntro: string;
  description: string;
  careerYears?: number;
  careerDescription?: string;
  serviceIds: number[];
  primaryCategoryId: number;
  nationwide: boolean;
  wholeRegionIds: number[];
  regionIds: number[];
  workImages?: WorkImageRequest[];
}

// ExpertProfileUpdateRequest
// 경력: 생략하면 유지 · null이면 삭제 / 목록: null이면 유지 · 빈 목록이면 연결 삭제
// workImages: 생략하면 유지 · 빈 목록이면 모두 지움 · 목록이면 그 순서로 바꿈
export interface ExpertProfileUpdateRequest {
  shortIntro?: string;
  description?: string;
  careerYears?: number | null;
  careerDescription?: string | null;
  serviceIds?: number[];
  primaryCategoryId?: number;
  nationwide?: boolean;
  wholeRegionIds?: number[];
  regionIds?: number[];
  workImages?: WorkImageRequest[];
}
