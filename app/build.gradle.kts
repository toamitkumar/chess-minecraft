val logbackVersion: String by project

plugins {
    application
}

application {
    mainClass.set("com.knightscrusade.app.MainKt")
    applicationDefaultJvmArgs = listOf(
        "-XstartOnFirstThread" // Required for GLFW on macOS
    )
}

dependencies {
    implementation(project(":common"))
    implementation(project(":client"))
    implementation(project(":server"))
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
}
