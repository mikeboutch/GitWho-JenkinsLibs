def git_utils(){
    return "I am in git_utils"
}
//echo "not in a function" // DONT DO THAT


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

def commitsCountSinceBranch(sinceBranchName) {
    return sh(returnStdout: true, script: "git rev-list --no-merges --count HEAD ^origin/${sinceBranchName}").trim()
}

def showSuffixOfBrachName(prefixBranchName){
    prefixBranchName=prefixBranchName.replaceFirst(/\/$/,"")
    return sh(returnStdout: true, script: "git branch -r --list \"origin/${prefixBranchName}/*\" --sort=-committerdate |head -1").trim().
        replaceFirst("origin/${prefixBranchName}/","")
}



