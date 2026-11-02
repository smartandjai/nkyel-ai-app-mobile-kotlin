package com.smartandj.gabomagpt.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.smartandj.gabomagpt.data.local.GabomaDatabase
import com.smartandj.gabomagpt.data.local.dao.ChatDao
import com.smartandj.gabomagpt.data.remote.GabomaNetworkConfig
import com.smartandj.gabomagpt.data.repository.ChatRepositoryImpl
import com.smartandj.gabomagpt.domain.repository.ChatRepository
import com.smartandj.gabomagpt.presentation.settings.ThemePreferencesManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// ── Extension: DataStore singleton via delegate ──────────────────────
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gaboma_settings")

// ═══════════════════════════════════════════════════════════════════════
// REPOSITORY BINDINGS
// ═══════════════════════════════════════════════════════════════════════

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
}

// ═══════════════════════════════════════════════════════════════════════
// NETWORK MODULE — OkHttp + Ktor HttpClient
// ═══════════════════════════════════════════════════════════════════════

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(GabomaNetworkConfig.CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(GabomaNetworkConfig.READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .writeTimeout(GabomaNetworkConfig.WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideKtorHttpClient(okHttpClient: OkHttpClient): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// DATABASE MODULE — Room + ChatDao
// ═══════════════════════════════════════════════════════════════════════

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GabomaDatabase {
        return Room.databaseBuilder(
            context,
            GabomaDatabase::class.java,
            "gaboma_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: GabomaDatabase): ChatDao {
        return database.chatDao
    }
}

// ═══════════════════════════════════════════════════════════════════════
// PREFERENCES MODULE — DataStore + ThemePreferencesManager
// ═══════════════════════════════════════════════════════════════════════

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideThemePreferencesManager(dataStore: DataStore<Preferences>): ThemePreferencesManager {
        return ThemePreferencesManager(dataStore)
    }
}
