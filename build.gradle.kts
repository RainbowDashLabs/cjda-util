plugins {
    java
    `maven-publish`
    `java-library`
}

repositories {
    mavenLocal {
        isAllowInsecureProtocol = true
    }
    maven {
        url = uri("https://jcenter.bintray.com")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api("net.dv8tion:JDA:4.2.0_229")
    api("org.apache.commons", "commons-text", "1.9")
    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

group = "de.chojo"
version = "1.0"
description = "CJDAUtil"
java.sourceCompatibility = JavaVersion.VERSION_11

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        groupId = project.group as String?
        artifactId = project.name
        version = project.version as String?
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/RainbowDashLabs/cdja-util")
            credentials {
                username = System.getenv("secrets.GITHUB_USERNAME")
                password = System.getenv("secrets.GITHUB_TOKEN")
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}



tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}