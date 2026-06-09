package com.example.plantify.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://zywspxhnpegpxhjwvpqb.supabase.co",
        supabaseKey = "sb_publishable_fSQy0N4vZUbPa0XN5ToVEQ_sWK9gGeV"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
