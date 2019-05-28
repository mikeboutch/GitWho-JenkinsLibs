#!groovy
def call(String script, returnStdout=false, returnStatus=false){
    if (!(new File("c:/temp/${env.GIT_COMMIT}").exists())){
        echo "c:/temp/${env.GIT_COMMIT} don't exists"
        if(!(new File( "/temp").exists())) {
            echo "c:\\temp don't exists"
            bat "mkdir \\temp"
        }
        bat "mklink /J c:\\temp\\%GIT_COMMIT% ."
    }
    script="""
           cd c:\\temp\\${env.GIT_COMMIT}
           ${script}
           """
    return powershell(script:script,returnStdout:returnStdout,returnStatus:returnStatus)
}

