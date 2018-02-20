

# Current Branch Name
## Jenkins:
### groovy:
`env.BRANCH_NAME`
## Working directory:
### shell:
`git symbolic-ref --short HEAD`  

# Commit Hash:
# Jenkins
### groovy
`env.GIT_COMMIT`
`env.GIT_COMMIT[0..6]`
## Working directory:
### shell:
`git rev-parse HEAD`
`git rev-parse --short HEAD`

# Lasted tags
# Jenkins
### groovy
`sh(returnStdout: true, script: 'git fetch --tags &>/dev/null;git describe --abbrev=0 --tags 2>/dev/null || true').trim()`
## Working directory:
### bash:
`git describe --abbrev=0 --tags 2>/dev/null`
### Powershell:
`git describe --abbrev=0 --tags >2 $null`

# Current tags:
# Jenkins
### groovy
`sh(returnStdout: true, script: 'git fetch --tags &>/dev/null;git name-rev --tags --name-only HEAD').trim().replaceFirst(/\^0$/,"").replaceFirst(/^undefined$/,"")`


# Number of commits since branch develop
# Jenkins
### groovy
`sh(returnStdout: true, script: 'git rev-list --no-merges --count HEAD ^origin/develop').trim()`