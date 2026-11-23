import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "app.rpkindwords"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.rpkindwords"
        minSdk = 26
        targetSdk = 36
        versionCode = 17
        versionName = "1.1.0"
    }

    androidResources {
        // Erzeugt locales_config.xml aus den values-xx Ordnern (per-App-Sprache, Android 13+)
        generateLocaleConfig = true
        localeFilters += listOf("en", "de")
    }

    // Upload-Key liegt ausserhalb des Repos; keystore.properties ist gitignored.
    val keystoreProps = Properties().apply {
        val f = rootProject.file("keystore.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }
    signingConfigs {
        if (keystoreProps.isNotEmpty()) {
            create("release") {
                storeFile = file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        // AdMob: Debug immer mit Googles Test-IDs (echte IDs im Test können das Konto sperren).
        debug {
            resValue("string", "admob_app_id", "ca-app-pub-3940256099942544~3347511713")
            resValue("string", "admob_banner_unit_id", "ca-app-pub-3940256099942544/9214589741")
        }
        release {
            // Echte IDs von RP Kind Words (AdMob-Herausgeber pub-2109805482260954). Release-Builds liefern echte Anzeigen:
            // nie auf eigene Anzeigen klicken, eigenes Gerät in res/values/ads.xml als Testgerät eintragen.
            resValue("string", "admob_app_id", "ca-app-pub-2109805482260954~7836980498")
            resValue("string", "admob_banner_unit_id", "ca-app-pub-2109805482260954/2155077277")
            signingConfigs.findByName("release")?.let { signingConfig = it }
            // Symboltabelle für nativen Code (AndroidX-Libs) im Bundle: bessere Absturz-/ANR-Analyse in der Play Console
            ndk.debugSymbolLevel = "SYMBOL_TABLE"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        resValues = true
    }

    lint {
        // Keine statischen Texte im Code/Layout, jede Sprache vollständig
        error += setOf("HardcodedText", "MissingTranslation", "ExtraTranslation")
        abortOnError = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.play.app.update)
    implementation(libs.androidx.fragment) // Play-Update zieht Fragment 1.0.0 mit; ActivityResult braucht >= 1.3.0
    implementation(libs.mlkit.translate)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
    implementation(libs.admob.nextgen) // Werbung (AdMob + UMP-Einwilligung), siehe docs/PLAN-WERBUNG.md
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    testImplementation(libs.org.json)
}
