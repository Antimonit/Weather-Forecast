package com.gelo.weatherforecast.injection.module

import com.gelo.weatherforecast.api.WeatherAPI
import com.gelo.weatherforecast.utils.URL
import com.google.gson.GsonBuilder
import com.squareup.moshi.*
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

@Module
@Suppress
object NetworkModule {

    @Provides
    @Reusable
    @JvmStatic
    internal fun providesWeatherAPI(retrofit: Retrofit): WeatherAPI{
        return retrofit.create(WeatherAPI::class.java)
    }

    // TODO: low: Property "gson" is never used
    // TODO: low: Please don't use Gson. Moshi (or KotlinX serialization) will do better job.
    var gson = GsonBuilder()
        .setLenient()
        .create()

    var interceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .retryOnConnectionFailure(true)
        .writeTimeout(2, TimeUnit.MINUTES)
        .readTimeout(2, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.MINUTES)
        .build()

    // TODO: medium: Why not just use `List<>` instead of `ArrayList<>`?
    abstract class MoshiArrayListJsonAdapter<C : MutableCollection<T>?, T> private constructor(
        private val elementAdapter: JsonAdapter<T>
    ) :
        JsonAdapter<C>() {
        abstract fun newCollection(): C

        @Throws(IOException::class)
        override fun fromJson(reader: JsonReader): C {
            val result = newCollection()
            reader.beginArray()
            while (reader.hasNext()) {
                result?.add(elementAdapter.fromJson(reader)!!)
            }
            reader.endArray()
            return result
        }

        @Throws(IOException::class)
        override fun toJson(writer: JsonWriter, value: C?) {
            writer.beginArray()
            for (element in value!!) {
                elementAdapter.toJson(writer, element)
            }
            writer.endArray()
        }

        override fun toString(): String {
            return "$elementAdapter.collection()"
        }

        companion object {
            val FACTORY = Factory { type, annotations, moshi ->
                val rawType = Types.getRawType(type)
                if (annotations.isNotEmpty()) return@Factory null
                if (rawType == ArrayList::class.java) {
                    return@Factory newArrayListAdapter<Any>(
                        type,
                        moshi
                    ).nullSafe()
                }
                null
            }

            private fun <T> newArrayListAdapter(
                type: Type,
                moshi: Moshi
            ): JsonAdapter<MutableCollection<T>> {
                val elementType =
                    Types.collectionElementType(
                        type,
                        MutableCollection::class.java
                    )

                val elementAdapter: JsonAdapter<T> = moshi.adapter(elementType)

                return object :
                    MoshiArrayListJsonAdapter<MutableCollection<T>, T>(elementAdapter) {
                    override fun newCollection(): MutableCollection<T> {
                        return ArrayList()
                    }
                }
            }
        }
    }

    val moshi = Moshi.Builder()
        .add(MoshiArrayListJsonAdapter.FACTORY)
        .build()

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRetrofitInterface(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }
}