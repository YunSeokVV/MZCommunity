plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mzcommunity"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mzcommunity"
        minSdk = 24
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth-ktx")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // 로그를 보기 편하게 해주는 라이브러리
    implementation("com.orhanobut:logger:2.2.0")

    // 네이버 아이디로 로그인
    implementation("com.navercorp.nid:oauth:5.9.0") // jdk 11

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.19.0")

    // 뷰페이저2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // circleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // by viewModels 를 사용하기 위해 추가한 라이브러리
    implementation("androidx.activity:activity-ktx:1.5.0")
    // by viewModels 를 프레그먼트에서 사용하기 위해 추가함
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.44")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}