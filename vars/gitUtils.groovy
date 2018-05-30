def gitUtils(){
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
    return sh(returnStdout: true, script: 'git name-rev --tags --name-only HEAD').trim().replaceFirst(/\^0$/,"").
        replaceFirst(/^undefined$/,"")
}

def commitsCountSinceBranch(sinceBranch) {
    return sh(returnStdout: true, script: "git rev-list --no-merges --count HEAD ^origin/${sinceBranch}").trim()
}

def latestSuffixOfBranch(prefixBranch){
    prefixBranch=prefixBranch.replaceFirst(/\/$/,"")
    return sh(returnStdout: true, script: "git branch -r --list \"origin/${prefixBranch}/*\" --sort=-committerdate |head -1").trim().
        replaceFirst("origin/${prefixBranch}/","")
}



