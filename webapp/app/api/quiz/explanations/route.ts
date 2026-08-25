import { NextRequest, NextResponse } from "next/server";

const SYSTEM_PROMPT = `You are Vastavik AI, a programming tutor for Indian school students (Class 5-12). 
For each question, explain WHY the correct answer is correct in simple, school-level language.
Be crisp and educational. Return ONLY valid JSON.`;

export async function POST(req: NextRequest) {
  const { questions } = await req.json();
  if (!Array.isArray(questions) || !questions.length) {
    return NextResponse.json({ error: "questions array required" }, { status: 400 });
  }

  const key = process.env.MISTRAL_API_KEY;
  if (!key) return NextResponse.json({ error: "MISTRAL_API_KEY not set on server" }, { status: 500 });

  const prompt = `For each question below, provide a brief explanation (2-3 sentences) of WHY the correct option is correct. Use simple language for Indian school students (Class 5-12).

Questions:
${questions.map((q: any, i: number) => 
  `${i+1}. Q: ${q.q}\nOptions: ${q.o.map((o: string, j: number) => `${String.fromCharCode(65+j)}) ${o}`).join(" | ")}\nCorrect: ${String.fromCharCode(65+q.a)}) ${q.o[q.a]}`
).join("\n\n")}

Return ONLY a JSON array of strings (explanations in the same order):
["explanation 1", "explanation 2", ...]`;

  try {
    const res = await fetch("https://api.mistral.ai/v1/chat/completions", {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${key}` },
      body: JSON.stringify({
        model: "mistral-small-latest",
        messages: [
          { role: "system", content: SYSTEM_PROMPT },
          { role: "user", content: prompt },
        ],
        max_tokens: 4096,
        temperature: 0.2,
      }),
    });
    const j = await res.json();
    if (!res.ok) return NextResponse.json({ error: j.error?.message || "Mistral API error" }, { status: res.status });

    const txt = j.choices?.[0]?.message?.content || "[]";
    const cleaned = txt.replace(/```json\s*/g, "").replace(/```\s*/g, "").trim();
    let explanations: string[];
    try { explanations = JSON.parse(cleaned); } catch { return NextResponse.json({ error: "Failed to parse explanations", raw: cleaned.slice(0, 500) }, { status: 500 }); }
    if (!Array.isArray(explanations)) return NextResponse.json({ error: "Response is not an array" }, { status: 500 });

    return NextResponse.json({ explanations });
  } catch (e: any) {
    return NextResponse.json({ error: e.message }, { status: 500 });
  }
}