set scriptdir [file dirname [info script]]

source "$scriptdir/login.tcl"
source "$scriptdir/common-upload.tcl"

proc get-hashes {images} {
    global hash

    set dict [json-request action query \
		  titles [join [map mk-title $images] "|"] \
		  prop imageinfo iiprop sha1]
    dict for {_ d2} [dict get $dict query pages] {
	dict with d2 {
	    if {[info exists imageinfo]} {
		set sha1 [dict get [lindex $imageinfo 0] sha1]
		set hash($title) $sha1
	    }
	}
    }
}

proc upload {image} {
    global wiki

    # Get an edit token
    set dict [json-request action query \
		  prop info intoken edit titles [mk-title $image]]
    set pageinfo [dict get $dict query pages]
    set edittoken [dict get [lindex $pageinfo 1] edittoken]

    # Upload the image
    # "ignorewarnings" is needed to force duplicate files to be registered
    set dict [json-multipart action upload filename [captialize $image] \
		  token $edittoken ignorewarnings 1 \
		  file:foo.png [file-data $image]]
    if {[dict get $dict upload result] ne "Success"} {
	puts $dict
	error "Oops"
    }
}

login

set images $argv
get-hashes $images

foreach img $images {
    set title [mk-title $img]
    set sha1 [::sha1::sha1 -hex -file $img]
    if {[info exists hash($title)] && $hash($title) eq $sha1} {
	puts "$img ok"
    } else {
	puts "upload $img"
	upload $img
    }
}
