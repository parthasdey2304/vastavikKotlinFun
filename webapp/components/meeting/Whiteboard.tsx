"use client";
import { useRef, useEffect, useState } from "react";
import type { WhiteboardElement, ElementType } from "@/lib/meeting";

export type Tool = "PEN" | "ERASER" | "HAND" | "TEXT";

export function Whiteboard({
  elements,
  onChange,
  readOnly,
  tool,
  onToolChange,
}: {
  elements: WhiteboardElement[];
  onChange: (els: WhiteboardElement[]) => void;
  readOnly?: boolean;
  tool: Tool;
  onToolChange: (t: Tool) => void;
}) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [current, setCurrent] = useState<{ x: number; y: number }[]>([]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.clearRect(0, 0, rect.width, rect.height);
    // grid
    ctx.strokeStyle = "#E5E7EB";
    ctx.lineWidth = 0.5;
    for (let x = 0; x < rect.width; x += 48) { ctx.beginPath(); ctx.moveTo(x, 0); ctx.lineTo(x, rect.height); ctx.stroke(); }
    for (let y = 0; y < rect.height; y += 48) { ctx.beginPath(); ctx.moveTo(0, y); ctx.lineTo(rect.width, y); ctx.stroke(); }
    // existing strokes
    elements.forEach(el => {
      if (el.points.length < 2) return;
      ctx.strokeStyle = el.type === "ERASER" ? "#FFFFFF" : (el.color || "#000");
      ctx.lineWidth = el.strokeWidth || 3;
      ctx.lineCap = "round";
      ctx.lineJoin = "round";
      ctx.beginPath();
      ctx.moveTo(el.points[0].x, el.points[0].y);
      el.points.slice(1).forEach(p => ctx.lineTo(p.x, p.y));
      ctx.stroke();
    });
    // live stroke
    if (current.length > 1) {
      ctx.strokeStyle = tool === "ERASER" ? "#FFFFFF" : "#000";
      ctx.lineWidth = 3;
      ctx.lineCap = "round";
      ctx.beginPath();
      ctx.moveTo(current[0].x, current[0].y);
      current.slice(1).forEach(p => ctx.lineTo(p.x, p.y));
      ctx.stroke();
    }
  }, [elements, current, tool]);

  const pos = (e: React.PointerEvent) => {
    const rect = canvasRef.current!.getBoundingClientRect();
    return { x: e.clientX - rect.left, y: e.clientY - rect.top };
  };

  return (
    <div className="neo p-0 overflow-hidden relative bg-white">
      {/* toolbar */}
      {!readOnly && (
        <div className="absolute top-3 left-3 z-10 flex gap-1.5 bg-white border-2 border-black rounded-xl p-1.5 shadow-[3px_3px_0_0_#000]">
          {(["PEN", "ERASER", "HAND", "TEXT"] as Tool[]).map(t => (
            <button
              key={t}
              onClick={() => onToolChange(t)}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold border-2 ${tool === t ? "bg-black text-white border-black" : "bg-white text-black border-black hover:bg-slate-50"}`}
            >
              {t}
            </button>
          ))}
          <button onClick={() => onChange([])} className="px-3 py-1.5 rounded-lg text-xs font-bold border-2 border-black bg-white hover:bg-red-50">Clear</button>
        </div>
      )}
      <canvas
        ref={canvasRef}
        className="w-full h-[520px] md:h-[640px] touch-none"
        onPointerDown={e => {
          if (readOnly || tool === "HAND") return;
          (e.target as Element).setPointerCapture(e.pointerId);
          setIsDrawing(true);
          setCurrent([pos(e)]);
        }}
        onPointerMove={e => {
          if (!isDrawing) return;
          setCurrent(c => [...c, pos(e)]);
        }}
        onPointerUp={() => {
          if (!isDrawing) return;
          setIsDrawing(false);
          if (current.length > 1) {
            const el: WhiteboardElement = {
              id: `el_${Date.now()}`,
              type: tool === "ERASER" ? "ERASER" : "PEN",
              points: current,
              text: "",
              color: tool === "ERASER" ? "#FFFFFF" : "#000000",
              strokeWidth: tool === "ERASER" ? 14 : 3,
              fontSize: 16,
              bounds: { x: 0, y: 0, width: 0, height: 0 },
            };
            onChange([...elements, el]);
          }
          setCurrent([]);
        }}
      />
      <div className="absolute bottom-3 right-3 text-[11px] text-muted bg-white/90 border border-black rounded-full px-3 py-1">
        Default whiteboard — style: thick bottom/right border, rounded corners
      </div>
    </div>
  );
}
