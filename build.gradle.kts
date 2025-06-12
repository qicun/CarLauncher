import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
}

/////////////////////////////////////////////////////////////////////////////////////////
//[基于环境信息自动生成版本信息][Version: 20250326-01][start]/////////////////////////////////
fun execCommand(command: String): String {
    val buffer = ByteArrayOutputStream()
    exec {
        commandLine = command.split(" ")
        standardOutput = buffer
    }
    return buffer.toString().trim()
}

/**
 * 基于 git 信息计算版本号
 * @param versionCodeOffset 版本号的偏移量，默认0
 * @return git commit 数与 versionCodeOffset 的和
 */
fun genAutoVersionCode(versionCodeOffset: Int = 0): Int {
    val command = "git rev-list HEAD --count"
    val gitCommitCount = execCommand(command).toIntOrNull() ?: 0
    return gitCommitCount + versionCodeOffset
}

/**
 * 基于当前时间计算版本名
 * @param baseVersionName 业务指定的基础版本名信息
 * @param snapshot 是否为 snapshot 版本。当为 true 时，会附加 SNAPSHOT 标识
 * @param versionCode 版本号
 * @return 当前时间 baseVersionName-versionCode-yyyyMMddHHmm-commit[-SNAPSHOT]
 */
fun genAutoVersionName(baseVersionName: String, snapshot: Boolean, versionCode: Int): String {
    val builder = StringBuilder()
    builder.append(baseVersionName)
    builder.append("-")
    builder.append(versionCode)
    builder.append("-")
    builder.append(SimpleDateFormat("yyyyMMddHHmm").format(Date()))
    builder.append("-")
    builder.append(execCommand("git rev-parse --short HEAD"))
    if (snapshot) {
        builder.append("-SNAPSHOT")
    }
    return builder.toString()
}

fun getRaiteMavenUsername(): String {
    var username = ""
    kotlin.runCatching {
        val localProperties = Properties().apply {
            FileInputStream(file("local.properties")).use { stream ->
                load(stream)
            }
        }
        username = localProperties.getProperty("maven.raite.username")
    }.onFailure {
        // ignore
    }
    return username
}

fun getRaiteMavenPassword(): String {
    var password = ""
    kotlin.runCatching {
        val localProperties = Properties().apply {
            FileInputStream(file("local.properties")).use { stream ->
                load(stream)
            }
        }
        password = localProperties.getProperty("maven.raite.password")
    }.onFailure {
        // ignore
    }
    return password
}

/**
 * 生成适用于 maven 的版本名，例如 1.0.0, 1.0.0-SNAPSHOT
 * @param baseVersionName 业务指定的基础版本名信息
 * @param snapshot 是否为 snapshot 版本。当为 true 时，会附加 SNAPSHOT 标识
 * @return baseVersionName[-SNAPSHOT]
 */
fun genAutoMavenVersionName(baseVersionName: String, snapshot: Boolean): String {
    return if (snapshot) {
        "$baseVersionName-SNAPSHOT"
    } else {
        baseVersionName
    }
}
//[基于环境信息自动生成版本信息][Version: 20250326-01][end]///////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

// 所有 maven aar 使用统一的版本号
val baseVersionName = "1.0.0"

// maven artifact 后缀，一般使用与项目分支相关的定义值
// 以此能够体现该 maven aar 是在哪个项目分支上发布的
val mavenArtifactIdSuffix = ""

val snapshot = true
val autoVersionCode = genAutoVersionCode()
val autoVersionName = genAutoVersionName(
    "${baseVersionName}${mavenArtifactIdSuffix}", snapshot, autoVersionCode,
)
val autoMavenVersionName = genAutoMavenVersionName(baseVersionName, snapshot)
val raitePassword = getRaiteMavenPassword()
val raiteUsername = getRaiteMavenUsername()

println("autoVersionCode: $autoVersionCode")
println("autoVersionName: $autoVersionName")
println("autoMavenVersionName: $autoMavenVersionName")
println("raitePassword: $raitePassword")
println("raiteUsername: $raiteUsername")

rootProject.ext["autoVersionCode"] = autoVersionCode
rootProject.ext["autoVersionName"] = autoVersionName
rootProject.ext["autoMavenVersionName"] = autoMavenVersionName
rootProject.ext["mavenArtifactIdSuffix"] = mavenArtifactIdSuffix
rootProject.ext["raitePassword"] = raitePassword
rootProject.ext["raiteUsername"] = raiteUsername

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(10, TimeUnit.MINUTES)
    resolutionStrategy.cacheChangingModulesFor(10, TimeUnit.MINUTES)
}

afterEvaluate {
    // 提供 ci 集成相关的任务
    // raiteCiDebug 在项目根目录下生成 ${rootProject.name}-debug.apk
    // raiteCiRelease 在项目根目录下生成 ${rootProject.name}-release.apk
    // raiteCi 依次调用 raiteCiDebug, raiteCiRelease
    tasks.create(name = "raiteCiDebug") {
        dependsOn(":app:assembleDebug")
        doLast {
            // copy fromFile to outFile
            val fromFile = "app/build/outputs/apk/debug/app-debug.apk"
            val outFile = "${rootProject.name}-debug.apk"
            Files.copy(
                Path.of(project.rootDir.absolutePath, fromFile),
                Path.of(project.rootDir.absolutePath, outFile),
                StandardCopyOption.REPLACE_EXISTING,
            )
        }
    }
    tasks.create(name = "raiteCiRelease") {
        dependsOn(":app:assembleRelease")
        doLast {
            // copy fromFile to outFile
            val fromFile = "app/build/outputs/apk/release/app-release.apk"
            val outFile = "${rootProject.name}-release.apk"
            Files.copy(
                Path.of(project.rootDir.absolutePath, fromFile),
                Path.of(project.rootDir.absolutePath, outFile),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
    tasks.create(name = "raiteCi") {
        dependsOn("raiteCiDebug", "raiteCiRelease")
    }
}

tasks.withType(JavaCompile::class.java) {
    options.compilerArgs.apply {
        add("-Xbootclasspath/p:/libs/framework.jar")
        add("-Xlint:unchecked")
        add("-Xlint:deprecation")
    }
}
