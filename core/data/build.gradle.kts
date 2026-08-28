import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

// local.properties から値を読み込む処理
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}
val apiKey = localProperties.getProperty("NEWS_API_KEY") ?: ""

android {
    namespace = "com.github.takayuki_hub.dashboard.core.data"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig を有効化
        buildFeatures {
            buildConfig = true
        }

        // BuildConfig クラスに API_KEY 定数を生成
        buildConfigField("String", "NEWS_API_KEY", "\"$apiKey\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:model"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
}