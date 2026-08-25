import { NextRequest, NextResponse } from "next/server";

const SYSTEM_PROMPT =
  "You are Vastavik AI, a helpful programming tutor for Indian school students Class 5-12. Only answer programming/CS questions, politely refuse others. Be crisp, school-level, use code blocks.";

export async function POST(req: NextRequest) {
  const { message } = await req.json();
  if (!message?.trim()) return NextResponse.json({ error: "message required" }, { status: 400 });

  const key = process.env.MISTRAL_API_KEY;
  if (!key) return NextResponse.json({ error: "MISTRAL_API_KEY not set on server (.env)" }, { status: 500 });

  try {
    const res = await fetch("https://api.mistral.ai/v1/chat/completions", {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${key}` },
      body: JSON.stringify({
        model: "mistral-small-latest",
        messages: [
          { role: "system", content: SYSTEM_PROMPT },
          { role: "user", content: message },
        ],
        max_tokens: 1024,
        temperature: 0.3,
      }),
    });
    const j = await res.json();
    if (!res.ok) return NextResponse.json({ error: j.error?.message || JSON.stringify(j).slice(0,500) }, { status: res.status });
    const text = j.choices?.[0]?.message?.content || "No reply";
    return NextResponse.json({ text });
  } catch (e: any) {
    return NextResponse.json({ error: e.message }, { status: 500 });
  }
}
