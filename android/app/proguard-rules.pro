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
