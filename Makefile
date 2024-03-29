# Top level Makefile for GeomLab

JAVAC = javac

PACKAGES = funbase funjit geomlab plugins
JAVA := $(patsubst src/%,%,$(foreach pkg,$(PACKAGES),$(wildcard src/$(pkg)/*)))
SOURCE = $(JAVA) boot.txt compiler.txt prelude.txt 
HELP = commands errors language library tips
RESOURCES = VeraMono.ttf mike.jpg mikelet.jpg contents.html style.css \
	$(HELP:%=%.html) properties sunflowers.jpg
ICONS = icon16.png icon32.png icon64.png icon128.png
SESSIONS = obj/geomlab.gls examples.gls

default: build 

all: build figs book

figs book: force
	$(MAKE) -C $@ all

build: prep .compiled $(RESOURCES:%=obj/%) $(SESSIONS) $(ICONS:%=obj/%)

prep: force
	@mkdir -p obj

JFILES = $(PACKAGES:%=src/%/*.java)
.compiled: $(wildcard $(JFILES))
	$(JAVAC) -d obj $(JFILES)
	echo timestamp >$@

Boot.class: src/Boot.java .compiled
	$(JAVAC) -d . -cp obj $<

obj/%: res/%; cp $< $@
obj/%: src/%; cp $< $@

obj/%.html: wiki/%.wiki wiki/htmlframe scripts/htmltran.tcl
	tclsh scripts/htmltran.tcl $< >$@

RUNJAVA = java -cp .:obj -ea
RUNSCRIPT = $(RUNJAVA) geomlab.RunScript

obj/icon128.png:
	$(RUNSCRIPT) progs/solutions.txt \
		-e 'savepic(colour(T), "$@", 128, 0.5, 0)'

obj/icon%.png: obj/icon128.png
	convert obj/icon128.png -scale $*x$* $@

obj/geomlab.gls: .compiled Boot.class src/compiler.txt src/prelude.txt
	$(RUNSCRIPT) -B Boot src/compiler.txt \
		src/prelude.txt -e '_save("$@")'

geomlab0.gls: .compiled src/boot.txt src/prelude.txt
	$(RUNSCRIPT) -b src/boot.txt src/compiler.txt \
		src/prelude.txt -e '_save("$@")'

examples.gls: obj/geomlab.gls progs/examples.txt
	$(RUNSCRIPT) progs/examples.txt -e '_save("$@")'

progs/examples.txt: progs/examples.in progs/exprep.tcl
	tclsh progs/exprep.tcl $< >$@

life.gls: obj/geomlab.gls progs/life.txt
	$(RUNSCRIPT) progs/life.txt -e '_save("$@")'

progs/life.txt: progs/life.tcl
	tclsh $< >$@

bootstrap: Boot.class force
	$(RUNSCRIPT) -B Boot src/compiler.txt -e '_dump("Boot1")'
	$(JAVAC) -d . -cp obj Boot1.java
	$(RUNSCRIPT) -B Boot1 src/compiler.txt -e '_dump("Boot2")'
	$(JAVAC) -d . -cp obj Boot2.java
	$(RUNSCRIPT) -B Boot2 src/compiler.txt -e '_dump("Boot3")'
	sed 's/Boot2/Boot3/' Boot2.java | cmp - Boot3.java
	(sed '/^ *$$/q' src/Boot.java; sed 's/Boot2/Boot/' Boot2.java) >tmpa
	mv tmpa src/Boot.java

# Testing

test: force
	tclsh test/language
	tclsh test/library
	tclsh test/graphics
	tclsh test/examples


# Web resources

PREFIX = https://spivey.oriel.ox.ac.uk/gwiki/files

web: web-dirs update .signed

FILES = .htaccess geomlab.jar examples.jar geomlab.jnlp \
	deployJava.js arrow.png icon32.png icon64.png

SKIN = web/skins/GeomSkin

web-dirs: force
	@mkdir -p web/files $(SKIN)/images web/extensions

update: $(FILES:%=web/files/%) \
	$(SKIN)/GeomSkin.php $(SKIN)/screen.css \
	$(SKIN)/images/cycletpale.png $(SKIN)/images/quad96.png \
	$(SKIN)/images/document.png \
	web/LocalSettings.php \
	web/extensions/GeomGrind.php

web/files/geomlab.jar: .compiled obj/geomlab.gls $(RESOURCES:%=obj/%)
	cd obj; jar cfm ../$@ ../scripts/manifest \
		$(PACKAGES) $(RESOURCES) $(ICONS) geomlab.gls

web/files/examples.jar: examples.gls
	jar cf $@ $<

web/files/.htaccess: res/htaccess;		cp $< $@
web/files/%: obj/%;				cp $< $@
web/% web/files/% web/extensions/% \
	$(SKIN)/% $(SKIN)/images/%: res/%; 	cp $< $@

web/files/%.jnlp: res/%.jnlp.in
	sed 's=@CODEBASE@=$(PREFIX)=' $< >$@

# TSA = http://timestamp.comodoca.com/rfc3161
TSA = http://timestamp.globalsign.com/scripts/timestamp.dll 

.signed: web/files/geomlab.jar web/files/examples.jar
	for f in $?; do \
	    jarsigner -keystore javakey/keystore \
		-storepass `cat javakey/storepass` -tsa $(TSA) $$f geomlab; \
	done
	echo timestamp >$@

HOST = spivey.oriel.ox.ac.uk
WIKI = /var/www/gwiki

publish: web force
	rsync -rvt web/ $(HOST):$(WIKI)
	$(MAKE) -C wiki $@
	$(MAKE) -C figs $@
	$(MAKE) -C img $@

purge: force
	ssh $(HOST) php $(WIKI)/maintenance/deleteOldRevisions.php --delete

clean: force
	rm -rf obj examples.gls Boot?.java Boot?.class Boot.class
	rm -f .compiled .signed

force:
