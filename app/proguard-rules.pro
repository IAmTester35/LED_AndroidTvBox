# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Gson
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontwarn com.google.gson.**

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Kotlin
-keep class kotlin.reflect.** { *; }
-keep interface kotlin.reflect.** { *; }

# Data Models (Keep them to avoid obfuscation issues with reflection/serialization)
-keep class com.reecotech.androidtvbox.data.model.** { *; }

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Coroutines (Crucial for Retrofit suspend functions)
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class kotlin.Result { *; }

# Keep all application classes that might be used via reflection or serialization
-keep class com.reecotech.androidtvbox.data.remote.** { *; }
-keep class com.reecotech.androidtvbox.di.** { *; }
-keep class com.reecotech.androidtvbox.domain.model.** { *; }