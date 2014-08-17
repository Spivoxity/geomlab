proc test {name scripts exp result} {
    test-sess $name "obj/geomlab.gls" $scripts $exp $result
}

proc test-sess {name sess scripts exp result} {
    puts -nonewline $name

    set scripts1 {}
    foreach f $scripts {append scripts1 "progs/$f"}
    set cmd [concat [list exec java -cp obj geomlab.RunScript] \
                     [list -s $sess] $scripts1 [list -e $exp]]

    if {[catch {eval $cmd} out]} {
        # Error message on penultimate line of output
        set val [lindex [split $out "\n"] end-1]
    } else {
        # Value marked with -->
        regexp -line {^--> (.*)$} $out _ val
    }

    if {[string equal $val $result]} {
	puts " OK"
    } else {
	puts " ***FAILED***"
    }
}

proc pngtest {name scripts exp result} {
    puts -nonewline $name

    set scripts1 {}
    foreach f $scripts {append scripts1 "progs/$f"}

    set cmds [list -e "_saveimg(_render($exp, 144, 0.5, 0.95), \"png\", \"$name.png\")"]

    set out [eval [concat [list exec java -cp obj geomlab.RunScript] \
		       $scripts1 $cmds]]
    set hash [lindex [exec md5sum $name.png] 0]
    # file delete $name.png
    if {[string equal $hash $result]} {
	puts " OK"
    } else {
	puts " ***FAILED*** $hash"
    }
}
