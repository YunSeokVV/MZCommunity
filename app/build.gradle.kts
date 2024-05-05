plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")

}

android {

    signingConfigs {
        create("release") {
            keyAlias = "key0"
            storeFile = file("C:\\Users\\asus vivo\\AndroidStudioProjects\\keystorePath\\key.jks")
            storePassword = "@!dakota3276"
            keyPassword = "@!dakota3276"
        }
    }
    namespace = "com.example.mzcommunity"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mzcommunity"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // roomDB
    implementation ("androidx.room:room-runtime:2.5.2")
    kapt ("androidx.room:room-compiler:2.5.2")

    //exoPlayer
    implementation("com.google.android.exoplayer:exoplayer-core:2.18.2")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.18.2")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.18.2")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth-ktx")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // 로그를 보기 편하게 해주는 라이브러리
    implementation("com.orhanobut:logger:2.2.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // 뷰페이저2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // circleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // by viewModels 를 사용하기 위해 추가한 라이브러리
    //implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.activity:activity-ktx:1.5.0")

    // by viewModels 를 프레그먼트에서 사용하기 위해 추가함
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Add the dependency for the Cloud Storage library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-storage")

    implementation("com.google.firebase:firebase-firestore:24.10.3")

    implementation("com.facebook.android:facebook-login:latest.release")

    implementation("androidx.cardview:cardview:1.0.0")


    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}