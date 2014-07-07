set scriptdir [file dirname [info script]]

source "$scriptdir/login.tcl"
source "$scriptdir/common-upload.tcl"
source "$scriptdir/../wiki/titles"

proc template-upload {tmpl} {
    global title
    set text [file-data $tmpl]
    set name [file tail $tmpl]
    if {[file extension $name] == ".css"} {
        upload MediaWiki:$name $text
    } else {
        set key [file rootname $name]
        upload Template:$key $text
    }
}

foreach f $argv { template-upload $f }
