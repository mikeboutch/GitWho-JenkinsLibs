#!groovy
import java.text.SimpleDateFormat

def verYMX() {
    echo "verYMX() is deprecated: use verYMRH()"
    return this.verYMRH()
}


def setYYMMDD() {
    now = new Date()
    YY = (new SimpleDateFormat("YY")).format(now).toInteger()
    MM = (new SimpleDateFormat("MM")).format(now).toInteger()
    DD = (new SimpleDateFormat("d")).format(now).toInteger()
    echo "today $YY-$MM-$DD"
}

@NonCPS
def splitVersion(v,n=3) {
    def a=v.tokenize('.')*.toInteger()
    def al=a.size()
    if (al==0) al=1
    if (al<n){
         for (i in al..n)
             a[i-1]=0
    }
    return a[0..n-1]
}

def latestVersionArray(a1, a2) {
    if (a1.size()==a2.size && a1.size>0)
        for (i in 0..a1.size()-1) {
            if (a1[i] > a2[i]) {
                return a1
            } else if (a1[i] > a2[i]) {
                return a2
            }
        }  
    return a1
}
 def latestVersion(v1, v2, n=3) {
    def a1 = this.splitVersion(v1,n)
    def a2 = this.splitVersion(v2,n)
    
    return this.latestVersionArray(a1,a2).join('.')
 }
 def latestVersionString2Array(v1, v2, n=3){
    def a1 = this.splitVersion(v1,n)
    def a2 = this.splitVersion(v2,n)
    
    return this.latestVersionArray(a1,a2)
 }


def nextVersion(){
    this.setYYMMDD()
    def lv = this.latestVersionString2Array(gitUtils.latestSuffixOfBranch("release"),gitUtils.lastestTags())
    if (lv[0]==YY && lv[1]==MM) {
        lv[2]+=1
        return "${lv.join('.')}"
    } else {
        return "${YY}.${MM}.1"
    }
}

def verYMRH() {

    if (binding.hasVariable('version')) {
        return version
    }
    def currentBranchName = gitUtils.currentBranchName()
    version=""
    env.JOB_VERSION = ""
    if (currentBranchName == "master") {
        currentTags = gitUtils.currentTags()
        echo "we are in master $currentTags"
        if (currentTags?.trim()) {
            version = currentTags
            env.JOB_VERSION = version
            currentBuild.displayName = version
            return version
        } else {
            //error
            error "Error Master as no tag"
            return
        }
    }
    
    if ((currentBranchName =~ /^(?:develop|feature\/|bugfix\/|release\/|hotfix\/)/).find()) {
        if ((currentBranchName =~ /^(release|hotfix)\/.*/).find()) {
            version = currentBranchName.replaceFirst(/^(release|hotfix)\//, "")
        } else if ((currentBranchName =~ /^bugfix\/.*/).find()) {
            version=gitUtils.latestSuffixOfBranch("release")
        }else {
            version=this.nextVersion()
        }
        if ((currentBranchName =~ /^release\//).find()) {
            echo "we are in release/"
            version += "-rc"
        } else if ((currentBranchName =~ /^hotfix\//).find()) {
            echo "we are in hotfix/"
            version += "-beta"
        } else if ((currentBranchName =~ /^bugfix\//).find()) {
            
            version += "-bf"
        } else if ((currentBranchName =~ /^develop/).find()) {
            echo "we are in develop"
            version += "-beta"
        } else if ((currentBranchName =~ /^feature/).find()) {
            echo "we are in feature/"
            version += "-alpha"
        } else{
            error "Error not valid GitFlow branch name: $currentBranchName"
        }
        version+="-${env.BUILD_NUMBER}"
        version+="-"+gitUtils.currentCommitShortHash()
        env.JOB_VERSION = version
        currentBuild.displayName = version
        return version
    } else { 
        error "Error not valid GitFlow branch name: $currentBranchName"
    }
}