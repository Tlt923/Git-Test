import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.python
import jetbrains.buildServer.configs.kotlin.buildSteps.script

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {

    buildType(BuildTest)
    buildType(Test2)

    params {
        param("User", "Jeff")
        password("Password", "credentialsJSON:1ce05aa2-eecd-43dc-87cc-9ae8a6db6bdb", label = "1", readOnly = true)
    }
}

object BuildTest : BuildType({
    name = "Build_Test"

    params {
        param("test_number", "0")
    }

    steps {
        python {
            name = "Python_Test"
            workingDir = """D:\Study\Version Control Tools\Git\test"""
            environment = virtualenv {
            }
            command = file {
                filename = "python_test.py"
                scriptArguments = "--source_p4_user %User% --source_p4_user2 %Password%"
            }
        }
        script {
            scriptContent = "echo %test_number%"
        }
        script {
            scriptContent = "teamcity.exe setParameter /name:test_number /value:1 /build:Build_Test"
        }
        script {
            scriptContent = "echo %test_number%"
        }
    }

    requirements {
        matches("teamcity.agent.jvm.os.family", "Windows")
        equals("teamcity.agent.name", "Windows_test")
    }
})

object Test2 : BuildType({
    name = "test2"
})