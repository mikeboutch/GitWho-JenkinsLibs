#!groovy
package gitUtils

def currentBranchName(){
    return env.BRANCH_NAME
}

def currentCommitHash(){
    return env.GIT_COMMIT
}

def currentCommitShortHash(){
    return env.GIT_COMMIT[0..6]
}

def lastedTags() {
    return sh(returnStdout: true, script: 'git fetch --tags &>/dev/null;git describe --abbrev=0 --tags 2>/dev/null || true').trim()
}

def currentTags() {
    return sh(returnStdout: true, script: 'git fetch --tags &>/dev/null;git name-rev --tags --name-only HEAD').trim().replaceFirst(/\^0$/,"").
        replaceFirst(/^undefined$/,"")
}

def commitsCountSinceBranch(sinceBranch) {
    try {
        return sh(returnStdout: true, script: "git rev-list --no-merges --count HEAD ^origin/${sinceBranch}").trim()
    } catch(Exception e) {
        echo" Warnings: commitsCountSinceBranch return error"
        return "0"
    }
}

def latestSuffixOfBranch(prefixBranch){
    prefixBranch=prefixBranch.replaceFirst(/\/$/,"")
    return sh(returnStdout: true, script: "git branch -r --list \"origin/${prefixBranch}/*\" --sort=-committerdate |head -1").trim().
        replaceFirst("origin/${prefixBranch}/","")
}

def getEmailLastCommiter(int i=10) {
    return sh(returnStdout: true, script: "git log -$i --pretty=%ae|tr '[:upper:]' '[:lower:]'|sort|uniq").readLines()
}



