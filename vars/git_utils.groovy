def git_utils(){
    return "I am in git_utils"
}
//echo "not in a function" // DONT DO THAT


def currentBranchName(){
    return env.BRANCH_NAME
}