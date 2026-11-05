// #173 MVP 도메인 API (현재 원격 dev 백엔드 컨트롤러·DTO 기준)
// 요청서·견적·의뢰 요청·채팅·크레딧·결제·커뮤니티·찜·관리자
import { request } from './client';
import type { FileUploadResponse, Member, MemberRole } from './types';

// ── 공통 ──
export interface Page<T> {
  content: T[];
  pageNumber: number;     // 1부터
  pageSize: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
}

// ── 회원가입 ──
export interface SignupRequest { email: string; password: string; nickname: string; phone: string }
export const signup = (body: SignupRequest) =>
  request<Member>('/api/public/auth/signup', { method: 'POST', body, auth: false });

// ── 요청서 (REQ) ──
export type RequestStatus = 'OPEN' | 'MATCHED' | 'CLOSED';
export interface Question { id: number; content: string; isRequired: boolean; displayOrder: number }
export interface AnswerRequest { questionId: number; content: string }
export interface ServiceRequestCreateRequest { secondCategoryId: number; secondRegionId: number; answers: AnswerRequest[] }
export interface ServiceRequestListItem { requestId: number; secondCategoryName: string; secondRegionName: string; requestStatus: RequestStatus; createdAt: string }
export interface Answer { questionId: number; questionContent: string; content: string }
export interface ServiceRequestDetail extends ServiceRequestListItem { customerNickname: string; answers: Answer[] }
export interface MatchedRequest extends ServiceRequestListItem { sentQuoteId: number | null }

export const getQuestions = () => request<Question[]>('/api/questions');
export const createServiceRequest = (body: ServiceRequestCreateRequest) =>
  request<{ id: number }>('/api/requests', { method: 'POST', body });
export const getMyRequests = () => request<ServiceRequestListItem[]>('/api/members/me/requests');
export const getServiceRequest = (id: number) => request<ServiceRequestDetail>(`/api/requests/${id}`);
export const cancelServiceRequest = (id: number) => request<void>(`/api/requests/${id}/cancel`, { method: 'PATCH' });
export const getMatchedRequests = () => request<MatchedRequest[]>('/api/members/me/expert-profile/requests');

// ── 견적 (QT) ──
export type QuoteStatus = 'SENT' | 'REQUESTED';
export interface RequestQuote { quoteId: number; expertProfileId: number; expertNickname: string; description: string; price: number; quoteStatus: QuoteStatus; sentAt: string }
export interface Quote extends RequestQuote { serviceRequestId: number; secondCategoryName: string; secondRegionName: string }
export const getRequestQuotes = (requestId: number) => request<RequestQuote[]>(`/api/requests/${requestId}/quotes`);
export const getQuote = (quoteId: number) => request<Quote>(`/api/quotes/${quoteId}`);
export const getMyQuotes = () => request<Quote[]>('/api/quotes');
export const createQuote = (requestId: number, body: { description: string; price: number }) =>
  request<{ quoteId: number; description: string; price: number; sentAt: string }>(`/api/requests/${requestId}/quotes`, { method: 'POST', body });
export const deleteQuote = (quoteId: number) => request<void>(`/api/quotes/${quoteId}`, { method: 'DELETE' });

// ── 의뢰 요청 = 채팅방 생성 (서버가 차감·상태 변경·방 생성을 한 트랜잭션으로 처리) ──
export const requestQuote = (quoteId: number) =>
  request<{ roomId: number; quoteId: number; createdAt: string }>('/api/chat-rooms', { method: 'POST', body: { quoteId } });

// ── 채팅 (CHAT) ──
export interface ChatRoomSummary {
  roomId: number; quoteId: number;
  partner: { memberId: number; nickname: string; profileImageUrl: string | null } | null;
  partnerLeft: boolean; lastMessagePreview: string | null; lastMessageAt: string | null; unreadCount: number; createdAt: string;
}
export interface ChatMessage { messageId: number; roomId: number; senderId: number; senderNickname: string; type: 'TEXT' | 'IMAGE'; content: string | null; imageUrl: string | null; createdAt: string }
export const getChatRooms = (pageNumber = 1) => request<Page<ChatRoomSummary>>(`/api/chat-rooms?pageNumber=${pageNumber}`);
export const getChatMessages = (roomId: number, cursor?: number | null) =>
  request<{ messages: ChatMessage[]; nextCursor: number | null; hasNext: boolean }>(`/api/chat-rooms/${roomId}/messages${cursor ? `?cursor=${cursor}` : ''}`);
export const readChatRoom = (roomId: number) => request<void>(`/api/chat-rooms/${roomId}/read`, { method: 'PATCH' });
export const leaveChatRoom = (roomId: number) => request<void>(`/api/chat-rooms/${roomId}/leave`, { method: 'PATCH' });

// ── 크레딧·결제 (CR) · 고수 전용 ──
export interface CreditBalance { balance: number; reservedCredit: number; availableCredit: number; creditPerQuote: number; availableQuoteCount: number }
export interface CreditProduct { id: number; name: string; price: number; creditAmount: number }
export interface CreditReservation { quoteId: number; amount: number; reservedAt: string }
export type CreditHistoryType = 'CHARGE' | 'USE' | 'REFUND';
export interface CreditHistory { id: number; historyType: CreditHistoryType; amount: number; balanceAfter: number; paymentId: number | null; quoteId: number | null; createAt: string }
export const getCredit = () => request<CreditBalance>('/api/members/me/credit');
export const getCreditProducts = () => request<CreditProduct[]>('/api/credit-products');
export const getCreditReservations = () => request<CreditReservation[]>('/api/members/me/credit-reservations');
export const getCreditHistories = (pageNumber: number, type?: CreditHistoryType) =>
  request<Page<CreditHistory>>(`/api/members/me/credit-histories?pageNumber=${pageNumber}&pageSize=10${type ? `&type=${type}` : ''}`);
export const createPayment = (creditProductId: number) =>
  request<{ paymentId: number; orderId: string; orderName: string; amount: number }>('/api/payments', { method: 'POST', body: { creditProductId } });
export const confirmPayment = (body: { paymentKey: string; orderId: string; amount: number }) =>
  request<{ orderId: string; paymentStatus: 'DONE' | 'PENDING'; chargedCredit: number; balance: number }>('/api/payments/confirm', { method: 'POST', body });
export const failPayment = (orderId: string, body: { code: string; message?: string }) =>
  request<{ orderId: string; paymentStatus: 'CANCELED' | 'FAILED' }>(`/api/payments/${encodeURIComponent(orderId)}/fail`, { method: 'PATCH', body });

// ── 커뮤니티 (CM) ──
export type BoardType = 'QUESTION' | 'TIP' | 'RECRUIT' | 'PROMOTION';
export interface PostCategory { firstCategoryId: number; firstCategoryName: string; secondCategoryId: number; secondCategoryName: string }
export interface PostRegion { firstRegionId: number; firstRegionName: string; secondRegionId: number; secondRegionName: string }
export interface PostImage { fileId: number; url: string }
export interface PostSummary {
  postId: number; boardType: BoardType; memberNickname: string; title: string; thumbnailUrl: string | null;
  category: PostCategory | null; region: PostRegion | null; commentCount: number; likeCount: number; viewCount: number;
  createdAt: string; updatedAt: string; isVisible: boolean;
}
export interface Post extends Omit<PostSummary, 'thumbnailUrl'> {
  memberId: number; memberProfileImageUrl: string | null; content: string; images: PostImage[]; liked: boolean;
}
export interface PostBody { boardType?: BoardType; title: string; content: string; secondCategoryId?: number | null; secondRegionId?: number | null; imageIds: number[] }
export interface Comment {
  commentId: number; memberId: number; memberNickname: string; memberProfileImageUrl: string | null; isExpert: boolean;
  content: string; images: PostImage[]; likeCount: number; liked: boolean; createdAt: string; updatedAt: string;
}
// 분야·지역 필터는 선택 (firstCategoryId·secondCategoryId·firstRegionId·secondRegionId)
export interface PostFilter { firstCategoryId?: number; secondCategoryId?: number; firstRegionId?: number; secondRegionId?: number }
const filterQuery = (f?: PostFilter) => f ? Object.entries(f).filter(([, v]) => v != null).map(([k, v]) => `&${k}=${v}`).join('') : '';
export const getPosts = (boardType: BoardType, pageNumber: number, keyword?: string, filter?: PostFilter) =>
  request<Page<PostSummary>>(`/api/public/posts?boardType=${boardType}&pageNumber=${pageNumber}${keyword ? `&keyword=${encodeURIComponent(keyword)}` : ''}${filterQuery(filter)}`);
export const getMyPosts = (pageNumber: number, boardType?: BoardType) =>
  request<Page<PostSummary>>(`/api/members/me/posts?pageNumber=${pageNumber}${boardType ? `&boardType=${boardType}` : ''}`);
export const getPost = (postId: number) => request<Post>(`/api/public/posts/${postId}`);
export const createPost = (body: PostBody) => request<Post>('/api/posts', { method: 'POST', body });
export const updatePost = (postId: number, body: PostBody) => request<Post>(`/api/posts/${postId}`, { method: 'PUT', body });
export const deletePost = (postId: number) => request<void>(`/api/posts/${postId}`, { method: 'DELETE' });
export const getComments = (postId: number, pageNumber: number) => request<Page<Comment>>(`/api/public/posts/${postId}/comments?pageNumber=${pageNumber}`);
export const createComment = (postId: number, body: { content: string; imageIds: number[] }) =>
  request<Comment>(`/api/posts/${postId}/comments`, { method: 'POST', body });
export const updateComment = (commentId: number, body: { content: string; imageIds: number[] }) =>
  request<Comment>(`/api/comments/${commentId}`, { method: 'PUT', body });
export const deleteComment = (commentId: number) => request<void>(`/api/comments/${commentId}`, { method: 'DELETE' });
export const likePost = (postId: number, on: boolean) =>
  request<{ postId: number; liked: boolean; likeCount: number }>(`/api/posts/${postId}/likes`, { method: on ? 'POST' : 'DELETE' });
export const likeComment = (commentId: number, on: boolean) =>
  request<{ commentId: number; liked: boolean; likeCount: number }>(`/api/comments/${commentId}/likes`, { method: on ? 'POST' : 'DELETE' });

// 게시글·댓글 이미지: 공개 파일로 올린 뒤 fileId를 본문에 담는다
export async function uploadPublicFiles(files: File[]): Promise<FileUploadResponse[]> {
  const form = new FormData();
  files.forEach((f) => form.append('files', f));
  form.append('visibility', 'PUBLIC');
  return request<FileUploadResponse[]>('/api/files', { method: 'POST', form });
}

// 채팅 사진: 비공개(PRIVATE) 파일로 올린 뒤 받은 fileId로 IMAGE 메시지를 보낸다 (서버는 본인이 올린 PRIVATE 파일만 받음)
export async function uploadPrivateImage(file: File): Promise<FileUploadResponse> {
  const form = new FormData();
  form.append('files', file);
  form.append('visibility', 'PRIVATE');
  const list = await request<FileUploadResponse[]>('/api/files', { method: 'POST', form });
  if (!list?.[0]?.fileId || list[0].visibility !== 'PRIVATE') throw new Error('사진 업로드 결과를 확인하지 못했어요.');
  return list[0];
}

// ── 고수 찜 (BM) ──
export interface BookmarkSummary {
  expertProfileId: number; expertNickname: string; expertProfileImageUrl: string | null;
  category: PostCategory | null; nationwide: boolean;
  wholeRegions: { firstRegionId: number; firstRegionName: string }[]; regions: PostRegion[]; bookmarkedAt: string;
  // averageRating·reviewCount도 오지만 MVP 제외 항목이라 쓰지 않는다
}
export const getBookmarks = (pageNumber: number) => request<Page<BookmarkSummary>>(`/api/members/me/bookmarks?pageNumber=${pageNumber}`);
export const addBookmark = (expertProfileId: number) =>
  request<{ expertProfileId: number; bookmarked: boolean }>(`/api/members/me/bookmarks/${expertProfileId}`, { method: 'POST' });
export const removeBookmark = (expertProfileId: number) =>
  request<{ expertProfileId: number; bookmarked: boolean }>(`/api/members/me/bookmarks/${expertProfileId}`, { method: 'DELETE' });

// ── 회원 탈퇴 (MemberController DELETE /api/members/me · 204 · 리프레시 쿠키도 만료) ──
export const deleteMe = () => request<void>('/api/members/me', { method: 'DELETE' });

// ── 관리자 ──
export const adminGetMembers = (pageNumber: number, sortBy = 'id', sortOrder = 'asc') =>
  request<Page<Member>>(`/api/admin/members?pageNumber=${pageNumber}&pageSize=10&sortBy=${sortBy}&sortOrder=${sortOrder}`);
export const adminSetRole = (memberId: number, role: MemberRole) =>
  request<Member>(`/api/admin/members/${memberId}/role`, { method: 'PATCH', body: { role } });
export const adminDeleteMember = (memberId: number) => request<void>(`/api/admin/members/${memberId}`, { method: 'DELETE' });

export const adminCreateFirstCategory = (name: string) => request<{ id: number; name: string }>('/api/admin/first-categories', { method: 'POST', body: { name } });
export const adminUpdateFirstCategory = (id: number, name: string) => request<{ id: number; name: string }>(`/api/admin/first-categories/${id}`, { method: 'PUT', body: { name } });
export const adminDeleteFirstCategory = (id: number) => request<unknown>(`/api/admin/first-categories/${id}`, { method: 'DELETE' });
export const adminCreateSecondCategory = (firstCategoryId: number, name: string) =>
  request<unknown>(`/api/admin/first-categories/${firstCategoryId}/second-categories`, { method: 'POST', body: { name } });
export const adminUpdateSecondCategory = (id: number, name: string, firstCategoryId: number) =>
  request<unknown>(`/api/admin/second-categories/${id}`, { method: 'PUT', body: { name, firstCategoryId } });
export const adminDeleteSecondCategory = (id: number) => request<unknown>(`/api/admin/second-categories/${id}`, { method: 'DELETE' });

export const adminCreateFirstRegion = (name: string, code: string) => request<unknown>('/api/admin/first-regions', { method: 'POST', body: { name, code } });
export const adminUpdateFirstRegion = (id: number, name: string, code: string) => request<unknown>(`/api/admin/first-regions/${id}`, { method: 'PUT', body: { name, code } });
export const adminDeleteFirstRegion = (id: number) => request<unknown>(`/api/admin/first-regions/${id}`, { method: 'DELETE' });
export const adminCreateSecondRegion = (firstRegionId: number, name: string, code: string) =>
  request<unknown>(`/api/admin/first-regions/${firstRegionId}/second-regions`, { method: 'POST', body: { name, code } });
export const adminUpdateSecondRegion = (id: number, name: string, code: string, firstRegionId: number) =>
  request<unknown>(`/api/admin/second-regions/${id}`, { method: 'PUT', body: { name, code, firstRegionId } });
export const adminDeleteSecondRegion = (id: number) => request<unknown>(`/api/admin/second-regions/${id}`, { method: 'DELETE' });

// ── 관리자 · 게시글 공개 관리 (PostController) ──
export type PostSort = 'createdAt' | 'commentCount' | 'likeCount' | 'viewCount';
export interface AdminPostQuery extends PostFilter { pageNumber: number; boardType?: BoardType; keyword?: string; isVisible?: boolean; sortBy?: PostSort; sortOrder?: 'asc' | 'desc' }
export const adminGetPosts = (q: AdminPostQuery) => {
  const qs = new URLSearchParams();
  Object.entries(q).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') qs.set(k, String(v)); });
  qs.set('pageSize', '20');
  return request<Page<PostSummary>>(`/api/admin/posts?${qs}`);
};
export const adminSetPostVisibility = (postId: number, isVisible: boolean, reason?: string) =>
  request<{ postId: number; isVisible: boolean }>(`/api/admin/posts/${postId}/visibility`, { method: 'PATCH', body: { isVisible, ...(reason ? { reason } : {}) } });
export interface VisibilityHistory { historyId: number; isVisible: boolean; reason: string | null; adminId: number; adminNickname: string; createdAt: string }
export const adminGetPostVisibilityHistory = (postId: number) =>
  request<{ postId: number; histories: VisibilityHistory[] }>(`/api/admin/posts/${postId}/visibility-history`);

// ── 관리자 · 요청서·견적 현황 (ServiceRequestController · QuoteController) ──
export interface AdminRequest { requestId: number; customerId: number; customerNickname: string; secondCategoryName: string; secondRegionName: string; requestStatus: RequestStatus; createdAt: string }
export interface AdminQuote { quoteId: number; serviceRequestId: number; expertProfileId: number; expertNickname: string; customerId: number; customerNickname: string; secondCategoryName: string; secondRegionName: string; price: number; quoteStatus: QuoteStatus; sentAt: string }
const adminList = (path: string, pageNumber: number, sortBy: string, sortOrder: string, status?: string) =>
  `${path}?pageNumber=${pageNumber}&pageSize=10&sortBy=${sortBy}&sortOrder=${sortOrder}${status ? `&status=${status}` : ''}`;
export const adminGetRequests = (pageNumber: number, sortBy = 'createdAt', sortOrder = 'desc', status?: RequestStatus) =>
  request<Page<AdminRequest>>(adminList('/api/admin/requests', pageNumber, sortBy, sortOrder, status));
export const adminGetQuotes = (pageNumber: number, sortBy = 'sentAt', sortOrder = 'desc', status?: QuoteStatus) =>
  request<Page<AdminQuote>>(adminList('/api/admin/quotes', pageNumber, sortBy, sortOrder, status));
