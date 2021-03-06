#!groovy

import jenkins.plugins.hipchat.HipChatNotifier;
import com.cloudbees.plugins.credentials.*;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import groovy.json.*;

def call(String message) {
    emails = gitUtils.getEmailLastCommiter()
    //emails=["michel.buczynski@tdsecurities.com"]
    echo "emails:${emails}"
    server = getServer()
    token = getToken()
    def data = [
            message       : message,
            notify        : true,
            message_format: 'html'
    ]
    def json = JsonOutput.toJson(data)
    for (email in emails) {
        try {
            if (
            sh(returnStdout: true, script: """
                    curl -k -H "Content-Type: application/json" https://$server/v2/user/$email/message?auth_token=$token -X POST -d '$json'  --fail --silent --show-error 2>&1
            """) == "")
                println("hipchat: send to $email");
            else
                echo("hipchat: NOT send to $email");
        } catch (Exception e) {
            echo("hipchat: NOT send to $email");
        }
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

