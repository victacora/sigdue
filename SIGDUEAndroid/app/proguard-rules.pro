-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
 }

-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**

-dontwarn net.sqlcipher.**

-keep class org.greenrobot.greendao.* {*;}

-keep class com.sigdue.db.* {*;}

-keep class com.sigdue.webservice.api.* {*;}

-keep class com.sigdue.webservice.modelo.* {*;}