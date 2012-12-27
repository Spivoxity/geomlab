package require http

source wiki/titles

set fname [lindex $argv 0]
if {[llength $argv] > 1} {
    set ttl [lindex $argv 1]
} else {
    set ttl "$title($fname)"
}
set qstring [::http::formatQuery title $ttl action raw]
set url "http://spivey.oriel.ox.ac.uk/wiki/index.php?$qstring"
puts $url
set tok [::http::geturl $url]
set contents [::http::data $tok]
::http::cleanup $tok

set fid [open "$fname.wiki" w]
puts -nonewline $fid $contents
close $fid

