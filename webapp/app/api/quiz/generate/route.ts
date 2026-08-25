import { NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
  const { topic, count, difficulty } = await req.json();
  if (!topic?.trim()) return NextResponse.json({ error: "topic required" }, { status: 400 });

  const key = process.env.MISTRAL_API_KEY;
  if (!key) return NextResponse.json({ error: "MISTRAL_API_KEY not set on server (.env)" }, { status: 500 });

  const numQ = Math.min(Math.max(Number(count) || 20, 1), 50);
  const diff = ["Easy", "Medium", "Hard"].includes(difficulty) ? difficulty : "Medium";

  const prompt = `Generate exactly ${numQ} multiple-choice questions for the topic "${topic}" at ${diff} difficulty.

IMPORTANT: You MUST return EXACTLY ${numQ} questions. Not fewer, not more.

Return ONLY a valid JSON array (no markdown, no explanation). Each element:
{"q":"question text","o":["option1","option2","option3","option4"],"a":0}

"a" is the zero-based index of the correct option.
Make questions school-level appropriate for Indian students (Class 5-12).`;

  try {
    const res = await fetch("https://api.mistral.ai/v1/chat/completions", {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${key}` },
      body: JSON.stringify({
        model: "mistral-small-latest",
        messages: [{ role: "user", content: prompt }],
        max_tokens: 8192,
        temperature: 0.15,
      }),
    });
    const j = await res.json();
    if (!res.ok) return NextResponse.json({ error: j.error?.message || "Mistral API error" }, { status: res.status });

    const txt = j.choices?.[0]?.message?.content || "[]";
    const cleaned = txt.replace(/```json\s*/g, "").replace(/```\s*/g, "").trim();

    let parsed: any[];
    try { parsed = JSON.parse(cleaned); } catch { return NextResponse.json({ error: "Failed to parse Mistral response as JSON", raw: cleaned.slice(0, 500) }, { status: 500 }); }
    if (!Array.isArray(parsed)) return NextResponse.json({ error: "Response is not an array", raw: cleaned.slice(0, 500) }, { status: 500 });

    const questions = parsed.map((x: any) => ({ q: String(x.q || ""), o: Array.isArray(x.o) ? x.o.map(String) : [], a: Number(x.a) || 0 }));
    return NextResponse.json({ questions, count: questions.length });
  } catch (e: any) {
    return NextResponse.json({ error: e.message }, { status: 500 });
  }
}
