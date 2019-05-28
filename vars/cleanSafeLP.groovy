#!groovy
def call(){
    bat(script:"rm.exe c:\\temp\\%GIT_COMMIT%", returnStatus:true)
}

