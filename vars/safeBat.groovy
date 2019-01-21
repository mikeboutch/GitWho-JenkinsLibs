#!groovy
def call(String script, returnStdout=false, returnStatus=false){
    // if (!(new File("/temp/${env.GIT_COMMIT}").exists())){
    //     echo "/temp/${env.GIT_COMMIT} don't exists"
    //     if(!(new File( "/temp").exists())) {
    //         echo "\\temp don't exists"
    //         bat "mkdir \\temp"
    //     }
    //     bat "mklink /J \\temp\\%GIT_COMMIT% ."
    // }
    echo script
    def pwdDOS=sh(script:"cygpath -d -a .",returnStdout:true).trim()
    echo "#####################$pwdDOS##############################"
    script="""
           cd "${pwdDOS}"
           ${script}
           """
    return bat(script:script,returnStdout:returnStdout,returnStatus:returnStatus)
}


//
// cygpath -a -d .

// String path = "/root/file"
// File file = new File(path)

// println("Name: " + file.name)
// println("Parent: " + file.parent)

