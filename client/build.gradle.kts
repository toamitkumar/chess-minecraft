val lwjglVersion: String by project
val jomlVersion: String by project
val logbackVersion: String by project

val lwjglNatives = when {
    System.getProperty("os.name").contains("Mac", true) -> {
        if (System.getProperty("os.arch") == "aarch64") "natives-macos-arm64" else "natives-macos"
    }
    System.getProperty("os.name").contains("Windows", true) -> "natives-windows"
    else -> "natives-linux"
}

dependencies {
    implementation(project(":common"))

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    for (module in listOf("lwjgl", "lwjgl-glfw", "lwjgl-opengl", "lwjgl-openal", "lwjgl-stb")) {
        implementation("org.lwjgl:$module")
        runtimeOnly("org.lwjgl:$module::$lwjglNatives")
    }
    implementation("org.joml:joml:$jomlVersion")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
}
