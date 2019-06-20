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
    //return  sh(returnStdout: true, script:"git ls-remote --tags --sort=-v:refname origin| sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/([^\\^]+)(.*)/\\1/g'|uniq").split('\n') 
}
def latestTags() {
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
    //echo prefixBranch
    //return sh(returnStdout: true, script: "git branch -r --list \"origin/${prefixBranch}/*\" --sort=-v:refname |head -1").trim().
    return sh(returnStdout: true, script: "git ls-remote --sort=-v:refname origin '$prefixBranch/*'|sed -E 's/^.*$prefixBranch\\/(.*)\$/\\1/g'|uniq").split('\n') 
}

def latestSuffixOfBranch(prefixBranch){
    return listSuffixOfBranch(prefixBranch)[0].trim()   
}

def getEmailLastCommitter(int i=10) {
    return sh(returnStdout: true, script: "git log -$i --pretty=%ae|tr '[:upper:]' '[:lower:]'|sort|uniq").readLines()
}
String parentMergeBranchName(){
    String commitMessage=sh(returnStdout: true, script: 'git log --format=%B  -n 1');
    String branchName=''
    if ((commitMessage =~ /^Merge branch '/).find()) {
        echo "find a merge"
        branchName=(commitMessage =~ /^Merge branch '([^']*)/)[0][1]
    } else 
    if ((commitMessage =~ /^Merge pull request #/).find()) {
        echo "find a pull request"
        branchName=(commitMessage =~ / from ([^ ]*)/)[0][1]    
    }
    echo "parentMergeBranchName=$branchName"
    return branchName
}
String parentMergeBranchHash(){
    return sh(returnStdout: true, script: "git log --pretty=%P -n 1 |awk '{print \$2}'").trim()
}
String branchHash(String branchName){
    return sh(returnStdout: true, script: "git ls-remote -q origin $branchName |awk '{print \$1}'").trim()
}


def deleteBranchNameHash(String branchName,String branchHash){
    echo "verify:$branchName:$branchHash"
    String branchHashFromName=this.branchHash("$branchName")
    echo "verify:$branchName:$branchHashFromName:$branchHash"
    if ( branchHash !='' && branchHashFromName != '' &&
        branchHash == branchHashFromName){
            echo "branch $branchName will be deleted"
            sh("git push origin :$branchName")
        } 
}


def gitWhoPreBuildCheck(){
    if (binding.hasVariable('gitWhoPreBuildCheck')) {
        return gitWhoPreBuildCheck
    }
    String currentBranchName=currentBranchName()
    String parentMergeBranchName = parentMergeBranchName()
    echo "currentBranchName=$currentBranchName"
    if (currentBranchName == 'master') {
        currentTags = currentTags()      
        echo "currentTags: $currentTags"
        if (!currentTags ){
            targetVersion=(parentMergeBranchName=~/^(?:release|hotfix)\/(.*)$/)[0][1]
            echo "targetVersion=$targetVersion"
            sh("git tag '$targetVersion'")
            sh("git push origin $targetVersion")
        }
        if ((parentMergeBranchName=~/^(?:release|hotfix)\/.*$/).find()){
            //echo "merged from $parentMergeBranchName[${parentMergeBranchHash()}] into master"
            deleteBranchNameHash(parentMergeBranchName,parentMergeBranchHash())
        }
    } else if (currentBranchName == 'develop') {
        if (parentMergeBranchName==~/^feature\/(.*)$/){
            //echo "merged from $parentMergeBranchName[${parentMergeBranchHash()}] into develop"
            deleteBranchNameHash(parentMergeBranchName,parentMergeBranchHash())
        }
    }
    gitWhoPreBuildCheck=true
}



