#!groovy

import jenkins.plugins.hipchat.HipChatNotifier;
import com.cloudbees.plugins.credentials.*;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

def call(String messages) {
    def emails=sh(returnStdout: true, script: "git log -10 --pretty=%ae|tr '[:upper:]' '[:lower:]'|sort|uniq").readLines()
    echo emails
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
