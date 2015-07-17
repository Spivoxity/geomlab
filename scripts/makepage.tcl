# makepage.tcl

set scriptdir [file dirname [info script]]

source "$scriptdir/common.tcl"

proc read-file {name} {
    set f [open $name r]
    set s [read $f]
    close $f
    return $s
}


# MAIN PROGRAM

set fname [lindex $argv 0]

set incdir [file dirname $fname]

set template [read-file "$incdir/skeleton.html"]
set content [read-file $fname]

# Find the content title and section
set this [file rootname [file tail $fname]]
extract {^<!--(.*?)/(.*?)-->} $content title secname

# Prepare the content
if {[regexp -indices {{{HelpStart}}} $content match]} {
    set content [put-string $content $match \
                     [read-file "$incdir/HelpStart.tmpl"]]
}

regsub -all {<!--.*?-->} $content "" content

regsub -line -all {===(.*)===} $content {<h3>\1</h3>} content
regsub -line -all {==(.*)==} $content {<h2>\1</h2>} content
regsub -all {{{GeomLab}}} $content "" content

regsub -all {{{XLink\|([^{|}]*)[^{}]*\|([^{}]*)}}} $content \
    {<a href="\1.html">\2</a>} content
regsub -all {{{Resource\|([^{|}]*)\|([^{|}]*)}}} $content \
    {<a href="\1">\2</a>} content

proc twin {lhs rhs} {
    set s "<table class=\"twin\"><tr><td class=\"input\"><code>$lhs</code></td>"
    append s "<td class=\"output\">$rhs</td></tr></table>"
    return $s
}

proc image {source} {
    return "<img src=\"$source\"/>"
}

regsub -all {{{GeomPic\|([^{|}]*)\|([^|]*)\|[^{|}]*}}} $content \
    "[twin {\2} [image {\1.png}]]" content
# Missing picture replaced by blank.png
regsub -all {{{GeomPic\|([^{|}]*)\|([^{|}]*)}}} $content \
    "[twin {\2} [image {blank.png}]]" content
# Other kinds, such as missing picture, ! for JPEG

regsub -all {\[\[Image:([^|]*?)(\|[^\]]*?)?\]\]} $content \
    {<img src="\1"/>} content
regsub -all {\[\[Media:([^|]*)\|([^\]]*)\]\]} $content \
    {<a href="\1">\2</a>} content
regsub -all {\[(https?:[^ ]*) ([^\]]*)\]} $content \
    {<a href="\1">\2</a>} content

regsub -all {{{=}}} $content = content

regsub -all {{{IfBook\|([^|]*)\|([^{|}]*)}}} $content {\2} content
regsub -all {{{IfBook\|([^|]*)}}} $content {} content
regsub -all {{{IfWiki\|([^{|}]*)}}} $content {\1} content

if {[regexp {{{bigspace}}} $content]} {error bigspace}

regsub -all {''(.*?)''} $content {<i>\1</i>} content
regsub -all {{{Markup\|(.*?)}}} $content {<\1>} content
regsub -line -all {^;(.*?): *(.*?)$} $content {<dt>\1</dt><dd>\2</dd>} content
regsub -all {{{Message\|(.*?)\|(.*?)\|(.*?)}}} $content \
    {<a name="\1">\2: \3</a>} content
regsub -all {{{LibDef\|(.*?)}}} $content {\1} content
regsub -all {{{Cons}}} $content ":" content
regsub -line -all {^:(.*)$} $content {<p class="equation">\1</p>} content
regsub -line -all {^#(.*)$} $content {<li>\1</li>} content
regsub -line -all {^\*(.*)$} $content {<li>\1</li>} content
regsub -all {@\\\\@} $content {<code>\\</code>} content
regsub -all {@(.*?)@} $content {<code>\1</code>} content

regsub -all \
    {\n+((([^ <\n]|<code>|<a )[^\n]*\n)*([^ <\n]|<code>|<a )[^\n]*)\n} \
    $content "\n<p>\\1</p>\n" content

while {[regexp -indices {(\n [^\n]*)+\n} $content match]} {
    set text [get-string $content $match]
    regsub -all {\n } $text "\n!xyzzy!" text
    set content [put-string $content $match "\n<pre>$text</pre>"]
}

regsub -all {!xyzzy!} $content "" content

# Paste literal HTML last
while {[regexp -indices {{{#html:([A-Za-z]*)}} *\n} $content match tnamex]} {
      set tname [get-string $content $tnamex]
      set text [read-file "$incdir/$tname.tmpl"]
      set content [put-string $content $match $text]
}

### END OF CONTENT

# Like regsub, but with no interpretation of the subst text
proc regsubq {re text sub} {
    while {[regexp -indices $re $text match]} {
        set text [put-string $text $match $sub]
    }
    return $text
}

# Paste in the title
set template [regsubq {\#TITLE\#} $template $title]

# Format the section links
set re {#SECTION:([^#]*):([^#]*)#}
while {[regexp -indices $re $template match secx txtx]} {
    set sec [get-string $template $secx]
    set txt [get-string $template $txtx]

    if {$sec eq $this} {
	# Case 1: we are the root page of this section
	set link "<span class=\"actnav\">$txt</span>"
    } elseif {$sec eq $secname} {
	# Case 2: we are in this section, but not the root
	set link "<a class=\"actnav\" href=\"$sec.html\">$txt</a>"
    } else {
	# Case 3: this is a different section
	set link "<a href=\"$sec.html\">$txt</a>"
    }

    set template [put-string $template $match $link]
}

# Paste in the content
set template [regsubq {\#CONTENT\#} $template $content]

puts -nonewline $template
