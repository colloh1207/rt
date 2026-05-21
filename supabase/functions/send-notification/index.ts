import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

serve(async (req) => {
  const supabase = createClient(Deno.env.get("SUPABASE_URL")!, Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!)
  const { user_id, type, title, body, data = {} } = await req.json()
  const { data: profile } = await supabase.from("users").select("fcm_token").eq("id", user_id).single()
  await supabase.from("notifications").insert({ user_id, type, title, body, data, is_read: false })
  if (profile?.fcm_token) {
    const fcmKey = Deno.env.get("FCM_SERVER_KEY")
    if (fcmKey) {
      await fetch("https://fcm.googleapis.com/fcm/send", {
        method: "POST",
        headers: { "Authorization": `key=${fcmKey}`, "Content-Type": "application/json" },
        body: JSON.stringify({ to: profile.fcm_token, notification: { title, body }, data })
      })
    }
  }
  return new Response(JSON.stringify({ success: true }), { headers: { "Content-Type": "application/json" } })
})
