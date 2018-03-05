

def verYMX(){

    return "varYMX"
}

def version(){
    if (binding.hasVariable('version')){return version}
    def currentBranchName=gitUtils.currentBranchName()
    //def currentTags=""
    //def suffixBranchName=""
    if (currentBranchName=="master") {
        currentTags=gitUtils.currentTags()
        echo "we are in master $currentTags"
        if (currentTags?.trim()){   
            return currentBuild.displayName= env.JOB_VERSION=version=currentTags
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
            return currentBuild.displayName= env.JOB_VERSION=version=suffixBranchName+"-rc"+(countSince.toInteger()>0?countSince:"")
        } else {
            error ("Error with branch $currentBranchName")
            return
        }
    } 
    if ( currentBranchName==~/(?:develop|feature\/.*)/ ){
        return currentBuild.displayName= env.JOB_VERSION=version="$currentBranchName" 
    } else { //now
        error "Error not valid GitFlow branch name: $currentBranchName"
        return 
    }
}