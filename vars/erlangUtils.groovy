#!groovy

//def firstTest(){
//    return env.BRANCH_NAME
//}

def restoreElixirDeps(){
    bat "mix deps.clean --all"
    bat "mix deps.get"
}

def buildMixApp(String elixirApp){
    bat "mix release --env=prod --verbose"
    bat "_build/prod/rel/${elixirApp}/bin/${elixirApp}.bat help"
    bat "_build/prod/rel/${elixirApp}/bin/${elixirApp}.bat console"
    bat "_build/prod/rel/${elixirApp}/bin/${elixirApp}.bat help"
}

def copyArtifactToTargetFolder(String elixirApp, String MIX_BUILD_VSN, String destTarget){
    sh "cp  _build/prod/rel/${elixirApp}/releases/${MIX_BUILD_VSN}/sys.config  _build/prod/rel/${elixirApp}/releases/${MIX_BUILD_VSN}/sys0.config"
    sh "cp  _build/prod/rel/${elixirApp}/sys.config  _build/prod/rel/${elixirApp}/releases/${MIX_BUILD_VSN}"
    bat """ xcopy "_build/prod/rel/${elixirApp}" "${destTarget}" /E /S /H /Y """
}
