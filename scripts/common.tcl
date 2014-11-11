proc get-title {name text} {
    global title

    set key [file rootname $name]
    
    if {[file extension $name] eq ".tmpl"} {
        return "Template:$key"
    }

    if {[file extension $name] eq ".css"} {
        return "MediaWiki:$key"
    }

    if {[regexp {^<!--(.*?)-->} $text _ ttl]} {
        return $ttl
    }

    if {[info exists title($key)]} {
        return $title($key)
    }

    error "No title for $key"
}

