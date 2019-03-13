#!groovy

def call(String elixirApp, String HYDRA_VSN, String destFolder){
    return erlangUtils.copyArtifactToTargetFolder(elixirApp, HYDRA_VSN, destFolder)
}
