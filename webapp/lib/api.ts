// Vastavik API client — mirrors kotlin-app AuthInterceptor HMAC logic
// Backend: middleware/requireApiKey.ts → hmac = HMAC(secret, "ts.keyId.nonce") base64url
export const API_BASE = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:3001";
const API_KEY_ID = process.env.NEXT_PUBLIC_API_KEY_ID || "android-prod";
const API_KEY_SECRET = process.env.NEXT_PUBLIC_API_KEY_SECRET || "dev-secret-android-32bytes-hex-0000";

async function hmacBase64Url(secret: string, payload: string): Promise<string> {
  const enc = new TextEncoder();
  const key = await crypto.subtle.importKey("raw", enc.encode(secret), { name: "HMAC", hash: "SHA-256" }, false, ["sign"]);
  const sig = await crypto.subtle.sign("HMAC", key, enc.encode(payload));
  const bytes = new Uint8Array(sig);
  // base64url without padding
  let b64 = btoa(String.fromCharCode(...bytes));
  return b64.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/,"");
}

export async function authHeaders(idToken?: string | null): Promise<Record<string,string>> {
  const nonce = (typeof crypto !== "undefined" && "randomUUID" in crypto) ? crypto.randomUUID() : Math.random().toString(36).slice(2);
  const ts = Math.floor(Date.now()/1000).toString();
  const payload = `${ts}.${API_KEY_ID}.${nonce}`;
  const hmac = await hmacBase64Url(API_KEY_SECRET, payload);
  const h: Record<string,string> = {
    "x-request-id": nonce,
    "x-api-timestamp": ts,
    "x-api-key": `${API_KEY_ID}.${hmac}`,
    "Accept": "application/json",
  };
  if (idToken) h["Authorization"] = `Bearer ${idToken}`;
  return h;
}

export async function apiGet<T>(path: string, idToken?: string | null): Promise<T> {
  const headers = await authHeaders(idToken);
  let res: Response;
  try {
    res = await fetch(`${API_BASE}${path}`, { headers, cache: "no-store" });
  } catch (e: any) {
    throw new Error(`Backend unreachable (${API_BASE}${path}): ${e.message} — is backend running on :3001?`);
  }
  const text = await res.text();
  if (!res.ok) throw new Error(`${res.status} ${text.slice(0,300)}`);
  if (!text) throw new Error("Empty response from backend");
  try {
    return JSON.parse(text) as T;
  } catch (e: any) {
    throw new Error(`Invalid JSON from backend (${text.slice(0,200)}): ${e.message}`);
  }
}

// DTO helpers — keep lenient like kotlin mappers (mirrors CourseModel.kt / FirestoreRepository.kt)
export type CourseDto = { id:string; title:string; iconName?:string; color?:number; description?:string; order:number; catalogEnabled?:boolean; thumbnailUrl?:string; isPublished?:boolean; language?:string; createdBy?:string };
export type PartDto = { id:string; title:string; description?:string; order:number };
export type SubpartDto = { id:string; title:string; order:number };
export type LessonDto = { id:string; title:string; description:string; youtubeUrl:string; youtubeVideoId?:string; duration?:string; durationSec?:number; order:number; isPremium?:boolean; isPublished?:boolean; videoFormat?:string; whiteboardImageUrl?:string; codeSample?:string; notes?:string; path?:string };
export type BannerDto = { id:string; title:string; subtitle?:string; imageUrl:string; actionLink?:string; order:number; isActive?:boolean };
export type QuizDto = { id:string; title:string; topic:string; difficulty?:string; questions?: {question:string;options:string[];answerIndex:number;explanation?:string}[] };
export type CodingChallengeDto = { id:string; title:string; topic:string; difficulty:"Easy"|"Medium"|"Hard"; description?:string };
export type PyqDto = { id:string; title:string; year:string; board:string; subject:string };
export type TransactionDto = { id:string; planName?:string; amount:number; status:string; timestamp?:string };

// Convenience wrappers — same endpoints as kotlin VastavikApiService + fallback to mock
export const api = {
  getCourses: (token?:string|null)=> apiGet<{data:CourseDto[]}>("/api/v1/courses?limit=20", token),
  getCourse: (id:string, token?:string|null)=> apiGet<{data:CourseDto}>(`/api/v1/courses/${id}`, token),
  getParts: (courseId:string, token?:string|null)=> apiGet<{data:PartDto[]}>(`/api/v1/courses/${courseId}/parts`, token),
  getSubparts: (courseId:string, partId:string, token?:string|null)=> apiGet<{data:SubpartDto[]}>(`/api/v1/courses/${courseId}/parts/${partId}/subparts`, token),
  getLessons: (courseId:string, partId:string, subpartId:string, token?:string|null)=> apiGet<{data:LessonDto[]}>(`/api/v1/courses/${courseId}/parts/${partId}/subparts/${subpartId}/lessons`, token),
  getLesson: (lessonId:string, token?:string|null)=> apiGet<{data:LessonDto}>(`/api/v1/lessons/${lessonId}`, token),
  getBanners: (token?:string|null)=> apiGet<{data:BannerDto[]}>("/api/v1/banners", token),
  // Coming soon — backend will add these; webapp already wired to same paths as spec (.agent/API_ROUTES.md):
  getQuizzes: (token?:string|null)=> apiGet<{data:QuizDto[]}>("/api/v1/quizzes", token).catch(()=>({data:[] as QuizDto[]})),
  getChallenges: (token?:string|null)=> apiGet<{data:CodingChallengeDto[]}>("/api/v1/coding-challenges", token).catch(()=>({data:[] as CodingChallengeDto[]})),
};
