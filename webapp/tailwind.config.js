/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: "class",
  content: ["./app/**/*.{ts,tsx}", "./components/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: { DEFAULT: "#4F46E5", dark: "#3730A3", light: "#EEF2FF", soft: "#6366F1" },
        surface: "#F8FAFC",
        ink: "#0F172A",
        muted: "#64748B",
        cyan: "#06B6D4",
        amber: "#F59E0B",
      },
      borderRadius: { 'neo': '20px', '2xl': '16px', '3xl': '20px' },
      fontFamily: {
        display: ["Plus Jakarta Sans", "Inter", "system-ui", "sans-serif"],
        sans: ["Inter", "system-ui", "sans-serif"],
      },
      boxShadow: {
        card: "0 4px 24px rgba(15,23,42,0.06)",
        "card-hover": "0 8px 32px rgba(15,23,42,0.10)",
        soft: "0 2px 12px rgba(15,23,42,0.06)",
      }
    },
  },
  plugins: [],
};
