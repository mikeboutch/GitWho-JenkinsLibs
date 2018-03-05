
def verYMX(){ return "varYMX"; }

def version(){
    if (binding.hasVariable('version')){return version}
    def currentBranchName=gitUtils.currentBranchName()
    env.JOB_VERSION=""
    if (currentBranchName=="master") {
        currentTags=gitUtils.currentTags()
        echo "we are in master $currentTags"
        if (currentTags?.trim()){ 
            version=currentTags
            env.JOB_VERSION=version
            currentBuild.displayName=version
            return   version
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
            version=suffixBranchName+"-rc"+(countSince.toInteger()>0?countSince:"")
            env.JOB_VERSION=version
            currentBuild.displayName=version
            return version
        } else {
            error ("Error with branch $currentBranchName")
            return
        }
    } 
    if ( currentBranchName==~/(?:develop|feature\/.*)/ ){
        version="$currentBranchName"
        env.JOB_VERSION= version
        currentBuild.displayName= version
        return version 
    } else { //now
        error "Error not valid GitFlow branch name: $currentBranchName"
        return 
    }
}