package com.example.quickgram.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.Auth   // ✅ correct import


object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://vxexigrndbgzgbnbvhtr.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZ4ZXhpZ3JuZGJnemdibmJ2aHRyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM5MjE5NjksImV4cCI6MjA1OTQ5Nzk2OX0.hTWvkU7IRnkSG-kVJVnq6cdLK2Gn8w3nSj_IQXno-Qk"
    ) {
        install(Postgrest)
        install(Auth)   // ✅ correct usage
    }
}
