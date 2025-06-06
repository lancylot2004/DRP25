package dev.lancy.drp25.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.SupabaseClient


val client: SupabaseClient = createSupabaseClient(
    supabaseUrl = "https://zepduojefkyzoreleeoi.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InplcGR1b2plZmt5em9yZWxlZW9pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg5NzA5OTMsImV4cCI6MjA2NDU0Njk5M30.bXfAH98IEQzoHm3pprtPtSnoB_fcGsF2MW3raoDHz3M"
) {
    install(Postgrest)
}
