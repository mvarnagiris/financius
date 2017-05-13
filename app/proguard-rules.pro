# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Developer\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class !android.support.v7.internal.view.menu.MenuBuilder, !android.support.v7.internal.view.menu.SubMenuBuilder { *; }

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepattributes SourceFile, LineNumberTable

-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.google.common.**
-dontwarn dagger.internal.**
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.tz.**
