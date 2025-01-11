import java.lang.Boolean.parseBoolean

plugins {
  id("java")
  id("maven-publish")
  id("com.gradleup.shadow") version ("9.0.0-beta4") apply (false)
  id("io.papermc.paperweight.patcher") version ("1.7.7")
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
  apply(plugin = "java")

  java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
  }

  tasks.withType<JavaCompile>().configureEach {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(21)
  }
}

paperweight {
  serverProject.set(project(":etheria-server"))

  usePaperUpstream(providers.gradleProperty("paperRef")) {

    remapRepo.set(paperMavenPublicUrl)
    decompileRepo.set(paperMavenPublicUrl)

    withPaperPatcher {
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
  apply(plugin = "maven-publish")

  repositories {
    mavenCentral()
    maven(paperMavenPublicUrl)
    maven("https://repo.subkek.space/maven-public/")
  }

  publishing {
    repositories {
      maven("https://repo.subkek.space/maven-public/") {
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

  if (parseBoolean("${properties["publishPaperclipReobf"]}")) {
    publications.create<MavenPublication>("paperclipReobf") {
      artifact(tasks.createReobfPaperclipJar) {
        artifactId = "paperclip-reobf"
      }
    }
  }
}

tasks.generateDevelopmentBundle {
  apiCoordinates.set("org.subkek.etheria:etheria-api")
  libraryRepositories.addAll("https://repo.subkek.space/maven-public/")
}
