// 백엔드 data.sql의 question 시드와 같은 내용이다.
// 연결 후에는 GET /api/questions 응답으로 대체한다.
export const REQUEST_QUESTIONS = [
  { id: 1, content: '어떤 서비스가 필요하신가요?', isRequired: true, kind: 'long', placeholder: '예: 투룸에서 투룸으로 이사해요. 냉장고와 세탁기가 있어요.' },
  { id: 2, content: '언제 서비스를 받고 싶으신가요?', isRequired: true, kind: 'when' },
  { id: 3, content: '생각하고 계신 예산이 있나요?', isRequired: false, kind: 'short', placeholder: '예: 50만 원 안쪽' },
  { id: 4, content: '고수에게 전달하고 싶은 추가 요청사항이 있나요?', isRequired: false, kind: 'long', placeholder: '예: 엘리베이터가 없는 3층이에요.' },
];

export const WHEN_OPTIONS = ['가능한 빨리', '이번 주 안에', '다음 주 중', '날짜를 정했어요', '협의 가능'];
