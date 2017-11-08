#!/usr/bin/python
#-----------------------------------------------------------------------------
__author__ = ('Lance Finn Helsten',)
__version__ = '1.0.2'
__copyright__ = """Copyright 1997-2011 Lance Finn Helsten (helsten@acm.org)"""
__license__ = """
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

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
