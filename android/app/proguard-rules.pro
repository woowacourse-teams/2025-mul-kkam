# 제네릭/어노테이션/예외/내포정보 보존 (Retrofit 타입반영에 필요)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions,InnerClasses,EnclosingMethod

# Retrofit HTTP 어노테이션(@GET/@POST 등)이 달린 메서드를 가진
# 클래스/인터페이스는 멤버 시그니처를 보존 (raw Call 방지)
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Kakao SDK 내부 API/모델 보존 (패키지 전반 보존이 안전)
-keep class com.kakao.sdk.** { *; }
-keep interface com.kakao.sdk.** { *; }

# Retrofit/OkHttp 자체는 경고/난독화 이슈 방지
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okio.**

-keepattributes KotlinMetadata

# 기본 속성 보존 (리플렉션/Crashlytics용)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Kakao SDK 모델
-keep class com.kakao.sdk.**.model.* { <fields>; }

# WorkManager Worker 이름 유지
-keepnames class * extends androidx.work.ListenableWorker

# Kotlinx Serialization
-keepclassmembers class **$$serializer { *; }
-keepnames class **$$serializer

# Timber 로그 제거
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
