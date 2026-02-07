val kotlinVersion: String by project
val protobufPluginVersion: String by project
val junitVersion: String by project
val slf4jVersion: String by project

plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("com.google.protobuf") version "0.9.4" apply false
}

allprojects {
    group = "com.knightscrusade"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    dependencies {
        "testImplementation"(platform("org.junit:junit-bom:$junitVersion"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "implementation"("org.slf4j:slf4j-api:$slf4jVersion")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
