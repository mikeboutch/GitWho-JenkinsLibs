def verYMX(){
    return "we are in verYMX"
}
def version=""

def version(){
    def currentBranchName=gitUtils.currentBranchName()
    //def currentTags=""
    //def suffixBranchName=""
    if (currentBranchName=="master") {
        currentTags=gitUtils.currentTags()
        echo "we are in master $currentTags"
        if (currentTags?.trim()){
            env.GIT_VERSION=version=currentTags
            return version
        } else {
            //error
            error ("Error Master as no tag")
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
            version=suffixBranchName+"-rc"+(countSince.toInteger()>0?countSince:"")
            return version
        } else {
            err
        }
    } else { //now
        return "batard."
    }
}