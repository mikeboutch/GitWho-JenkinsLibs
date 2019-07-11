#!groovy

/**
 * Send notifications based on build status string
 */
def call(String buildStatus = 'STARTED', String warningMessages='') {
    // build status of null means successful
    buildStatus = buildStatus ?: 'SUCCESS'
    // notify=false
    // notifyCommiter=false


    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    // def chatMessage = """</p>${buildStatus}: Job "${env.BUILD_TAG}"<br/>
    //      ${currentBuild.displayName} - ${currentBuild.durationString}<br/>
    //      ${env.RUN_DISPLAY_URL} <br/>
    //      """
    // def details = """<p>${buildStatus}: Job "${env.JOB_NAME}" [${env.BUILD_NUMBER}]:</p>
    // <p>Check console output at &QUOT;<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""
    def msTeamMessage= """${env.JOB_NAME} - #${env.BUILD_NUMBER} - <a href="${env.BUILD_URL}">${currentBuild.displayName}</a> - ${currentBuild.durationString}"""
    if (warningMessages!=''){
        msTeamMessage="""<h2>$warningMessages</h2>"""+msTeamMessage
    }

    // Override default values based on build status
    if (buildStatus == 'STARTED'  || buildStatus == 'INFO') {
        color = 'BLUE'
        colorCode = '00FFFF'
    } else if (buildStatus == 'SUCCESS') {
        color = 'GREEN'
        colorCode = '00FF00'
        //notifyCommiter=true
    } else if ( buildStatus == 'UNSTABLE') {
        color = 'MAROON'
        colorCode = 'C70039'
        //notify=true
        //notifyCommiter=true
    } else if (buildStatus == 'FAILURE' ) {
        color = 'RED'
        colorCode = 'FF0000'
        //notify=true
        //notifyCommiter=true
    } else { //normally a warnings
        color = 'ORANGE'
        colorCode = 'FF4500'
        //notify=true
        //notifyCommiter=true
    }

    // Send notifications
    //slackSend (color: "#$colorCode", message: summary)
    // try {
    //     hipchatSend (color: color, notify: notify, message: chatMessage)
    //     if (notifyCommiter) hipchatSendPrivate(chatMessage)
    // } catch(Exception e){
    //     return ''
    // }
    /*emailext (
            to: 'bitwiseman@bitwiseman.com',
            subject: subject,
            body: details,
            recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )*/
    office365ConnectorSend( webhookUrl:"${env.MS_TEAM_WEBHOOK_URL}", color:colorCode,
        status:buildStatus, message:msTeamMessage)
}