/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    const backend = process.env.NEXT_PUBLIC_API_BASE || "http://localhost:3001";
    return [
      { source: "/api/backend/:path*", destination: `${backend}/:path*` },
      { source: "/api/v1/:path*", destination: `${backend}/api/v1/:path*` },
      { source: "/admin/:path*", destination: `${backend}/admin/:path*` },
      { source: "/health", destination: `${backend}/health` },
      { source: "/ready", destination: `${backend}/ready` },
    ];
  },
};
module.exports = nextConfig;
