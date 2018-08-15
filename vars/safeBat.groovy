#!groovy
def call(String script, returnStdout=false, returnStatus=false){
    if (!(new File("\temp\${env.GIT_COMMIT}").exists())){
        if(!(new File( "\temp"))) bat "mkdir \\temp"
        bat "mklink /J \\temp\\%GIT_COMMIT% ."
    }
    return bat(script:script,returnStdout:returnStdout,returnStatus:returnStatus)
}

