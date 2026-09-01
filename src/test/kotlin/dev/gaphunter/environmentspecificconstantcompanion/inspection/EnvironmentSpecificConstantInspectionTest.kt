package dev.gaphunter.environmentspecificconstantcompanion.inspection

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class EnvironmentSpecificConstantInspectionTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(EnvironmentSpecificConstantInspection::class.java)
        myFixture.addFileToProject(
            "application.properties",
            """
            api.endpoint=https://api.example.com/v2
            some.ratio=12:34
            some.port=8080
            """.trimIndent(),
        )
        myFixture.addFileToProject(
            "application.yml",
            """
            db:
              host: db.internal:5432
            """.trimIndent(),
        )
    }

    fun `test a full URL literal that also appears in a properties file is flagged`() {
        myFixture.configureByText(
            "Service.java",
            """
            class Service {
                String url = "https://api.example.com/v2";
            }
            """.trimIndent(),
        )
        val highlights = myFixture.doHighlighting()
        assertTrue(highlights.any { it.description?.contains("also appears as a config value") == true })
    }

    fun `test a host colon port literal that also appears in a yml file is flagged`() {
        myFixture.configureByText(
            "Service2.java",
            """
            class Service2 {
                String hostPort = "db.internal:5432";
            }
            """.trimIndent(),
        )
        val highlights = myFixture.doHighlighting()
        assertTrue(highlights.any { it.description?.contains("also appears as a config value") == true })
    }

    fun `test an all-digit host colon port shape is never flagged even if the exact text is a config value`() {
        myFixture.configureByText(
            "Service3.java",
            """
            class Service3 {
                String ratio = "12:34";
            }
            """.trimIndent(),
        )
        val highlights = myFixture.doHighlighting()
        assertTrue(highlights.none { it.description?.contains("also appears as a config value") == true })
    }

    fun `test a URL-shaped literal not present in any config file is not flagged`() {
        myFixture.configureByText(
            "Service4.java",
            """
            class Service4 {
                String url = "https://not-in-config.example.com";
            }
            """.trimIndent(),
        )
        val highlights = myFixture.doHighlighting()
        assertTrue(highlights.none { it.description?.contains("also appears as a config value") == true })
    }

    fun `test a bare port literal is never flagged regardless of config content`() {
        myFixture.configureByText(
            "Service5.java",
            """
            class Service5 {
                String port = "8080";
            }
            """.trimIndent(),
        )
        val highlights = myFixture.doHighlighting()
        assertTrue(highlights.none { it.description?.contains("also appears as a config value") == true })
    }
}
