set scriptdir [file dirname [info script]]

source "$scriptdir/login.tcl"
source "$scriptdir/common-upload.tcl"
source "$scriptdir/../wiki/titles"

proc page-upload {page} {
    global title

    set name [file tail $page]

    if {[regexp {\.php$} $name]} {
        exec rsync $page spivey:/var/www/gwiki/extensions/$name >@stdout
        return
    }

    set text [file-data $page]

    if {! [regexp {^<!--(.*?)-->} $text _ ttl]} {
        set key [file rootname $name]
        if {[info exists title($key)]} {
            set ttl $title($key)
        } else {
            error "No title for $key"
        }
    }

    upload $ttl $text
}

foreach f $argv { page-upload $f }
