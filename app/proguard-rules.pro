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

# Robust Retrofit Rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep generic signature of Response (Critical for Response<T>)
-keep class retrofit2.Response { *; }

# Keep all application classes that might be used via reflection or serialization
-keep class com.reecotech.androidtvbox.data.remote.** { *; }
-keep interface com.reecotech.androidtvbox.data.remote.** { *; }
-keep class com.reecotech.androidtvbox.data.model.** { *; }
-keep class com.reecotech.androidtvbox.di.** { *; }
-keep class com.reecotech.androidtvbox.domain.model.** { *; }

# Kotlin Serialization
-keep class kotlinx.serialization.** { *; }
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.**

# Coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }

# DataStore - Prevent R8 from stripping corruption handler
-keep class androidx.datastore.** { *; }
-keep class androidx.datastore.core.** { *; }
-keep class androidx.datastore.preferences.** { *; }
-keep class androidx.datastore.preferences.core.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# Protobuf Lite (used by DataStore internally)
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**
