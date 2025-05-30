pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "WhatToCook"
include(":app")
include(":ui_kit")
include(":network")
include(":core_api")
include(":feature_recipes_list")
include(":repo_recipes_list")
include(":local_db")
include(":feature_recipe_card")
include(":repo_recipe_card")
include(":feature_ingredients_recalculation")
include(":repo_ingredients")
include(":core_impl")
include(":core_factory")
