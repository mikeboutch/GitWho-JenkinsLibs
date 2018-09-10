#!groovy
def call(){
    bat script:"rm \\temp\\%GIT_COMMIT%" returnStatus:true
}

