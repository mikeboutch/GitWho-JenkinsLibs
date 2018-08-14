#!groovy

import jenkins.plugins.hipchat.HipChatNotifier;
import com.cloudbees.plugins.credentials.*;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import groovy.json.*;

def call(String message) {
    emails=getEmail10lastCommiter()
    emails="michel.buczynski@tdsecurities.com".readLines()
    server=getServer()
    token=getToken()
    def data = [
            message       : message,
            notify        : true,
            message_format: 'html'
    ]
    def json = JsonOutput.toJson(data)
   for (email in emails.readLines()) {
       sh """
    curl -H "Authorization: $token"  -H "Content-Type: application/json" https://hipchat.dom.se/v2/user/$email/message -X POST -d '$json'
    """
   }

}
def getServer() {
    HipChatNotifier.DescriptorImpl hipChatDesc =
            Jenkins.getInstance().getDescriptorByType(HipChatNotifier.DescriptorImpl.class);
    return hipChatDesc.getServer()
}
def getToken() {
    HipChatNotifier.DescriptorImpl hipChatDesc =
            Jenkins.getInstance().getDescriptorByType(HipChatNotifier.DescriptorImpl.class);
    credentialsId = hipChatDesc.getCredentialId().toString();
    creds = CredentialsProvider.lookupCredentials(StringCredentials.class, Jenkins.instance, null, null);
    for (c in creds) {
        if (c.id == credentialsId) {
            return c.getSecret()
        }
    }
}

def getEmail10lastCommiter(){
    return sh(returnStdout: true, script: "git log -10 --pretty=%ae|tr '[:upper:]' '[:lower:]'|sort|uniq").readLines()
}