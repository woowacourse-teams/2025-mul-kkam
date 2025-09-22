# 기본 속성 보존 (리플렉션/Crashlytics용)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTablem,Exceptions

# Kakao SDK 모델
-keep class com.kakao.sdk.**.model.* { <fields>; }

# WorkManager Worker 이름 유지
-keepnames class * extends androidx.work.ListenableWorker

# Kotlinx Serialization
-keepclasseswithmembers class * { @kotlinx.serialization.Serializable *; }
-keepclassmembers class **$$serializer { *; }
-keepclassmembers class * implements kotlinx.serialization.KSerializer { *; }

# Timber 로그 제거
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Retrofit 기본 클래스 및 핵심 타입 유지
-keep class retrofit2.** { *; }
-keep class kotlin.coroutines.Continuation { *; }
-keep class retrofit2.Response { *; }

# 커스텀 CallAdapterFactory 및 어댑터 & NetworkResult 타입 유지
-keep class com.mulkkam.data.remote.adapter.** { *; }
-keep class com.mulkkam.data.remote.model.** { *; }
-keep class kotlin.Result

# 서비스 인터페이스 보존
-keep interface com.mulkkam.data.remote.service.** { <methods>; }
