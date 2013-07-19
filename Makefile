# Top level Makefile for GeomLab

JAVAC = /usr/lib/jvm/java-6-openjdk-i386/bin/javac

PACKAGES = funbase funjit geomlab plugins
JAVA := $(patsubst src/%,%,$(foreach pkg,$(PACKAGES),$(wildcard src/$(pkg)/*)))
SOURCE = $(JAVA) boot.txt compiler.txt prelude.txt 
HELP = commands errors language library tips
RESOURCES = VeraMono.ttf mike.jpg mikelet.jpg contents.html style.css \
	$(HELP:%=%.html) properties icon16.png icon32.png icon64.png icon128.png
IMAGES = obj/geomlab.gls examples.gls

all: prep .compiled $(RESOURCES:%=obj/%) $(IMAGES)

prep: force
	@mkdir -p obj

.compiled: $(wildcard src/*/*.java)
	$(JAVAC) -d obj src/*/*.java
	echo timestamp >$@

obj/%: res/%; cp $< $@
obj/%: src/%; cp $< $@

obj/%.html: wiki/%.wiki wiki/htmlframe scripts/htmltran.tcl
	tclsh scripts/htmltran.tcl $< >$@

obj/icon128.png:
	runscript progs/solutions.txt -e 'savepic(colour(T), "$@", 128, 0.5, 0)'

obj/icon%.png: obj/icon128.png
	convert obj/icon128.png -scale $*x$* $@

RUNSCRIPT = java -cp obj -ea geomlab.RunScript

obj/geomlab.gls: .compiled src/boot.txt src/compiler.txt src/prelude.txt \
		$(RESOURCES:%=obj/%)
	$(RUNSCRIPT) -b src/boot.txt src/compiler.txt src/compiler.txt \
		src/prelude.txt -e '_save("$@")'

examples.gls: obj/geomlab.gls progs/examples.txt
	$(RUNSCRIPT) progs/examples.txt -e '_save("$@")'

bootstrap: .compiled force 
	$(RUNSCRIPT) -b src/boot.txt src/compiler.txt \
		-e 'let dump = _primitive("dump") in dump("stage1.boot")'
	$(RUNSCRIPT) -b stage1.boot src/compiler.txt \
		-e 'let dump = _primitive("dump") in dump("stage2.boot")'
	$(RUNSCRIPT) -b stage2.boot src/compiler.txt \
		-e 'let dump = _primitive("dump") in dump("stage3.boot")'
	cmp stage2.boot stage3.boot

bootup: stage2.boot force
	(sed -e 's/compiler.txt/boot.txt/' -e '/^ *$$/q' src/compiler.txt; \
		cat stage2.boot) >src/boot.txt


# Web resources

PREFIX = http://spivey.oriel.ox.ac.uk/wiki/files/gl

web: web-dirs update .signed

web-dirs: force
	@mkdir -p web web/src $(PACKAGES:%=web/src/%)

update: web/.htaccess web/geomlab.jar web/examples.jar web/geomlab-src.jar \
		web/geomlab.jnlp web/geomdemo.jnlp \
		$(SOURCE:%=web/src/%) web/arrow.png

web/geomlab.jar: .compiled obj/geomlab.gls $(RESOURCES:%=obj/%)
	cd obj; jar cfm ../$@ ../scripts/manifest \
		$(PACKAGES) $(RESOURCES) geomlab.gls

web/examples.jar: examples.gls
	jar cf $@ $<

.signed: web/geomlab.jar web/examples.jar
	for f in $?; do jarsigner -storepass `cat ~/.keypass` $$f mykey; done
	echo timestamp >$@

web/geomlab-src.jar: $(SOURCE:%=src/%) $(RESOURCES:%=obj/%)
	jar cf $@ $^

web/.htaccess: scripts/htaccess;		cp $< $@
web/src/%: src/%;				cp $< $@
web/%: res/%;					cp $< $@

web/%.jnlp: scripts/%.jnlp.in
	sed 's=@CODEBASE@=$(PREFIX)=' $< >$@

publish: web force
	rsync -rvt --delete web/ spivey:wiki/files/gl

clean: force
	rm -rf obj examples.gls
	rm -f .compiled .signed

force:
