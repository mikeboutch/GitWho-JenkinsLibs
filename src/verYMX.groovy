def verYMX(){
    return "we are in verYMX"
}
def version(){
    def currentBranchName=gitUtils.currentBranchName()
    //def currentTags=""
    //def suffixBranchName=""
    if (currentBranchName=="master") {
        currentTags=gitUtils.currentTags()
        echo "we are in master $currentTags"
        if (currentTags?.trim()){
            return currentTags
        } else {
            //error
            return "error Master"
        }
    } 
    if ( currentBranchName==~/(?:release|hotfix)\/.*/ ){
        suffixBranchName=currentBranchName.replaceFirst(/^(?:release|hotfix)\//,"")
        echo "we are in release/$suffixBranchName"
        if (suffixBranchName?.trim()){
            countSince=(currentBranchName==~/release.*/?
                gitUtils.commitsCountSinceBranch("develop"):
                gitUtils.commitsCountSinceBranch("master"))
            return suffixBranchName+"-rc"+(countSince>0?countsince:"")
        } else {
            //error
            return "Error Master"
        }
    } else { //now
        return "batard."
    }
}