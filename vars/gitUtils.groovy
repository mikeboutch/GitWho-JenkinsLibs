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
    return sh(label:"List Tags",returnStdout: true, script: """
        git ls-remote --tags --sort=-v:refname origin |
        sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/([^\\^]+)(.*)/\\1/g' | uniq |
        sed -E -n '/^[0-9]+(\\.[0-9]+)+\$/p'
        """ ).split('\n')
    // not safe on dirty tags
    //return  sh(returnStdout: true, script:"git ls-remote --tags --sort=-v:refname origin| sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/([^\\^]+)(.*)/\\1/g'|uniq").split('\n') 
}
def latestTags() {
    // return sh(returnStdout: true, script: 'git fetch --tags &>/dev/null;git describe --abbrev=0 --tags 2>/dev/null || true').trim()
    return listTags()[0].trim()
}

def currentTags() {
    return sh(label:"Current Tags", returnStdout: true,
        script: "git ls-remote --tags origin |grep $env.GIT_COMMIT| sed -E 's/^[[:xdigit:]]+[[:space:]]+refs\\/tags\\/([^\\^]+)(.*)/\\1/g'"
        ).trim()
}
def fetchRemoteBranch(String branchName){
    sh(label:"Fetch $branchName",
        script:"git fetch --no-tags --progress origin +refs/heads/$branchName:refs/remotes/origin/$branchName")
    try {
        sh(label:"Add remote branch $branchName",
            script:"git remote set-branches --add origin $branchName")
    } catch (Exception e) {}
}

def commitsCountSinceBranch(String sinceBranch) {

    fetchRemoteBranch(sinceBranch)
    try {
        return sh(label:"Counting commit since $sinceBranch",returnStdout: true, 
            script: "git rev-list --no-merges --count HEAD ^origin/${sinceBranch}").trim().toInteger()
        //echo "$commitCount commits since $sinceBranch"
        //return sh(returnStdout: true, script: "git rev-list --count  --first-parent ...${sinceBranch}").trim()
    } catch (Exception e) {
        echo "Warnings: commitsCountSinceBranch return error!"
        return -1
    }
}
def listSuffixOfBranch(String prefixBranch){
    prefixBranch = prefixBranch.replaceFirst(/\/$/, "")
    //echo prefixBranch
    //return sh(returnStdout: true, script: "git branch -r --list \"origin/${prefixBranch}/*\" --sort=-v:refname |head -1").trim().
    return sh(label:"List suffix of $prefixBranch branches",returnStdout: true, 
        script: "git ls-remote --sort=-v:refname origin '$prefixBranch/*'|sed -E 's/^.*$prefixBranch\\/(.*)\$/\\1/g'|uniq"
        ).split('\n')
}

def latestSuffixOfBranch(String prefixBranch){
    try{
        return listSuffixOfBranch(prefixBranch)[0].trim()
    } catch(Exception e){
        return ''
    }
}

def getEmailLastCommitter(int i = 10) {
    return sh(returnStdout: true, script: "git log -$i --pretty=%ae|tr '[:upper:]' '[:lower:]'|sort|uniq").readLines()
}

String parentMergeBranchName(String toBranchName=''){
    String commitMessage = sh(returnStdout: true, script: 'git log --format=%B  -n 1');
    String branchName = ''
    try {
    if ((commitMessage =~ /^Merge branch '/).find()) {
        if (toBranchName=='master' || toBranchName=='') {
            branchName = (commitMessage =~ /^Merge branch '([^']*)/)[0][1]
        } else {
             branchName = (commitMessage =~ /^Merge branch '([^']*)' into ${toBranchName}/)[0][1]
        }
    } else
        if ((commitMessage =~ /^Merge pull request #/).find()) {
            if (toBranchName=='')
                branchName = (commitMessage =~ / from ([^ ]*)/)[0][1]
            else
            branchName = (commitMessage =~ / from ([^ ]*) to ${toBranchName}/)[0][1]
        }
    } catch(Exception e){
        branchName=''
    }    
    echo "parentMergeBranchName=$branchName"
    return branchName
}

String parentMergeBranchHash(){
    return sh(label:"Parent merge hash", returnStdout: true, script: "git log --pretty=%P -n 1 |awk '{print \$2}'").trim()
}

String branchHash(String branchName){
    return sh(label:"$branchName branch hash",returnStdout: true, script: "git ls-remote -q origin $branchName |awk '{print \$1}'").trim()
}

def deleteBranchNameHash(String branchName, String branchHash){
    String branchHashFromName = this.branchHash("$branchName")
    if (branchHash != '' && branchHashFromName != '' &&
        branchHash == branchHashFromName) {
        echo 
        sh(label:"Branch $branchName will be deleted",script:"git push origin :$branchName")
    }
}

def gitWhoPreBuildCheck(){
    if (binding.hasVariable('gitWhoPreBuildCheck')) {
        return gitWhoPreBuildCheck
    }
    String currentBranchName = currentBranchName()
    String parentMergeBranchName = parentMergeBranchName(currentBranchName)
    echo "currentBranchName=$currentBranchName"
    if (currentBranchName == 'master') {
        if ((parentMergeBranchName =~/^(?:(?:release|hotfix)\/.*|develop)$/).find()) {
            currentTags = currentTags()
            echo "currentTags: $currentTags"
            if (!currentTags) {
                if (!(parentMergeBranchName =~/^develop$/)) {
                    targetVersion = (parentMergeBranchName =~/^(?:release|hotfix)\/(.*)$/)[0][1]
                    echo "targetVersion=$targetVersion"
                    sh("git tag '$targetVersion'")
                    sh("git push origin $targetVersion")
                }
                //TODO: if merged from develop
            }
            if (!(parentMergeBranchName =~/^develop$/))
                deleteBranchNameHash(parentMergeBranchName, parentMergeBranchHash())
        } else  {
            error "Only release/, hotfix/ and dvelop branches can be merged and direct commit are prohibited into master branch."
        }
    } else if (currentBranchName == 'develop') {
        if (parentMergeBranchName ==~/^feature\/(.*)$/) {
            deleteBranchNameHash(parentMergeBranchName, parentMergeBranchHash())
        }
    } else if (currentBranchName ==~ /^release\/.*$/){
        if (parentMergeBranchName ==~/^bugfix\/(.*)$/) {
            deleteBranchNameHash(parentMergeBranchName, parentMergeBranchHash())
        } else if (parentMergeBranchName ==~/^hotfix\/(.*)$/) {
        } else if (parentMergeBranchName !=''){
            error "Only bugfix/ and hotfix/ can be merged into a release/ branch"
        } else {
            echo "it's a direct commit"
        }
        //TODO: make a allow merge from bugfix to hotfix....
    } else if (currentBranchName ==~ /^(?:hotfix|bugfix)\/.*$/){
        if (parentMergeBranchName != ''){
            error "Any merge into hotfix/ or bugfix/ branch are prohibited"
        } else {
            echo "It's a direct commit"
        }
    } else if (currentBranchName ==~ /^feature\/.*$/){
        if (parentMergeBranchName ==~ /^(?:develop|feature\/.*|)$/){   
        } else {
            error "Only develop/ branches can be merged into a feature/ branch."
        }
    }
    gitWhoPreBuildCheck = true
}
def gitStatus(){
    sh(label:"Git status", script:"git remote -v;git status;git branch -r;git branch -vv")
}
def mergeCurrentInto(String targetBranchName, boolean isAtags= false){
    String currentBranchName = currentBranchName()
    if (branchHash(currentBranchName)==currentCommitHash() && commitsCountSinceBranch(targetBranchName)>0){
        if (isAtags == false) {
            sh(label:"Checkout $currentBranchName current branch", 
                script:"git checkout -b $currentBranchName -t origin/$currentBranchName --force || git checkout -b $currentBranchName --force || git checkout $currentBranchName --force")
        } else {
            currentBranchName = currentTags()
            sh(label:"Checkout $currentBranchName current tags", 
                script:"git fetch -t")
        }
        fetchRemoteBranch(targetBranchName)
        sh(label:"Checkout $targetBranchName target branch", 
            script:"git checkout  -t origin/$targetBranchName --force || (git pull origin $targetBranchName && git checkout $targetBranchName --force)")
        gitStatus()
        
        int mergeReturnStatus=sh(label: "Merge $currentBranchName into $targetBranchName", returnStatus: true, script:"git merge --no-edit --no-ff $currentBranchName")
        echo "mergeReturnStatus:$mergeReturnStatus"
        if (mergeReturnStatus==0){
            sh(label:"Pushing the merge", script:"git push origin $targetBranchName") 
        } else {
            sh(label:"Aborting the merge",script:"git merge --abort")
            echo "Warning: the merge failed, probably a merge conflict!"
        }      
        sh(label: "Checkout current", 
            script:"git checkout -f ${currentCommitHash()} --force")
        sh(label:"Clean up branches", returnStatus: true, script:"""
            git branch -D $targetBranchName 
            git branch -D $currentBranchName
            """)
        gitStatus()
    } else {
        echo "Already merged or not a new commit"
    }          
}

def gitWhoPostBuildCheck(){
    if (binding.hasVariable('gitWhoPostBuildCheck')) {
        return gitWhoPostBuildCheck
    }
    echo "Starting gitWhoPostBuildCheck()"
    String currentBranchName = currentBranchName()
    //echo "GITWHO_DISABLE_AMB_REL:${env['GITWHO_DISABLE_AMB_REL']},GITWHO_DISABLE_AMB_HF:${env['GITWHO_DISABLE_AMB_HF']}"
    if (currentBranchName ==~ /^release\/.*$/){

        if (env['GITWHO_DISABLE_AMB_REL'] != null){
            echo "Auto Merge Back disabled for release/"
        } else {
            mergeCurrentInto('develop')
        }
    } else if (currentBranchName ==~ /^hotfix\/.*$/){
        if (env.GITWHO_DISABLE_AMB_HF != null){
            echo "Auto Merge Back disabled for hotfix/"
        } else {
            String targetBranchName=''
            String targetVersion=latestSuffixOfBranch('release')
            if (targetVersion!=''){
                targetBranchName="release/$targetVersion"
            } else {
                targetBranchName='develop'
            }
            mergeCurrentInto(targetBranchName)
        }
    } else if (currentBranchName ==~ /^master$/){
        mergeCurrentInto('develop',true)
    }
    gitWhoPostBuildCheck = true
    echo "gitWhoPostBuildCheck() finished"
}