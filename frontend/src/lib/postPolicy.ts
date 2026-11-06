// 게시글·댓글 사진 규칙 (와이어프레임 W·16: 최대 10장 · 5MB · JPG·PNG·GIF·WebP)
export const POST_IMAGE_EXTS = ['jpg', 'jpeg', 'png', 'gif', 'webp'];
export const POST_IMAGE_ACCEPT = '.jpg,.jpeg,.png,.gif,.webp,image/jpeg,image/png,image/gif,image/webp';
export const POST_IMAGE_MAX = 10;
export const POST_IMAGE_BYTES = 5 * 1024 * 1024;
export const TITLE_MIN = 2, TITLE_MAX = 30, CONTENT_MIN = 2, CONTENT_MAX = 5000;

export function checkPostImage(f: File): string | null {
  const ext = f.name.split('.').pop()?.toLowerCase() ?? '';
  if (!POST_IMAGE_EXTS.includes(ext)) return '지원하지 않는 형식이에요.\nJPG·PNG·GIF·WebP 사진을 올려 주세요.';
  if (f.size === 0) return '내용이 없는 파일은 올릴 수 없어요.';
  if (f.size > POST_IMAGE_BYTES) return '5MB를 넘는 사진은 올릴 수 없어요.';
  return null;
}
