#!groovy

def currentBranchName(){
    return env.BRANCH_NAME.trim()
}

def currentCommitHash(){
    return env.GIT_COMMIT.trim()
}

def currentCommitShortHash(){
    return env.GIT_COMMIT[0..6]
}
def listTags() {
    // git ls-remote --tags --sort=-v:refname origin| sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\/tags\/([^\^]+)(.*)/\1/g'| grep -E '^v?[0-9]+(\.[0-9]+)+$'
    return  sh(returnStdout: true, script:"""
        git ls-remote --tags --sort=-v:refname origin|
        sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/([^\\^]+)(.*)/\\1/g'| uniq|
        sed -E -n '/^[0-9]+(\\.[0-9]+)+\$/p'
        """ ).split('\n')
    // not safe on dirty tags
    return  sh(returnStdout: true, script:"git ls-remote --tags --sort=-v:refname origin| sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/([^\\^]+)(.*)/\\1/g'|uniq").split('\n') 
}
def lastestTags() {
    return listTags()[0].trim()
}

def currentTags() {
    return sh(returnStdout: true, script: "git ls-remote --tags origin |grep $env.GIT_COMMIT| sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/([^\\^]+)(.*)/\\1/g'").trim()
}

def commitsCountSinceBranch(sinceBranch) {
    try {
        sh ("git fetch origin ${sinceBranch}:${sinceBranch}")    
        return sh(returnStdout: true, script: "git rev-list --count  --first-parent ...${sinceBranch}").trim()
    } catch(Exception e) {
        echo" Warnings: commitsCountSinceBranch return error"
        return "0"
    }
}
def listSuffixOfBranch(prefixBranch){
    prefixBranch=prefixBranch.replaceFirst(/\/$/,"")
    echo prefixBranch
    //return sh(returnStdout: true, script: "git branch -r --list \"origin/${prefixBranch}/*\" --sort=-v:refname |head -1").trim().
    return sh(returnStdout: true, script: "git ls-remote --sort=-v:refname origin '$prefixBranch/*'|sed -E 's/^.*$prefixBranch\\/(.*)\$/\\1/g'|uniq").split('\n') 
}

def latestSuffixOfBranch(prefixBranch){
    return listSuffixOfBranch(prefixBranch)[0].trim()   
}

def getEmailLastCommiter(int i=10) {
    return sh(returnStdout: true, script: "git log -$i --pretty=%ae|tr '[:upper:]' '[:lower:]'|sort|uniq").readLines()
}



