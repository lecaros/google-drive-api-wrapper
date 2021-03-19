plugins {
    kotlin("jvm") version "1.4.31"

    id("maven-publish")
    id("java-library")
}

group = "com.merkenlabs.googleapiwrapper.drive"
version = "0.2.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation( "org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.google.apis:google-api-services-drive:v3-rev20210315-1.31.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}


tasks.getByName<Jar>("jar") {
    enabled = true
}

java {
    withSourcesJar()
}

publishing{
    publications {
        create<MavenPublication>("google-drive-api-wrapper") {
            from(components["java"])
        }
    }
}
