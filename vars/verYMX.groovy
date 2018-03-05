import java.text.SimpleDateFormat

def verYMX(){ 
    return "varYMX"
}
// def getlatestSemver(a1,a2){
//     return a1
// }
// def extractSemver(s){

// }
def getNowYYMM(){
    now=new Date()
    return [ (new SimpleDateFormat("YY")).format(now).toInteger(), 
        (new SimpleDateFormat("MM")).format(now).toInteger()]​
}

// get a incrmented version
def getCurrentPreVersionYMX(){
    //echo "we are in getCurrentPreVersion"
    return "bob"
}

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
            countSince=(currentBranchName==~/release\/.*/?
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
        version="bob"
        //echo getCurrentPreVersionYMX()
        if (currentBranchName==~/develop/) {
            echo "we are in develop"
        } else if (currentBranchName==~/feature\/.*/) {
            echo "we are in feature/"
        }
        env.JOB_VERSION= version
        currentBuild.displayName= version
        return version 
    } else { //now
        error "Error not valid GitFlow branch name: $currentBranchName"
        return 
    }
}