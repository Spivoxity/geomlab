# textran.tcl

set scriptdir [file dirname [info script]]

source $scriptdir/common.tcl
source $scriptdir/../wiki/titles

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

set pagetitle [get-title $stem $content]

# This is horrible.  Please use Python instead.
proc regsub-eval {re string cmd} {
    # Protect existing metacharacters in the string
    set string [string map {\[ \\[ \] \\] \$ \\$ \\ \\\\} $string]

    # Perform the substitution
    regsub -all $re $string "\[$cmd\]" string

    # Use subst to evaluate the command
    return [subst $string]
}

regsub -all {<!--.*?-->} $content "" content

regsub -line -all {==(.*)==} $content {\section* \1.} content
regsub -line -all {{{GeomPic\|(.*?)\|(.*?)\|(.*?)}}} $content \
    {\\example@\2@\\gives \1\\end} content
regsub -line -all {{{GeomPic\|(.*?)\|(.*?)}}} $content \
    {\\example@\2@\\gives blank\\end} content
regsub -line -all {^:(.*)$} $content "\\\\quotation\n\\1\n\\\\endquote" content

regsub -all {{{IfWiki\|.*?\|(.*?)}}} $content "\\1" content
regsub -all {{{IfBook\|(.*?)\|.*?}}} $content "\\1" content

proc verbfun {s} {
    regsub -all -line {^ } $s "" s
    return "\n\\verbatim$s\n\\endverb"
}
set content [regsub-eval "(\n \[^\n]*)+" $content {verbfun "\0"}]

regsub -line -all {<b>(.*?)</b>} $content {{\\bf \1}} content
regsub -line -all {<i>(.*?)</i>} $content {{\\it \1\/}} content
regsub -line -all {&nbsp;} $content {~} content
regsub -line -all {''([^']*)''} $content {{\\it \1\\/}} content
regsub -line -all {&times;} $content {$\times$} content
regsub -line -all {^:(.*)$} $content {\quotation \1\endquote} content
regsub -line -all {@([^@]*)<br */>([^@]*)@} $content {@\1@\\\\@\2@} content
regsub -all {{{bigspace}}} $content {\bigspace} content
regsub -all {&lt;} $content {<} content
regsub -all {&gt;} $content {>} content

regsub -all {\[\[Image:(.*?)\.png\]\]} $content {\picture{\1}} content

puts "\\sheet{$pagetitle}"
puts $content
