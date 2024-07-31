pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "etheria"
include("etheria-api", "etheria-server", "paper-api-generator")