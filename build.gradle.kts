import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
    idea
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
}

group = "me.vlad"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "artifactorySaas"
        credentials(PasswordCredentials::class)
        url = uri("https://wolt.jfrog.io/artifactory/libs-release-local")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.1.3"))

    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-optics")
    ksp("io.arrow-kt:arrow-optics-ksp-plugin:1.1.3")

}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}
//
//buildscript {
//    dependencies {
//        classpath(kotlin("gradle-plugin", version = "1.8.0"))
//    }
//}

idea {
    module {
        // Not using += due to https://github.com/gradle/gradle/issues/8749
        sourceDirs = sourceDirs + file("build/generated/ksp/main/kotlin") // or tasks["kspKotlin"].destination
    }
}
