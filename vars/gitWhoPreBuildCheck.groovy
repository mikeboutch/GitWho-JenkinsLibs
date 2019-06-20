#!groovy
def call(){
    return gitUtils.gitWhoPreBuildCheck()
}

