proc get-title {name text} {
    global title

    if {[regexp {^<!--(.*?)-->} $text _ ttl]} {
        return $ttl
    }

    set key [file rootname $name]
    
    if {[info exists title($key)]} {
        return $title($key)
    }

    error "No title for $key"
}

