import java.text.SimpleDateFormat

def verYMX() {
    return "varYMX"
}


def version() {
    if (binding.hasVariable('version')) {
        return version
    }
    def currentBranchName = gitUtils.currentBranchName()
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
    if (currentBranchName ==~ /(?:release|hotfix)\/.*/) {
        suffixBranchName = currentBranchName.replaceFirst(/^(?:release|hotfix)\//, "")
        echo "we are in release/$suffixBranchName"
        if (suffixBranchName?.trim()) {
            countSince = (currentBranchName ==~ /release\/.*/ ?
                    gitUtils.commitsCountSinceBranch("develop") :
                    gitUtils.commitsCountSinceBranch("master"))
            version = suffixBranchName + "-rc" + (countSince.toInteger() > 0 ? countSince : "")
            env.JOB_VERSION = version
            currentBuild.displayName = version
            return version
        } else {
            error("Error with branch $currentBranchName")
            return
        }
    }
    if (currentBranchName ==~ /(?:develop|feature\/.*)/) {
        //version="bob"
        gv = this.greaterVersion(gitUtils.lastedTags(), gitUtils.latestSuffixOfBranch("release"))
        version = gv
        if (currentBranchName ==~ /develop/) {
            echo "we are in develop"
            version += "-beta"
        } else if (currentBranchName ==~ /feature\/.*/) {
            echo "we are in feature/"
            version += "-alpha"
        }
        env.JOB_VERSION = version
        currentBuild.displayName = version
        return version
    } else { //now
        error "Error not valid GitFlow branch name: $currentBranchName"
    }
}

//def incVerYMX(){
//    nowYYMM()
//    incVersion="${YY}.${MM}"
//    return incVersion
//}

def nowYYMM() {
    now = new Date()
    YY = (new SimpleDateFormat("YY")).format(now).toInteger()
    MM = (new SimpleDateFormat("MM")).format(now).toInteger()
    return [YY, MM]
}

@NonCPS
def splitVersion(v) {
    m=v.split(/\./)
    if (m.size()==3){
        return m
    } else {
        return [0, 0, 0]
    }
}

def greaterVersion(v1, v2) {
    def a1 = this.splitVersion(v1)
    def a2 = this.splitVersion(v2)
    if (a2==[0,0,0] || a1[0] > a2[0] ||
            (a1[0] == a2[0] && a1[1] > a2[1]) ||
            (a1[0] == a2[0] && a1[1] == a2[1] && a1[2] > a2[2])) {
        //println "a1 is greater $column"
        return v1
    } else if (a1==[0,0,0] || a1[0] < a2[0] ||
            (a1[0] == a2[0] && a1[1] < a2[1]) ||
            (a1[0] == a2[0] && a1[1] == a2[1] && a1[2] < a2[2])) {
        //println "a2 is greater"

        return v2
    } else return v1
}

