"use client";
import { extractVideoId, buildEmbedUrl } from "@/lib/youtube";

/**
 * VastavikPlayer — web equivalent of VastavikYouTubePlayer.kt
 * - modestbranding=1, rel=0, iv_load_policy=3, playsinline=1, fs=0, youtube-nocookie
 * - transparent watermark shield bottom-right 72x28 blocks "Watch on YouTube" clicks
 * - unlisted videos play exactly like public if you have the ID
 */
export function VastavikPlayer({ youtubeUrl, youtubeVideoId, startSeconds = 0, title = "Lesson" }: { youtubeUrl?: string; youtubeVideoId?: string; startSeconds?: number; title?: string }) {
  const vid = youtubeVideoId && youtubeVideoId.length === 11 ? youtubeVideoId : (youtubeUrl ? extractVideoId(youtubeUrl) : null);
  if (!vid) return <div className="w-full aspect-video bg-black rounded-b-2xl flex items-center justify-center text-white/70 text-sm">Invalid video</div>;
  let src = buildEmbedUrl(vid);
  if (startSeconds > 0) src += `&start=${Math.floor(startSeconds)}`;
  return (
    <div className="relative w-full aspect-video bg-black rounded-b-2xl overflow-hidden">
      <iframe src={src} title={title} className="w-full h-full" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowFullScreen loading="lazy" referrerPolicy="strict-origin-when-cross-origin" />
      {/* watermark click shield — transparent overlay bottom-right ~72x28 */}
      <div className="absolute bottom-2 right-2 w-[72px] h-[28px] bg-transparent" aria-hidden />
      {/* top scrim shields title/share bar flash on pause */}
      <div className="absolute top-0 left-0 w-full h-10 bg-transparent" aria-hidden />
    </div>
  );
}
