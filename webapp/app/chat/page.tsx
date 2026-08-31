"use client";
import { useState, useRef, useEffect, useMemo } from "react";
import { usePersistedState } from "@/lib/usePersistedState";

const SYSTEM_PROMPT="You are Vastavik AI, a helpful programming tutor for Indian school students Class 5-12. Only answer programming/CS questions, politely refuse others. Be crisp, school-level, use code blocks.";
const DEFAULT_MSGS: {role:"user"|"ai"; text:string}[] = [{role:"ai",text:"Hello! I am Vastavik AI. Ask me anything about Java/Python/JS/SQL."}];

/* ── Syntax highlighting ── */
const JAVA_KW = new Set(["abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","do","double","else","enum","extends","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","try","void","volatile","while","true","false","null","var","record","sealed","permits","yield"]);
const PY_KW = new Set(["and","as","assert","async","await","break","class","continue","def","del","elif","else","except","finally","for","from","global","if","import","in","is","lambda","nonlocal","not","or","pass","raise","return","try","while","with","yield","True","False","None","print","range","len","int","float","str","list","dict","set","tuple","input","open","type"]);
const JS_KW = new Set(["abstract","arguments","async","await","boolean","break","byte","case","catch","char","class","const","continue","debugger","default","delete","do","double","else","enum","export","extends","final","finally","float","for","function","goto","if","implements","import","in","instanceof","int","interface","let","long","native","new","of","package","private","protected","public","return","short","static","super","switch","synchronized","this","throw","throws","transient","try","typeof","undefined","var","void","volatile","while","with","yield","true","false","null","console","document","Math","JSON","Promise","Array","Object","String","Number","Boolean"]);
const SQL_KW = new Set(["SELECT","FROM","WHERE","INSERT","UPDATE","DELETE","CREATE","DROP","ALTER","TABLE","INDEX","VIEW","INTO","VALUES","SET","AND","OR","NOT","IN","LIKE","BETWEEN","JOIN","LEFT","RIGHT","INNER","OUTER","ON","AS","ORDER","BY","GROUP","HAVING","LIMIT","OFFSET","DISTINCT","COUNT","SUM","AVG","MIN","MAX","UNION","ALL","ANY","EXISTS","IS","NULL","PRIMARY","KEY","FOREIGN","REFERENCES","CONSTRAINT","CHECK","DEFAULT","AUTO_INCREMENT","VARCHAR","INT","INTEGER","TEXT","DATE","BOOLEAN"]);

function getKeywords(lang: string) {
  const l = lang.toLowerCase();
  if (l === "python") return PY_KW;
  if (l === "javascript" || l === "js") return JS_KW;
  if (l === "sql") return SQL_KW;
  return JAVA_KW;
}

function highlightCode(code: string, lang: string): string {
  const kw = getKeywords(lang);
  const isSQL = lang.toLowerCase() === "sql";
  const isPython = lang.toLowerCase() === "python";
  let out = "";
  let i = 0;
  while (i < code.length) {
    if (code[i] === "/" && i + 1 < code.length && code[i + 1] === "/") {
      const end = code.indexOf("\n", i); const e = end === -1 ? code.length : end;
      out += '<span class="syn-cmt">' + esc(code.slice(i, e)) + '</span>';
      i = e;
    } else if (code[i] === "/" && i + 1 < code.length && code[i + 1] === "*") {
      const end = code.indexOf("*/", i + 2); const e = end === -1 ? code.length : end + 2;
      out += '<span class="syn-cmt">' + esc(code.slice(i, e)) + '</span>';
      i = e;
    } else if (code[i] === "#" && isPython) {
      const end = code.indexOf("\n", i); const e = end === -1 ? code.length : end;
      out += '<span class="syn-cmt">' + esc(code.slice(i, e)) + '</span>';
      i = e;
    } else if (code[i] === '"' || code[i] === "'") {
      const q = code[i]; let j = i + 1;
      while (j < code.length && code[j] !== q) { if (code[j] === "\\") j++; j++; }
      j = Math.min(j + 1, code.length);
      out += '<span class="syn-str">' + esc(code.slice(i, j)) + '</span>';
      i = j;
    } else if (/\d/.test(code[i]) && (i === 0 || !/[a-zA-Z_]/.test(code[i - 1]))) {
      let j = i;
      while (j < code.length && (/\d/.test(code[j]) || code[j] === ".")) j++;
      out += '<span class="syn-num">' + esc(code.slice(i, j)) + '</span>';
      i = j;
    } else if (/[a-zA-Z_]/.test(code[i])) {
      let j = i;
      while (j < code.length && /[a-zA-Z0-9_]/.test(code[j])) j++;
      const w = code.slice(i, j);
      const lookup = isSQL ? w.toUpperCase() : w.toLowerCase();
      if (kw.has(lookup)) {
        out += '<span class="syn-kw">' + esc(w) + '</span>';
      } else if (/^[A-Z]/.test(w) && j < code.length && code[j] === "(") {
        out += '<span class="syn-fn">' + esc(w) + '</span>';
      } else if (/^[A-Z]/.test(w)) {
        out += '<span class="syn-tp">' + esc(w) + '</span>';
      } else if (j < code.length && code[j] === "(") {
        out += '<span class="syn-fn">' + esc(w) + '</span>';
      } else {
        out += '<span class="syn-nm">' + esc(w) + '</span>';
      }
      i = j;
    } else if ("+-*/%=!<>&|^~?:.".includes(code[i])) {
      out += '<span class="syn-op">' + esc(code[i]) + '</span>';
      i++;
    } else if ("(){}[];,".includes(code[i])) {
      out += '<span class="syn-pn">' + esc(code[i]) + '</span>';
      i++;
    } else { out += esc(code[i]); i++; }
  }
  return out;
}

function esc(s: string) { return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); }

/* ── Parse AI markdown into segments ── */
type Segment = { type: "text"; html: string } | { type: "code"; lang: string; code: string; highlighted: string };

function parseAIResponse(text: string): Segment[] {
  const segs: Segment[] = [];
  const parts = text.split("```");
  for (let idx = 0; idx < parts.length; idx++) {
    if (idx % 2 === 0) {
      const t = parts[idx].trim();
      if (t) segs.push({ type: "text", html: parseMarkdownInline(t) });
    } else {
      const lines = parts[idx].split("\n");
      const lang = (lines[0] || "").trim();
      const code = lines.slice(1).join("\n");
      if (code.trim()) {
        segs.push({ type: "code", lang, code, highlighted: highlightCode(code, lang) });
      }
    }
  }
  return segs;
}

function parseMarkdownInline(text: string): string {
  return text
    .replace(/^### (.+)$/gm, '<h3 class="font-bold text-base mt-2 mb-1">$1</h3>')
    .replace(/^## (.+)$/gm, '<h2 class="font-bold text-lg mt-2 mb-1">$1</h2>')
    .replace(/^# (.+)$/gm, '<h1 class="font-bold text-xl mt-2 mb-1">$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/`([^`]+)`/g, '<code class="bg-zinc-100 px-1 rounded text-xs font-mono">$1</code>')
    .replace(/\n/g, "<br/>");
}

/* ── AI bubble with parsed markdown + code blocks ── */
function AIBubble({ text }: { text: string }) {
  const segments = useMemo(() => parseAIResponse(text), [text]);
  const codeSegs = segments.filter(s => s.type === "code") as Extract<Segment, { type: "code" }>[];
  const firstCode = codeSegs[0];
  return (
    <div className="space-y-2">
      {segments.map((seg, idx) => {
        if (seg.type === "text") {
          return <div key={idx} className="whitespace-pre-wrap" dangerouslySetInnerHTML={{ __html: seg.html }} />;
        }
        const code = seg as Extract<Segment, { type: "code" }>;
        const label = codeSegs.length === 1
          ? (code.lang || "code")
          : (code.lang ? `${code.lang}` : "code");
        return (
          <div key={idx} className="rounded-xl overflow-hidden my-2">
            <div className="flex items-center gap-2 bg-[#252526] px-2 py-1">
              <span className="text-[9px] text-gray-500 font-mono">{label}</span>
              {code === firstCode && (
                <a
                  href={`/code-editor?initialCode=${encodeURIComponent(codeSegs.map(c => c.code).join("\n\n"))}&language=${encodeURIComponent(code.lang || "java")}`}
                  className="ml-auto text-[9px] text-blue-400 hover:text-blue-300 font-semibold bg-blue-500/10 px-2 py-0.5 rounded"
                >
                  Open in Editor
                </a>
              )}
            </div>
            <pre className="bg-[#1E1E2E] p-3 overflow-x-auto text-[11px] leading-[18px] font-mono text-[#D4D4D4]">
              <code dangerouslySetInnerHTML={{ __html: code.highlighted }} />
            </pre>
          </div>
        );
      })}
    </div>
  );
}

/* ── Main Chat Component ── */
export default function Chat() {
  const [msgs, setMsgs] = usePersistedState<{role:"user"|"ai"; text:string}[]>("vastavik_chat_messages", DEFAULT_MSGS);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const listRef = useRef<HTMLDivElement>(null);
  const suggestions = ["Explain Code", "Generate Quiz", "Find Bug"];

  useEffect(() => { setTimeout(() => listRef.current?.scrollTo(0, 99999), 100); }, []);

  async function askMistral(prompt: string) {
    setLoading(true);
    try {
      const res = await fetch("/api/chat", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ message: prompt }) });
      const j = await res.json();
      if (!res.ok) throw new Error(j.error || "chat failed");
      const txt = j.text || "No reply";
      setMsgs(m => [...m, { role: "ai", text: txt }]);
    } catch (e: any) { setMsgs(m => [...m, { role: "ai", text: "Error: " + e.message }]); } finally { setLoading(false); setTimeout(() => listRef.current?.scrollTo(0, 99999), 50); }
  }

  function send(text: string) {
    const t = text.trim();
    if (!t) return;
    setMsgs(m => [...m, { role: "user", text: t }]);
    setInput("");
    askMistral(t);
  }

  return (
    <div className="flex flex-col h-[85vh]">
      <div className="flex justify-between items-center pb-2">
        <h1 className="font-bold flex items-center gap-2 text-lg">🤖 Vastavik AI <span className="text-xs bg-zinc-100 px-2 py-1 rounded-full">Mistral Small</span></h1>
        <button onClick={() => setMsgs(DEFAULT_MSGS)} className="text-xs text-brand font-semibold">+ New</button>
      </div>

      <div className="mt-2 flex gap-2 overflow-x-auto pb-2">
        {suggestions.map(s => <button key={s} onClick={() => send(s)} className="shrink-0 rounded-full border bg-white px-3 py-2 text-sm">{s}</button>)}
      </div>

      <div ref={listRef} className="flex-1 overflow-y-auto space-y-3 mt-3 pr-1">
        {msgs.map((m, i) => (
          <div key={i} className={`flex gap-2 ${m.role === "user" ? "justify-end" : ""}`}>
            {m.role === "ai" && <div className="w-8 h-8 rounded-full bg-brand text-white grid place-items-center text-xs shrink-0">AI</div>}
            <div className={`max-w-[80%] rounded-2xl px-3 py-2 text-sm ${m.role === "user" ? "bg-brand text-white rounded-br-sm" : "bg-white border rounded-bl-sm"}`}>
              {m.role === "ai" ? <AIBubble text={m.text} /> : <span className="whitespace-pre-wrap">{m.text}</span>}
            </div>
            {m.role === "user" && <div className="w-8 h-8 rounded-full bg-zinc-900 text-white grid place-items-center text-xs shrink-0">You</div>}
          </div>
        ))}
        {loading && <div className="text-xs text-zinc-500">Thinking…</div>}
      </div>

      <div className="mt-4 pt-3 border-t">
        <div className="flex gap-2 items-end">
          <textarea
            value={input}
            onChange={e => setInput(e.target.value)}
            placeholder="Ask anything (Java/Python/JS/SQL)…"
            className="flex-1 rounded-2xl border px-4 py-3 bg-white min-h-[48px] max-h-[120px] text-sm"
            rows={1}
            onKeyDown={e => { if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); send(input); } }}
          />
          <button onClick={() => send(input)} disabled={!input.trim() || loading} className="w-12 h-12 rounded-full bg-brand text-white grid place-items-center disabled:opacity-40 shrink-0">➤</button>
        </div>
        <p className="text-xs text-zinc-400 mt-2 text-center">Calls Mistral directly — same as Kotlin ChatScreen.callMistralApi</p>
      </div>
    </div>
  );
}
