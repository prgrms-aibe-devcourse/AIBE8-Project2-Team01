// 화면 입력 규칙 (서버 DTO 검증과 확정 정책 기준)

// 프로필 사진: 확정 정책(MEM-04) JPG·JPEG·PNG·WebP · 5MB
// 서버 공통 업로드는 gif·heic·pdf도 받지만 프로필 사진은 화면에서 더 좁게 제한한다.
export const PHOTO_EXTS = ['jpg', 'jpeg', 'png', 'webp'];
export const PHOTO_TYPES = ['image/jpeg', 'image/png', 'image/webp'];
export const PHOTO_MAX_BYTES = 5 * 1024 * 1024;
export const PHOTO_ACCEPT = '.jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp';

// 회원 정보 (배포 기준 비밀번호 8~20자)
export const NICKNAME_MAX = 20;
export const PASSWORD_MIN = 8;
export const PASSWORD_MAX = 20;
export const PHONE_RE = /^[0-9]{11}$/;

// 고수 프로필
export const SHORT_INTRO_MAX = 100;
export const DESCRIPTION_MIN = 10;
export const DESCRIPTION_MAX = 2000;
export const CAREER_YEARS_MAX = 80;
export const CAREER_DESC_MAX = 1000;

export function checkPhoto(file: File): 'type' | 'size' | null {
  const ext = file.name.split('.').pop()?.toLowerCase() ?? '';
  if (!PHOTO_EXTS.includes(ext) || (file.type && !PHOTO_TYPES.includes(file.type))) return 'type';
  if (file.size > PHOTO_MAX_BYTES) return 'size';
  return null;
}
