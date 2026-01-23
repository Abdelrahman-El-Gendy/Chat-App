pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Chat-App"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":feature:auth_identity")
include(":feature:chat_room")
include(":feature:media")
include(":feature:work")
