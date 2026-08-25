import { initializeApp, getApps } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const cfg = {
  apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY,
  authDomain: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
  appId: process.env.NEXT_PUBLIC_FIREBASE_APP_ID,
};

const app = getApps().length ? getApps()[0] : (cfg.apiKey ? initializeApp(cfg as any) : null as any);
export const auth = app ? getAuth(app) : null as any;
export const db = app ? getFirestore(app) : null as any;

// helper to get ID token for backend HMAC auth
export async function getIdToken(): Promise<string | null> {
  try { return (await auth?.currentUser?.getIdToken(false)) || null; } catch { return null; }
}
