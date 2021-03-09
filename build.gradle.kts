plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
    id("maven-publish")
}

group = "com.merkenlabs.googleapiwrapper.drive"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation( "org.jetbrains.kotlin:kotlin-stdlib")
}
