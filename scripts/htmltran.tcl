# htmltran.tcl

set scriptdir [file dirname [info script]]

source $scriptdir/common.tcl
source $scriptdir/../wiki/titles

proc readfile {fname} {
    set fd [open $fname "r"]
    set result [read $fd]
    close $fd
    return $result
}

set body [lindex $argv 0]
set stem [file rootname [file tail $body]]
set filename "$stem.html"

set content [readfile $body]
set pagetitle [get-title $stem $content]

set frame [readfile "wiki/htmlframe"]

regsub -line -all {===(.*)===} $content {<h3>\1</h3>} content
regsub -line -all {==(.*)==} $content {<h2>\1</h2>} content
regsub -all {{{GeomLab}}} $content "" content
regsub -all {''(.*?)''} $content {<i>\1</i>} content
regsub -all {{{Markup\|(.*?)}}} $content {<\1>} content
regsub -all {{{Message\|(.*?)\|(.*?)\|(.*?)}}} $content \
    {<a name="\1">\2: \3</a>} content
regsub -line -all {^;(.*)$} $content {<dt>\1</dt>} content
regsub -line -all {^::(.*)$} $content {<dd><dl><dd>\1</dd></dl></dd>} content
regsub -line -all {^:(.*)$} $content {<dd>\1</dd>} content
regsub -line -all {^#(.*)$} $content {<li>\1</li>} content
regsub -all {@(.*?)@} $content {<code>\1</code>} content
regsub -all "\n\n" $content "\n\n<p>" content

puts -nonewline [subst $frame]

