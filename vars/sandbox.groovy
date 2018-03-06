
println("bob")



def splitVersion(v){
    if ((m=v=~/^(\d+)\.(\d+)(?:|\.(\d+)(?:|\.(\d+)|((?:\+|-).*)))$/)){
        return m[0][1..-1]
    } else {
        //error "No valid version number:$v"
        return [0,0,0,0,""]
    }
}


def greaterVersion(v1,v2, Integer column) {
    a1=splitVersion(v1)
    a2=splitVersion(v2)
    if (a1[0]>a2[0] ||
             (a1[0]==a2[0] && a1[1]>a2[1]) ||
             (a1[0]==a2[0] && a1[1]==a2[1] && a1[2]>a2[2]) ||
             (column==4 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]>a2[3]) ||
             (column==5 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]==a2[3] && a1[3]>a2[3])) {
         //println "a1 is greater $column"
         return v1
     } else if (a1[0]<a2[0] ||
             (a1[0]==a2[0] && a1[1]<a2[1]) ||
             (a1[0]==a2[0] && a1[1]==a2[1] && a1[2]<a2[2]) ||
             (column==4 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]<a2[3]) ||
             (column==5 && a1[0]==a2[0] && a1[1]==a2[1] && a1[2]==a2[2] && a1[3]==a2[3] && a1[3]<a2[3])){
         //println "a2 is greater"

         return v2
     } else return v1
}
def greaterVersion(v1,v2) {
    return greaterVersion(v1,v2,3)
}


println greaterVersion("1123.4334.5455","1123.4334.55.234")
println splitVersion("1123.4334")
println greaterVersion("1123.4334.45+sdfghj-sdfghj","1123.4334.56-24567890,4")



//def unSplitVersion(a, column ){
//    println "colL $column"
//    if (column==3) {
//        return "${a[0]}.${a[1]}.${a[2]}"
//    } else if (column==4){
//        if (a[3]==null) {
//            return "${a[0]}.${a[1]}.${a[2]}.${a[4]}"
//        } else if (a[4]==null) {
//            return "${a[0]}.${a[1]}.${a[2]}.${a[3]}"
//        } else throw "error"
//        //else  error "really not a valid version"
//    } else if (column==5){
//        return "${a[0]}.${a[1]}.${a[2]}.${a[3]}.${a[4]}"
//    } else throw "error really not a valid version "
//}
//def unSplitVersion( a){
//    return unSplitVersion(a[],3)
//}