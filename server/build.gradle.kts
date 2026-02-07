val sqliteVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation(project(":common"))
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
}
