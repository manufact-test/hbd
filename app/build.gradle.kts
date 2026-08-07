import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val approvedSquirrelSvg = rootProject.layout.projectDirectory.file(
    "branding/assets/logo/bdaysquirrel-squirrel-icon.svg",
)
val generatedLauncherResDir = layout.buildDirectory.dir("generated/bdaysquirrel-launcher/res")
val generatedLauncherResFile = layout.buildDirectory.get().asFile.resolve(
    "generated/bdaysquirrel-launcher/res",
)

val generateBdaySquirrelLauncherIcon = tasks.register("generateBdaySquirrelLauncherIcon") {
    notCompatibleWithConfigurationCache(
        "Reads the maintained branding SVG and generates an Android launcher resource.",
    )
    inputs.file(approvedSquirrelSvg)
    outputs.dir(generatedLauncherResDir)

    doLast {
        val svg = approvedSquirrelSvg.asFile.readText()
        val pathTags = Regex("""<path\b[^>]*/>""")
            .findAll(svg)
            .map { it.value }
            .toList()

        require(pathTags.isNotEmpty()) {
            "No SVG paths found in ${approvedSquirrelSvg.asFile}"
        }

        val fillRegex = Regex("""fill="([^"]+)"""")
        val dataRegex = Regex("""d="([^"]+)"""")
        val vectorPaths = pathTags.mapNotNull { tag ->
            val fill = fillRegex.find(tag)?.groupValues?.get(1)
            val data = dataRegex.find(tag)?.groupValues?.get(1)
            if (fill == null || data == null || fill == "none") {
                null
            } else {
                "        <path android:fillColor=\"$fill\" android:pathData=\"$data\" />"
            }
        }

        require(vectorPaths.isNotEmpty()) {
            "No drawable SVG paths found in ${approvedSquirrelSvg.asFile}"
        }

        val outputFile = generatedLauncherResDir.get()
            .file("drawable/ic_launcher_foreground.xml")
            .asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <group
        android:pivotX="0"
        android:pivotY="0"
        android:scaleX="1.10"
        android:scaleY="1.10"
        android:translateX="22.1"
        android:translateY="27.6">
${vectorPaths.joinToString("\n")}
    </group>
</vector>
""",
        )
    }
}

android {
    namespace = "com.bdaysquirrel.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.bdaysquirrel.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "0.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main").assets.srcDir("../branding/assets/logo")
        getByName("main").res.srcDir(generatedLauncherResFile)
    }

    signingConfigs {
        create("development") {
            storeFile = file("bdaysquirrel-dev.keystore")
            storePassword = "bdaysquirrel-dev"
            keyAlias = "bdaysquirrel-dev"
            keyPassword = "bdaysquirrel-dev"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("development")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(generateBdaySquirrelLauncherIcon)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")

    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("io.coil-kt.coil3:coil-svg:3.5.0")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:2.8.4")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
