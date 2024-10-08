/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details on building Java & JVM projects, please refer to
 * https://docs.gradle.org/8.8/userguide/building_java_projects.html in the Gradle
 * documentation.
 */

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.lucene:lucene-analysis-common:9.11.1")
    implementation("org.apache.lucene:lucene-queries:9.11.1")
    implementation("org.apache.solr:solr-core:9.6.1")
    implementation("org.apache.solr:solr-solrj:9.6.1")

    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.2")
    testImplementation("org.apache.lucene:lucene-test-framework:9.11.1")
    testImplementation("org.apache.lucene:lucene-backward-codecs:9.11.1")
    testImplementation("org.apache.solr:solr-test-framework:9.6.1")
}

base {
    archivesName = "capitularia-lucene-tools"
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<Javadoc> {
    options {
        this as StandardJavadocDocletOptions
        addStringOption(
            "tag",
            "lucene.spi:t:SPI Name:"
        )
    }
}

tasks.named<Test>("test") {
    // lucene still uses JUnit 4
    useJUnit()
    jvmArgs("-Djava.security.egd=file:/dev/./urandom") // needed by SolrTestCaseJ4
}
