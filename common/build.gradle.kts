val nettyVersion: String by project
val protobufVersion: String by project
val gsonVersion: String by project
val jomlVersion: String by project

plugins {
    id("com.google.protobuf")
}

dependencies {
    api("io.netty:netty-all:$nettyVersion")
    api("com.google.protobuf:protobuf-java:$protobufVersion")
    api("com.google.code.gson:gson:$gsonVersion")
    api("org.joml:joml:$jomlVersion")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
}

sourceSets {
    main {
        proto {
            srcDir("${rootProject.projectDir}/proto")
        }
    }
}
