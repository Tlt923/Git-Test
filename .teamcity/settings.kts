import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.python
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.vcs.PerforceVcsRoot

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

    vcsRoot(TestP4)

    buildType(BuildTest)
    buildType(Test2)

    params {
        password("Password", "credentialsJSON:1ce05aa2-eecd-43dc-87cc-9ae8a6db6bdb", label = "1", readOnly = true)
        param("User", "Jeff")
    }
}

object BuildTest : BuildType({
    name = "Build_Test"

    params {
        param("test_number", "0")
    }

    vcs {
        root(TestP4)

        checkoutMode = CheckoutMode.ON_AGENT
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
            scriptContent = "echo ##teamcity[setParameter name='test_number' value='1']"
        }
        script {
            scriptContent = "setx %test_number% 2"
        }
        script {
            scriptContent = "echo %test_number%"
        }
    }

    requirements {
        equals("teamcity.agent.name", "Windows_test")
        matches("teamcity.agent.jvm.os.family", "Windows")
    }
})

object Test2 : BuildType({
    name = "test2"
})

object TestP4 : PerforceVcsRoot({
    name = "test_p4"
    port = "10.215.128.118:1666"
    mode = stream {
        streamName = "//NetEaseQA/Dev"
    }
    userName = "Admin"
    password = "credentialsJSON:893045cf-85fd-4f15-a07e-35163c46e5d4"
    workspaceOptions = """
        Options:        noallwrite clobber nocompress unlocked nomodtime rmdir
        Host:           %teamcity.agent.hostname%
        SubmitOptions:  revertunchanged
        LineEnd:        local
    """.trimIndent()
    nonStreamWorkspace = true
})
