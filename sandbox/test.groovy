def splitVersion(v) {
    if ((m = v =~ /^(\d+)\.(\d+)\.(\d+)$/)){
        return m[0][1..-1]
    } else {
        return [0, 0, 0]
    }
}


def greaterVersion(v1, v2) {
    a1 = this.splitVersion(v1)
    a2 = this.splitVersion(v2)
    println(a1)
    println a2
    if (a2==[0,0,0] || a1[0] > a2[0] ||
            (a1[0] == a2[0] && a1[1] > a2[1]) ||
            (a1[0] == a2[0] && a1[1] == a2[1] && a1[2] > a2[2])) {
        //println "a1 is greater $column"
        return v1
    } else if (a1==[0,0,0] || a1[0] < a2[0] ||
            (a1[0] == a2[0] && a1[1] < a2[1]) ||
            (a1[0] == a2[0] && a1[1] == a2[1] && a1[2] < a2[2])) {
        //println "a2 is greater"

        return v2
    } else return v1
}



println greaterVersion("18.3.2","18.3.1")
println greaterVersion("18.3.2.4","18.3.1.4")