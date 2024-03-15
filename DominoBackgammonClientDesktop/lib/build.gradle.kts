plugins {
    `java-library`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    implementation("com.fasterxml.woodstox:woodstox-core:6.5.1")
    implementation("javax.xml.stream:stax-api:1.0-2")
}