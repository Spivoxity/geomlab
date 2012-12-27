# texsheet.tcl

proc lsplit {xs args} {
    set n [llength $args]
    for {set i 0} {$i < $n} {incr i} {
	uplevel [list set [lindex $args $i] [lindex $xs $i]]
    }
}

set name [lindex $argv 0]
set stem [file rootname [file tail $name]]

# Read content file
set cont_fd [open $name "r"]
set content [read $cont_fd]
close $cont_fd

source ../wiki/titles

# This is horrible.  Please use Python instead.
proc regsub-eval {re string cmd} {
    # Protect existing metacharacters in the string
    set string [string map {\[ \\[ \] \\] \$ \\$ \\ \\\\} $string]

    # Perform the substitution
    regsub -all $re $string "\[$cmd\]" string

    # Use subst to evaluate the command
    return [subst $string]
}

regsub -line -all {{{GeomPic\|(.*?)\|(.*?)\|(.*?)}}} $content \
    {\\example@\2@\\gives \1\\end} content
regsub -line -all {{{GeomPic\|(.*?)\|(.*?)}}} $content \
    {\\example@\2@\\gives blank\\end} content
regsub -line -all {{{GeomLab}}} $content {} content
regsub -line -all {^:(.*)$} $content "\\\\quotation\n\\1\n\\\\endquote" content

proc verbfun {s} {
    regsub -all -line {^ } $s "" s
    return "\n\\verbatim$s\n\\endverb"
}
set content [regsub-eval "(\n \[^\n]*)+" $content {verbfun "\0"}]

regsub -line -all {&nbsp;} $content {~} content
regsub -line -all {''([^']*)''} $content {{\\it \1\\/}} content
regsub -line -all {&times;} $content {$\times$} content
regsub -line -all {^:(.*)$} $content {\quotation \1\endquote} content
regsub -line -all {@([^@]*)<br */>([^@]*)@} $content {@\1@\\\\@\2@} content

puts "\\sheet{$title($stem)}"
puts $content
