#!groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESS'

    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    def chatMessage = """${buildStatus}: Job '${env.BUILD_TAG}' ${currentBuild.displayName} - \${BUILD_DURATION}<br/>
         \${HIPCHAT_CHANGES_OR_CAUSE} <br/>
         \${BLUE_OCEAN_URL} <br/>
         \${TEST_REPORT_URL}"""
    def details = """<p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""
    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESS') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
    }

    // Send notifications
    //slackSend (color: colorCode, message: summary)

    hipchatSend (color: color, notify: true, message: chatMessage)

    /*emailext (
            to: 'bitwiseman@bitwiseman.com',
            subject: subject,
            body: details,
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )*/
}