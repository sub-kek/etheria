import java.lang.Boolean.parseBoolean

plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version("9.0.0-beta4")
    id("io.papermc.paperweight.patcher") version("1.7.7")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

repositories {
    mavenCentral()
    maven(paperMavenPublicUrl) {
        content {
            onlyForConfigurations(configurations.paperclip.name)
        }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.10.4:fat")
    decompiler("org.vineflower:vineflower:1.10.1")
    paperclip("io.papermc:paperclip:3.0.4-SNAPSHOT")
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
}

paperweight {
    serverProject.set(project(":etheria-server"))

    remapRepo.set("https://maven.fabricmc.net/")
    decompileRepo.set("https://files.minecraftforge.net/maven/")

    useStandardUpstream("Pufferfish") {
        url.set(github("pufferfish-gg", "Pufferfish"))
        ref.set(providers.gradleProperty("pufferfishRef"))

        withStandardPatcher {
            apiSourceDirPath.set("pufferfish-api")
            serverSourceDirPath.set("pufferfish-server")

            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))

            apiOutputDir.set(layout.projectDirectory.dir("etheria-api"))
            serverOutputDir.set(layout.projectDirectory.dir("etheria-server"))
        }

        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = layout.projectDirectory.dir("patches/generated-api")
            outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
        maven("https://repo.bambooland.fun/maven-public/")
    }
    publishing {
        repositories {
            maven("https://repo.bambooland.fun/maven-public/") {
                name = "subkek"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

publishing {
    if (parseBoolean("${properties["publishDevBundle"]}")) {
        publications.create<MavenPublication>("devBundle") {
            artifact(tasks.generateDevelopmentBundle) {
                artifactId = "dev-bundle"
            }
        }
    }
}

tasks.generateDevelopmentBundle {
    apiCoordinates.set("org.subkek.etheria:etheria-api")
    libraryRepositories.addAll("https://repo.bambooland.fun/maven-public/")
}
