#!groovy

def call(String elixirApp){
    return erlangUtils.buildMixApp(elixirApp)
}
