set scriptdir [file dirname [info script]]

source "$scriptdir/login.tcl"
source "$scriptdir/common-upload.tcl"

proc delete-oldimage {title oldname} {
    # Get a delete token
    set dict [json-request action query  prop info \
		  intoken delete  titles $title]
    set pageinfo [dict get $dict query pages]
    set deltoken [dict get [lindex $pageinfo 1] deletetoken]

    # Delete the old image
    set dict [json-request action delete  title $title  token $deltoken \
		  oldimage $oldname]
    if {! [dict exists $dict delete]} {
	puts $dict
	error "Oops"
    }
}

set images $argv

login

foreach img $images {
    puts $img

    set title [mk-title $img]

    # Get revision list
    set dict [json-request action query \
		  titles $title  prop imageinfo \
		  iiprop archivename  iilimit 1000]
    # puts $dict
    set revs [dict get [lindex [dict get $dict query pages] 1] imageinfo]

    puts $revs

    foreach r [lrange $revs 1 end] {
	set oldname [dict get $r archivename]

	puts "Deleting $oldname"
	delete-oldimage $title $oldname
    }
}
