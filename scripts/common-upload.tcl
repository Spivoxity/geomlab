package require http
package require json
package require sha1

proc map {f xs} {
    set ys {}
    foreach x $xs {lappend ys [eval $f [list $x]]}
    return $ys
}

proc captialize {fname} {
    set s [string toupper [string range $fname 0 0]]
    append s [string range $fname 1 end]
    return $s
}

proc mk-title {fname} {
    return "File:[captialize $fname]"
}

proc json-request {args} {
    global wiki cookies

    set q [eval ::http::formatQuery $args format json]
    if {[info exists cookies]} {
	set tok [::http::geturl "$wiki/api.php" -query $q -headers $cookies]
    } else {
	set tok [::http::geturl "$wiki/api.php" -query $q]
    }
    set dict [::json::json2dict [::http::data $tok]]
    ::http::cleanup $tok
    return $dict
}

proc file-data {fname} {
    set fid [open $fname r]
    fconfigure $fid -translation binary
    set data [read $fid]
    close $fid
    return $data
}

proc json-multipart {args} {
    global wiki cookies

    set boundary "dffba4e4c0927fd80c475ce6f4d365df"
    set form ""

    foreach {k v} [concat $args [list format json]] {
	switch -glob $k {
	    file:* {
		set fname [lindex {split $k ":"} 1]
		append form "--$boundary\nContent-Disposition: form-data;\
			name=\"file\"; filename=\"$fname\"\n\n$v\n"
	    }
	    default {
		append form "--$boundary\nContent-Disposition: form-data;\
			name=\"$k\"\n\n$v\n"
	    }
	}
    }

    append form "--$boundary--\n"
    set tok [::http::geturl "$wiki/api.php" \
		 -type "multipart/form-data; boundary=$boundary" \
		 -query $form -headers $cookies]
    set dict [::json::json2dict [::http::data $tok]]
    # puts $dict
    ::http::cleanup $tok
    return $dict
}

proc login {} {
    global uname password
    global cookies

    # Make initial login request
    set dict [json-request action login \
		  lgname $uname lgpassword $password]
    dict with dict login {
	set tok $token
	set prefix $cookieprefix
	set jar [list ${prefix}_session=$sessionid]
    }
    set cookies [list Cookie [join $jar ";"]]
    
    # Send confimation request
    set dict2 [json-request action login \
		   lgname $uname lgpassword $password lgtoken $token]
    dict with dict2 login {
	lappend jar ${prefix}UserName=$lgusername ${prefix}UserID=$lguserid \
	    ${prefix}Token=$lgtoken
    }
    set cookies [list Cookie [join $jar ";"]]
}    

proc upload {title contents} {
    global cookies

    if {! [info exists cookies]} login

    # Get an edit token
    set dict [json-request action query \
		  prop info  intoken edit  titles $title]
    set pageinfo [dict get $dict query pages]
    set edittoken [dict get [lindex $pageinfo 1] edittoken]

    # Put the page
    set dict [json-multipart action edit \
		  title $title  token $edittoken  text $contents]

    if {[dict get $dict edit result] ne "Success"} {
	puts $dict
	error "Oops"
    }
}
