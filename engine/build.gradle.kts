plugins { kotlin("jvm"); id("io.gitlab.arturbosch.detekt") }
kotlin { jvmToolchain(21) }
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
}
dependencies {
    implementation("org.apache.commons:commons-compress:1.28.0")
    implementation("commons-io:commons-io:2.20.0")
    compileOnly("org.json:json:20250517")
    testImplementation("org.json:json:20250517")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<JavaCompile>().configureEach { options.release.set(17) }
java { targetCompatibility = JavaVersion.VERSION_17 }
detekt { config.setFrom(rootProject.file("detekt.yml")); buildUponDefaultConfig = true }
