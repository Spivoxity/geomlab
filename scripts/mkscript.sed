# Find lines {{GeomPic...}} in sheet?.wiki, and output
# a script for MakePicture

s/{{=}}/=/g
s@<br/>@ @g
s/&nbsp;/ /g

/{{GeomPic|\(.*\)|\(.*\)|\*}}/s//{{GeomPic|\1|\2|\2}}/

/{{GeomPic|\(.*\)|.*|define \([A-Za-z0-9]*\)\(.*\)}}/{
    s//define \2\3; makepic(\2, "\1");/p
}

/{{GeomPic|\(.*\)|.*|!\(.*\)}}/s//makeimg(\2, "\1");/p

/{{GeomPic|\(.*\)|.*|\(.*\)}}/s//makepic(\2, "\1");/p
