/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests.tooling

import org.gradle.integtests.tooling.r18.NullAction
import org.gradle.integtests.tooling.fixture.TargetGradleVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.integtests.tooling.fixture.ToolingApiVersion
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.eclipse.EclipseProject
import org.gradle.util.GradleVersion

class ToolingApiUnsupportedVersionCrossVersionSpec extends ToolingApiSpecification {
    def setup() {
        file("build.gradle") << """
task noop {
    doLast {
        println "noop"
    }
}
"""
    }

    @ToolingApiVersion("<1.2")
    @TargetGradleVersion(">=2.0")
    def "provider rejects build request from a tooling API client older than 1.2"() {
        when:
        withConnection { ProjectConnection connection ->
            def build = connection.newBuild()
            build.forTasks("noop")
            build.run()
        }

        then:
        caughtGradleConnectionException = thrown()
        caughtGradleConnectionException.cause.message.contains('Support for clients using a tooling API version older than 2.0 was removed in Gradle 3.0. You should upgrade your tooling API client to version 2.0 or later.')
    }

    @ToolingApiVersion(">=1.2 <2.0")
    @TargetGradleVersion(">=3.0")
    def "provider rejects build request from a tooling API client older than 2.0"() {
        when:
        withConnection { ProjectConnection connection ->
            def build = connection.newBuild()
            build.forTasks("noop")
            build.run()
        }

        then:
        caughtGradleConnectionException = thrown()
        caughtGradleConnectionException.cause.message.contains("Support for clients using a tooling API version older than 2.0 was removed in Gradle 3.0. You are currently using tooling API version ${GradleVersion.current().version}. You should upgrade your tooling API client to version 2.0 or later.")
    }

    @ToolingApiVersion("<1.2")
    @TargetGradleVersion(">=2.0")
    def "provider rejects model request from a tooling API client older than 1.2"() {
        when:
        withConnection { ProjectConnection connection ->
            def modelBuilder = connection.model(EclipseProject)
            modelBuilder.get()
        }

        then:
        caughtGradleConnectionException = thrown()
        caughtGradleConnectionException.cause.message.contains('Support for clients using a tooling API version older than 2.0 was removed in Gradle 3.0. You should upgrade your tooling API client to version 2.0 or later.')
    }

    @ToolingApiVersion(">=1.2 <2.0")
    @TargetGradleVersion(">=3.0")
    def "provider rejects model request from a tooling API client older than 2.0"() {
        when:
        withConnection { ProjectConnection connection ->
            def modelBuilder = connection.model(EclipseProject)
            modelBuilder.get()
        }

        then:
        caughtGradleConnectionException = thrown()
        caughtGradleConnectionException.cause.message.contains("Support for clients using a tooling API version older than 2.0 was removed in Gradle 3.0. You are currently using tooling API version ${GradleVersion.current().version}. You should upgrade your tooling API client to version 2.0 or later.")
    }

    @ToolingApiVersion(">=1.8 <2.0")
    @TargetGradleVersion(">=2.14")
    def "provider rejects build action request from a tooling API client older than 2.0"() {
        when:
        withConnection { ProjectConnection connection ->
            def build = connection.action(new NullAction())
            build.run()
        }

        then:
        caughtGradleConnectionException = thrown()
        caughtGradleConnectionException.cause.message.contains("Support for clients using a tooling API version older than 2.0 was removed in Gradle 3.0. You are currently using tooling API version ${GradleVersion.current().version}. You should upgrade your tooling API client to version 2.0 or later.")
    }
}
