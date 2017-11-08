#!/usr/bin/python
# $Id$
#-----------------------------------------------------------------------------
# Copyright (C) 1997-2000 Lance Finn Helsten (helsten@acm.org)
#
# This library is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the
# Free Software Foundation; either version 2 of the License, or (at your
# option) any later version.
#
# This library is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this library; if not, write to the Free Software Foundation, Inc.,
# 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

from sys import *
import os

CMD = 'java -classpath $CLASSPATH org.ttt.salt.Main %s > %s 2> %s'
FILES = ['InvalidDescrip.xml',
        'ValidSchema.xml',
        'InvalidLevel.xml',
        'ValidDTD.xml',
        'InvalidPickList.xml']
        
def processFile(file):
    outf = file + '.out'
    errf = file + '.err'
    cmd = CMD % (file, outf, errf)
    os.system(cmd)
    out = open(outf, 'r').readlines()
    err = open(errf, 'r').readlines()
    os.remove(outf)
    os.remove(errf)
    return {'file':file, 'out':out, 'err':err}

if __name__ == '__main__':
    results = map(processFile, FILES)
    for r in results:
        print r
