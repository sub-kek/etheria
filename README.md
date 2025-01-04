# Etheria

[![Etheria CI](https://github.com/sub-kek/Etheria/actions/workflows/build.yml/badge.svg)](https://github.com/sub-kek/Etheria/actions/workflows/build.yml)
[![Etheria Download](https://img.shields.io/github/downloads/sub-kek/Etheria/total?color=0&logo=github)](https://github.com/sub-kek/Etheria/releases/latest)
[![Discord](https://badgen.net/discord/online-members/eRvwvmEXWz?icon=discord&label=Discord&list=what)](https://discord.gg/eRvwvmEXWz)

> Fork of [Pufferfish](https://github.com/pufferfish-gg/Pufferfish)
## How to download
Etheria use the same paperclip jar system that Paper uses.

You can download the latest build of Etheria by going [here](https://github.com/sub-kek/Etheria/releases/latest)

You can also [build it yourself](https://github.com/sub-kek/Etheria#building).
## How to link plugin api
### gradle
```kotlin
repositories {
  maven("https://repo.bambooland.fun/maven-public/")
}

dependencies {
  compileOnly("org.subkek.etheria:etheria-api:1.21.4-R0.1-SNAPSHOT")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
```

### maven
```xml
<repository>
  <id>subkek</id>
  <url>https://repo.bambooland.fun/maven-public/</url>
</repository>
```
```xml
<dependency>
  <groupId>org.subkek.etheria</groupId>
  <artifactId>etheria-api</artifactId>
  <version>1.21.4-R0.1-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
 ```
## Building
You need JDK 21 and good Internet conditions.

Clone this repo, run `./gradlew applyPatches`, then run `./gradlew createReobfPaperclipJar` in your terminal.

You can find the jars in the `./build/libs/` directory.
