import java.text.SimpleDateFormat

def verYMX(){ 
    return "varYMX"
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
        //version="bob"
        gv=greaterVersion(gitUtils.lastedTags(),gitUtils.latestSuffixOfBranch("release"))
        version=gv
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
    }
}

def incVerYMX(){
    nowYYMM()
    incVersion="${YY}.${MM}"
    return incVersion
}

def nowYYMM(){
     now=new Date()
     YY=(new SimpleDateFormat("YY")).format(now).toInteger()
     MM=(new SimpleDateFormat("MM")).format(now).toInteger()
     return [YY,MM]
}

def splitVersion(v){
    if ((m=v=~/^(\d+)\.(\d+)(?:|\.(\d+)(?:|\.(\d+)|((?:\+|-).*)))$/)){
        return m[0][1..-1]
    } else {
        //error "No valid version number:$v"
        return [0,0,0,0,""]
    }
}


def greaterVersion(v1,v2, Integer column) {
    a1=splitVersion(v1)
    a2=splitVersion(v2)
    if (a1[0]>a2[0] ||
            (a1[0]==a2[0] && a1[1]>a2[1]) ||
            (a1[0]==a2[0] && a1[1]==a2[1] && a1[2]>a2[2]) ||
            (column==4 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]>a2[3]) ||
            (column==5 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]==a2[3] && a1[3]>a2[3])) {
        //println "a1 is greater $column"
        return v1
    } else if (a1[0]<a2[0] ||
            (a1[0]==a2[0] && a1[1]<a2[1]) ||
            (a1[0]==a2[0] && a1[1]==a2[1] && a1[2]<a2[2]) ||
            (column==4 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]<a2[3]) ||
            (column==5 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]==a2[3] && a1[3]<a2[3])){
        //println "a2 is greater"

        return v2
    } else return v1
}
def greaterVersion(v1,v2) {
    return greaterVersion(v1,v2,3)
}