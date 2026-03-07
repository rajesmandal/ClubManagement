# Retrofit rules
-keepattributes Signature, InnerClasses, AnnotationDefault
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.**

# Gson rules
-keep class com.google.gson.** { *; }
-keep class com.sohitechnology.gymstudio.hammer.data.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Hilt rules
-keep class dagger.hilt.** { *; }
-keep interface dagger.hilt.** { *; }
-keep class com.sohitechnology.gymstudio.hammer.Hilt_** { *; }
-keep class com.sohitechnology.gymstudio.hammer.**_HiltModules { *; }

# Firebase rules
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Compose rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# WorkManager rules
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class * extends androidx.work.Worker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.work.ListenableWorker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}

# Room rules (WorkManager uses Room)
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep line numbers for Crashlytics
-keepattributes SourceFile,LineNumberTable

# Standard ProGuard rules
-dontoptimize
-dontpreverify
