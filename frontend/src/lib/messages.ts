// 서버 오류를 화면 문구로 바꾼다
// 문구 목록은 api/serverMessages.ts에 있고, 목록에 없는 서버 문구는 상태 코드별 중립 안내로 바뀐다 (api/client.ts errorText)
import { errorText } from '../api/client';

export function friendly(e: unknown, fallback?: string): string {
  return errorText(e, fallback);
}
