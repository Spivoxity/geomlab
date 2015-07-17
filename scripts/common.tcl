proc get-title {name text} {
    global title

    set key [file rootname $name]
    
    if {[file extension $name] eq ".tmpl"} {
        return "Template:$key"
    }

    if {[file extension $name] eq ".css"} {
        return "MediaWiki:$key"
    }

    if {[regexp {^<!--(.*?)/.*?-->} $text _ ttl]} {
        return $ttl
    }

    if {[info exists title($key)]} {
        return $title($key)
    }

    error "No title for $key"
}

proc lsplit {xs args} {
    set n [llength $args]
    for {set i 0} {$i < $n} {incr i} {
	uplevel [list set [lindex $args $i] [lindex $xs $i]]
    }
}

proc get-string {s loc} {
    lsplit $loc b e
    return [string range $s $b $e]
}

proc put-string {s loc t} {
    lsplit $loc b e
    return [string replace $s $b $e $t]
}

# extract -- match regexp or raise error
proc extract {regexp string args} {
    if {[llength $args] == 0} {
	# return one match as the result
	if {! [regexp $regexp $string _ result]} {
	    error "Matching failed"
	}
	return $result
    } else {
	# set one or more variables from the match
	if {! [uplevel [list regexp $regexp $string _] $args]} {
	    error "Matching failed"
	}
	return {}
    }
}

