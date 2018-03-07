import java.text.SimpleDateFormat

def verUtils() {
    return "varYMX"
}


def verYMX() {
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
        nowYYMMDD()
        def lv = this.latestVersion(gitUtils.lastedTags(), gitUtils.latestSuffixOfBranch("release"))
        if (lv[0].toInteger()==YY && lv[1].toInteger()==MM) {
            lv[2]+=1
            version=lv.join('.')
        } else {
            version="${YY}.${MM}.1"
        }
        if (currentBranchName ==~ /develop/) {
            echo "we are in develop"
            version += "-beta"
        } else if (currentBranchName ==~ /feature\/.*/) {
            echo "we are in feature/"
            version += "-alpha"
        }
        version+='-'+String.format("%02d", DD)+'-'+gitUtils.currentCommitShortHash()
        env.JOB_VERSION = version
        currentBuild.displayName = version
        return version
    } else { //now
        error "Error not valid GitFlow branch name: $currentBranchName"
    }
}

//def incVerYMX(){
//    nowYYMMDD()
//    incVersion="${YY}.${MM}"
//    return incVersion
//}

def nowYYMMDD() {
    now = new Date()
    YY = (new SimpleDateFormat("YY")).format(now).toInteger()
    MM = (new SimpleDateFormat("MM")).format(now).toInteger()
    DD = (new SimpleDateFormat("d")).format(now).toInteger()
    echo "today $YY-$MM-$DD"
    return [YY, MM, DD]
}

@NonCPS
def splitVersion(v) {
    m=v.split(/\./)
    if (m.size()==3){
        return [m[0].toInteger(),m[1].toInteger(),m[2].toInteger()]
    } else {
        return [0, 0, 0]
    }
}

def latestVersion(v1, v2) {
    def a1 = this.splitVersion(v1)
    def a2 = this.splitVersion(v2)
    if (a2==[0,0,0] || a1[0] > a2[0] ||
            (a1[0] == a2[0] && a1[1] > a2[1]) ||
            (a1[0] == a2[0] && a1[1] == a2[1] && a1[2] > a2[2])) {
        //println "a1 is greater $column"
        return a1
    } else if (a1==[0,0,0] || a1[0] < a2[0] ||
            (a1[0] == a2[0] && a1[1] < a2[1]) ||
            (a1[0] == a2[0] && a1[1] == a2[1] && a1[2] < a2[2])) {
        //println "a2 is greater"
        return a2
    } else return a1
}

