set fd [open [lindex $argv 0] r]

gets $fd line
while {$line ne "====="} {
    puts $line
    gets $fd line
}

set sep ""
set item {}

proc put-item {} {
    global sep item
    if {$sep ne ""} {puts $sep}
    set ln [lindex $item 0]
    if {[llength $item] == 1} {
	puts -nonewline "\"$ln\""
    } else {
	puts -nonewline "\[\"$ln\""
	foreach ln [lrange $item 1 end] {
	    puts ","; puts -nonewline "  \"$ln\""
	}
	puts -nonewline "\]"
    }
}

while {1} {
    if {[gets $fd line] < 0} {error "Unexpected EOF"}
    if {[regexp {^=====} $line]} {
	put-item; puts ""; break
    } elseif {[regexp "^-----" $line]} {
	put-item; set item {}; set sep ", "
    } else {
	lappend item $line
    }
}

while {[gets $fd line] >= 0} {puts $line}
