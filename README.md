# **GitWho automation: Jenkins Shared Libraries.**

**Jenkinsfiles Example**
```
pipeline {
    environment {
        //GITWHO_DISABLE_AMB_HF = "1" //Auto Merge Back disabled for hotfix/
        //GITWHO_DISABLE_AMB_REL = "1" //Auto Merge Back disabled for release/
        
        // Please try to avoid call of function like "JOB_VERSION = setVerYMX()"
        // Because if error happen, is difficult see it in Jenkins UI, since environment is not a step...
               
    } 
    agent any
    stages {
        stage ('Init'){
            steps {
                echo 'init'
                setVerYMRH()
                echo "VER:${env.JOB_VERSION}"
                sendNotifications()
            }
        }
        stage("build") {
            steps{
                bat("set") // put your build command              
            }
        }
        // Others stages
    }
    post {
        success { 
            gitWhoPostBuildCheck() 
        }
        always{
            sendNotifications currentBuild.result
        }
        cleanup {
            catchError(buildResult: currentBuild.result, message: 'Error on cleanWS', stageResult: 'UNSTABLE') {
                bat label: 'Kill process locking out the workspace', returnStatus: true, 
                    script: 'start /Wait /B LockHunter.exe -k %WORKSPACE% -sm -x '
                sleep 3
                cleanWs notFailBuild: true
            }
        }
    } 
}

```

# **Steps:**
- ## **setVerYMRH()**
   Set the current version according to [Year.Month.Release\[.Hotfix\] versioning model]() of the current commit.

   Set global variables:
   - **version** : the calculated Year.Month.Release[.Hotfix] version for the current commit

   Set environment variables:
   - **JOB\_VERSION** : the calculated Year.Month.Release[.Hotfix] version for the current commit

   This step call the **gitWhoPreBuildCheck()** before it's began to calculated the current version.

   Even this step is called multiple times, it's will calculate the current version only once.
- ## **gitWhoPreBuildCheck()**
   Do multiple checks and fixes against the git repository to assure the readiness in regard of the [GitWho model.]()

   Check and fixes for ***master*** branch:
   - Verify the source of the merge, only ***release/*** and ***hotfix/*** are accepted.
   - Tag the merge
   - Delete the merged ***release/*** or ***hotfix/*** branch

   Check and fixes for **develop** branch:
   - Delete merged **feature/** branch.

   Check and fixes for **release/** branches:
   - Delete merged **bugfix/** branch.
   - Verify the source of the merge, only ***bugfix/*** and ***hotfix/*** are accepted.

  Check and fixes for ***hotfix/*** branches.
  - Verify the it's a direct commit. Any merge are prohibited

  Check and fixes for ***bugfix/*** branches.
   - Verify the it's a direct commit.

  Check and fixes for ***feature/*** branches:
  - Only ***develop/*** branches can be merged into a feature/ branch.

  Even this step is called multiple times, it's will be executes only once.

- ## **gitWhoPostBuildCheck()**
   Do all the automatable "Updating" process of the [ addimage)

   For  ***release/*** branches:
   - Merge back to develop branch.

   For ***hotfix/*** branches:
   - Merge back to  the open ***release/*** branch or to the  ***develop*** branch.

   If a merge conflict happen it will simply not do the merge.


- ## **sendNotifications(buildStatus = 'STARTED', warningMessages='')**

   Send a card to a Microsoft Team channel. With links to the build both on the classic and the Blue Ocean view

   BuildStatus could contain any text, But it's preferable to use Jenkins build status (STARTED, UNSTABLE, FAILURE, SUCCESS) or INFO or WARNING, any other status will be considerate has a WARNING.

   If warningMessage is specified, it will be displayed has a  header at the beginning of the card.

  Configuration: Install the Office 365 connector plugs and set the webHookUrl in the  MS\_TEAM\_WEBHOOK\_URL environment variable.

  ```
      post {
        success { 
            gitWhoPostBuildCheck() 
        }
        always{
            sendNotifications currentBuild.result
        }
        cleanup {
            catchError(buildResult: currentBuild.result, message: 'Error on cleanWS', stageResult: 'UNSTABLE') {
                bat label: 'Kill process locking out the workspace', returnStatus: true, 
                    script: 'start /Wait /B LockHunter.exe -k %WORKSPACE% -sm -x '
                sleep 3
                cleanWs notFailBuild: true
            }
        }
    }
  ```

