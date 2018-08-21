#!groovy
def call(){
    bat "rm \\temp\\%GIT_COMMIT%"
}

