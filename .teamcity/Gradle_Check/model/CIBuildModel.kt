package model

import configurations.BuildDistributions
import configurations.ColonyCompatibility
import configurations.Gradleception
import configurations.SanityCheck
import configurations.SmokeTests
import jetbrains.buildServer.configs.kotlin.v10.BuildType

data class CIBuildModel (
        val projectPrefix: String = "Gradle_Check_",
        val rootProjectName: String = "Check",
        val tagBuilds: Boolean = true,
        val buildCacheActive: Boolean = true,
        val masterAndReleaseBranches: List<String> = listOf("master", "release"),
        val stages: List<Stage> = listOf(
            Stage("Sanity Check and Distribution",
                    specificBuilds = listOf(
                            SpecificBuild.SanityCheck,
                            SpecificBuild.BuildDistributions)),
            Stage("Test Embedded Java8 Linux",
                    functionalTests = listOf(
                            TestCoverage(TestType.quick, OS.linux, JvmVersion.java8))),
            Stage("Test Embedded Java7 Windows",
                    functionalTests = listOf(
                            TestCoverage(TestType.quick, OS.windows, JvmVersion.java7))),
            Stage("Test Forked Linux/Windows, Performance, Gradleception",
                    specificBuilds = listOf(
                            SpecificBuild.Gradleception),
                    functionalTests = listOf(
                            TestCoverage(TestType.platform, OS.linux, JvmVersion.java7),
                            TestCoverage(TestType.platform, OS.windows, JvmVersion.java8)),
                    performanceTests = listOf(PerformanceTestType.test)),
            Stage("Test Parallel, Java9, IBM VM, Cross-Version, Smoke Tests, Colony",
                    trigger = Trigger.eachCommit,
                    specificBuilds = listOf(
                            SpecificBuild.SmokeTests, SpecificBuild.ColonyCompatibility),
                    functionalTests = listOf(
                            TestCoverage(TestType.quickFeedbackCrossVersion, OS.linux, JvmVersion.java7),
                            TestCoverage(TestType.quickFeedbackCrossVersion, OS.windows, JvmVersion.java7),
                            TestCoverage(TestType.java9Smoke, OS.linux, JvmVersion.java8),
                            TestCoverage(TestType.parallel, OS.linux, JvmVersion.java7, JvmVendor.ibm))),
            Stage("Test Cross-version (All Versions), No-daemon, Soak Tests, Performance Experiments",
                    trigger = Trigger.daily,
                    functionalTests = listOf(
                            TestCoverage(TestType.soak, OS.linux, JvmVersion.java8),
                            TestCoverage(TestType.soak, OS.windows, JvmVersion.java8),
                            TestCoverage(TestType.crossVersion, OS.linux, JvmVersion.java7),
                            TestCoverage(TestType.crossVersion, OS.windows, JvmVersion.java7),
                            TestCoverage(TestType.noDaemon, OS.linux, JvmVersion.java8),
                            TestCoverage(TestType.noDaemon, OS.windows, JvmVersion.java8)),
                    performanceTests = listOf(
                            PerformanceTestType.experiment)),
            Stage("Performance Historical",
                    trigger = Trigger.weekly,
                    performanceTests = listOf(
                            PerformanceTestType.historical)))
    ) {

    val subProjects = listOf(
            GradleSubproject("announce"),
            GradleSubproject("antlr"),
            GradleSubproject("baseServices"),
            GradleSubproject("baseServicesGroovy"),
            GradleSubproject("buildCacheHttp"),
            GradleSubproject("buildComparison"),
            GradleSubproject("buildInit"),
            GradleSubproject("cli", functionalTests = false),
            GradleSubproject("docs"),
            GradleSubproject("codeQuality"),
            GradleSubproject("compositeBuilds"),
            GradleSubproject("core", crossVersionTests = true),
            GradleSubproject("dependencyManagement", crossVersionTests = true),
            GradleSubproject("diagnostics"),
            GradleSubproject("ear"),
            GradleSubproject("ide"),
            GradleSubproject("ideNative"),
            GradleSubproject("idePlay", crossVersionTests = true),
            GradleSubproject("integTest", crossVersionTests = true),
            GradleSubproject("internalIntegTesting"),
            GradleSubproject("internalPerformanceTesting", crossVersionTests = true),
            GradleSubproject("internalTesting", functionalTests = false),
            GradleSubproject("ivy", crossVersionTests = true),
            GradleSubproject("jacoco"),
            GradleSubproject("javascript"),
            GradleSubproject("jvmServices", functionalTests = false),
            GradleSubproject("languageGroovy"),
            GradleSubproject("languageJava"),
            GradleSubproject("languageJvm"),
            GradleSubproject("languageNative"),
            GradleSubproject("languageScala"),
            GradleSubproject("launcher"),
            GradleSubproject("logging"),
            GradleSubproject("maven", crossVersionTests = true),
            GradleSubproject("messaging"),
            GradleSubproject("modelCore"),
            GradleSubproject("modelGroovy"),
            GradleSubproject("native"),
            GradleSubproject("osgi"),
            GradleSubproject("platformBase"),
            GradleSubproject("platformJvm"),
            GradleSubproject("platformNative"),
            GradleSubproject("platformPlay"),
            GradleSubproject("pluginDevelopment"),
            GradleSubproject("pluginUse", crossVersionTests = true),
            GradleSubproject("plugins"),
            GradleSubproject("processServices"),
            GradleSubproject("publish"),
            GradleSubproject("reporting"),
            GradleSubproject("resources"),
            GradleSubproject("resourcesGcs"),
            GradleSubproject("resourcesHttp"),
            GradleSubproject("resourcesS3"),
            GradleSubproject("resourcesSftp"),
            GradleSubproject("scala"),
            GradleSubproject("signing"),
            GradleSubproject("testKit"),
            GradleSubproject("testingBase"),
            GradleSubproject("testingJvm"),
            GradleSubproject("testingNative"),
            GradleSubproject("toolingApi", crossVersionTests = true),
            GradleSubproject("toolingApiBuilders", functionalTests = false),
            GradleSubproject("workers"),
            GradleSubproject("wrapper", crossVersionTests = true),

            GradleSubproject("soak", unitTests = false, functionalTests = false),

            GradleSubproject("buildScanPerformance", unitTests = false, functionalTests = false),
            GradleSubproject("distributions", unitTests = false, functionalTests = false),
            GradleSubproject("docs", unitTests = false, functionalTests = false),
            GradleSubproject("installationBeacon", unitTests = false, functionalTests = false),
            GradleSubproject("internalAndroidPerformanceTesting", unitTests = false, functionalTests = false),
            GradleSubproject("performance", unitTests = false, functionalTests = false),
            GradleSubproject("runtimeApiInfo", unitTests = false, functionalTests = false),
            GradleSubproject("smokeTest", unitTests = false, functionalTests = false)
    )
}

data class GradleSubproject(val name: String, val unitTests: Boolean = true, val functionalTests: Boolean = true, val crossVersionTests: Boolean = false) {
    fun asDirectoryName(): String {
        return name.replace(Regex("([A-Z])"), { "-" + it.groups[1]!!.value.toLowerCase()})
    }
}

data class Stage(val description: String, val specificBuilds: List<SpecificBuild> = emptyList(), val performanceTests: List<PerformanceTestType> = emptyList(), val functionalTests: List<TestCoverage> = emptyList(), val trigger: Trigger = Trigger.never)

data class TestCoverage(val testType: TestType, val os: OS, val version: JvmVersion, val vendor: JvmVendor = JvmVendor.oracle) {
    fun asId(model : CIBuildModel): String {
        return "${model.projectPrefix}${testType.name.capitalize()}_${version.name.capitalize()}_${vendor.name.capitalize()}_${os.name.capitalize()}"
    }

    fun asConfigurationId(model : CIBuildModel, subproject: String = ""): String {
        val shortenedSubprojectName = subproject.replace("internal", "i").replace("Testing", "T")
        return asId(model) + "_" + if (!subproject.isEmpty()) shortenedSubprojectName else "0"
    }

    fun asName(): String {
        return "Test Coverage - ${testType.name.capitalize()} ${version.name.capitalize()} ${vendor.name.capitalize()} ${os.name.capitalize()}"
    }
}

enum class OS {
    linux, windows
}

enum class JvmVersion {
    java7, java8
}

enum class TestType(val unitTests: Boolean = true, val functionalTests: Boolean = true, val crossVersionTests: Boolean = false) {
    quick(true, true, false), platform(true, true, false),
    crossVersion(false, false, true), quickFeedbackCrossVersion(false, false, true),
    parallel(false, true, false), noDaemon(false, true, false), java9Smoke(false, true, false),
    soak(false, false, false),
    daemon(false, true, false) /* only for <4.1 Gradle versions*/
}

enum class JvmVendor {
    oracle, ibm
}

enum class PerformanceTestType(val taskId: String, val timeout : Int, val defaultBaselines: String = "", val extraParameters : String = "") {
    test("PerformanceTest", 420, "defaults"),
    experiment("PerformanceExperiment", 420, "defaults"),
    historical("FullPerformanceTest", 2280, "2.9,2.12,2.14.1,last", "--checks none");

    fun asId(model : CIBuildModel): String {
        return "${model.projectPrefix}Performance${name.capitalize()}Coordinator"
    }
}

enum class Trigger {
    never, eachCommit, daily, weekly
}

enum class SpecificBuild {
    SanityCheck, BuildDistributions, Gradleception, SmokeTests, ColonyCompatibility;

    fun create(model: CIBuildModel): BuildType {
        if (this == SanityCheck) {
            return SanityCheck(model)
        }
        if (this == BuildDistributions) {
            return BuildDistributions(model)
        }
        if (this == Gradleception) {
            return Gradleception(model)
        }
        if (this == SmokeTests) {
            return SmokeTests(model)
        }
        return ColonyCompatibility(model)
    }
}