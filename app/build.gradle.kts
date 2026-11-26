import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Googles öffentliche AdMob-Test-IDs (Debug, und Release-Standard falls keine echten IDs gesetzt sind)
val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
val TEST_BANNER_ID = "ca-app-pub-3940256099942544/9214589741"

// Eine Quelle für die Version: steht auch im Dateinamen von AAB/APK (kindwords-<version>-build<code>-release.aab).
val APP_VERSION_CODE = 23
val APP_VERSION_NAME = "1.1.2"

base.archivesName.set("kindwords-$APP_VERSION_NAME-build$APP_VERSION_CODE")

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
        versionCode = APP_VERSION_CODE
        versionName = APP_VERSION_NAME

        // AdMob: Debug nutzt immer Googles Test-IDs (nie echte Anzeigen im Test); Release nimmt die echten IDs aus gradle.properties.
        manifestPlaceholders["admobAppId"] = TEST_APP_ID
        buildConfigField("String", "ADMOB_BANNER_ID", "\"" + TEST_BANNER_ID + "\"")
        // Supabase (nur Lesen für anon, RLS): Satz-Updates. Der publishable Key ist öffentlich gedacht, kein Geheimnis.
        buildConfigField("String", "SUPABASE_URL", "\"https://jqpmwwjffmtlhrnyatlc.supabase.co\"")
        buildConfigField("String", "SUPABASE_KEY", "\"sb_publishable_U52Oh0f_lz0bIXzBMyIu6Q_dggPMCu2\"")
        buildConfigField("boolean", "ADMOB_CONSENT_DEBUG_EEA", providers.gradleProperty("ADMOB_CONSENT_DEBUG_EEA").getOrElse("false"))
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
        release {
            signingConfigs.findByName("release")?.let { signingConfig = it }
            manifestPlaceholders["admobAppId"] = providers.gradleProperty("ADMOB_APP_ID").getOrElse(TEST_APP_ID)
            buildConfigField("String", "ADMOB_BANNER_ID", "\"" + providers.gradleProperty("ADMOB_BANNER_ID").getOrElse(TEST_BANNER_ID) + "\"")
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
        buildConfig = true
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
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)
    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
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

// Warnung, solange im Release noch Googles Test-IDs stehen (dann gibt es keine Einnahmen).
gradle.taskGraph.whenReady {
    val releaseBuild = allTasks.any { it.name.contains("Release") && (it.name.startsWith("bundle") || it.name.startsWith("assemble")) }
    val appId = providers.gradleProperty("ADMOB_APP_ID").getOrElse("")
    if (releaseBuild && (appId.isEmpty() || appId.startsWith("ca-app-pub-3940256099942544"))) {
        logger.warn("WARNUNG: Release mit AdMob-Test-IDs (ADMOB_APP_ID/ADMOB_BANNER_ID in gradle.properties echte IDs eintragen).")
    }
}
