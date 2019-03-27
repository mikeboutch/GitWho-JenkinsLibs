#!groovy
def call(String script, returnStdout=false, returnStatus=false){
    if (!(new File("c:/temp/${env.GIT_COMMIT}").exists())){
        echo "c:/temp/${env.GIT_COMMIT} don't exists"
        if(!(new File( "c:/temp").exists())) {
            echo "\\temp don't exists"
            bat "mkdir c:\\temp"
        }
        bat "mklink /J c:\\temp\\%GIT_COMMIT% ."
    }
    script="""
           cd /d c:\\temp\\%GIT_COMMIT%
           ${script}
           """
    return bat(script:script,returnStdout:returnStdout,returnStatus:returnStatus)
}

