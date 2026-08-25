// Same regex as backend/backend/src/utils/youtube.ts and kotlin HmacUtil
const YT_REGEX = /(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([A-Za-z0-9_-]{11})/;
export function extractVideoId(url: string): string | null {
  const m = url.match(YT_REGEX);
  return m ? m[1] : null;
}
export function isYouTubeUrl(url: string): boolean { return extractVideoId(url) !== null; }
export function buildEmbedUrl(videoId: string, modestBranding = true): string {
  const p = new URLSearchParams({
    modestbranding: modestBranding ? "1" : "0",
    rel: "0",
    iv_load_policy: "3",
    playsinline: "1",
    controls: "1",
    fs: "0",
    disablekb: "1",
    enablejsapi: "1",
    origin: typeof window !== "undefined" ? window.location.origin : "http://localhost:3002",
  });
  return `https://www.youtube-nocookie.com/embed/${videoId}?${p.toString()}`;
}
