

def verYMX(){
    version=""
    //return 
}
def version(){
    if (binding.hasVariable('version'){return version}
    def currentBranchName=gitUtils.currentBranchName()
    //def currentTags=""
    //def suffixBranchName=""
    if (currentBranchName=="master") {
        currentTags=gitUtils.currentTags()
        echo "we are in master $currentTags"
        if (currentTags?.trim()){
            env.JOB_VERSION=version=currentTags
            return version
        } else {
            //error
            error "Error Master as no tag"
            return
        }
    } 
    if ( currentBranchName==~/(?:release|hotfix)\/.*/ ){
        suffixBranchName=currentBranchName.replaceFirst(/^(?:release|hotfix)\//,"")
        echo "we are in release/$suffixBranchName"
        if (suffixBranchName?.trim()){
            countSince=(currentBranchName==~/release.*/?
                gitUtils.commitsCountSinceBranch("develop"):
                gitUtils.commitsCountSinceBranch("master"))
            env.JOB_VERSION=version=suffixBranchName+"-rc"+(countSince.toInteger()>0?countSince:"")
            return version
        } else {
            error ("Error with branch $currentBranchName")
            return
        }
    } 
    if ( currentBranchName==~/(?:develop|feature\/.*)/ ){
        env.JOB_VERSION=version="$currentBranchName" 
        return version
    } else { //now
        error "Error not valid GitFlow branch name: $currentBranchName"
        return 
    }
}