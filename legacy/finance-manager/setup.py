# coding=utf-8
"""
Copyright 2016-2017 Hermann Krumrey <hermann@krumreyh.com>

This file is part of finance-manager.

finance-manager is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

finance-manager is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with finance-manager.  If not, see <http://www.gnu.org/licenses/>.
"""

# imports
from setuptools import setup


def readme():
    """
    Reads the readme file.
    :return: the readme file as a string
    """
    with open('README.md') as f:
        return f.read()


setup(name='finance-manager',
      version='0.3.1',
      description='A personal finance manager program',
      long_description=readme(),
      classifiers=['Development Status :: 2 - Pre-Alpha',
                   'Intended Audience :: End Users/Desktop',
                   'License :: OSI Approved :: GNU General Public License v3 (GPLv3)',
                   'Programming Language :: Python :: 3',
                   'Topic :: Office/Business :: Financial',
                   'Natural Language :: English',
                   'Operating System :: OS Independent'
                   ],
      url='http://namibsun.net/namboy94/finance-manager',
      author='Hermann Krumrey',
      author_email='hermann@krumreyh.com',
      license='GNU GPL3',
      packages=['finance_manager',
                'finance_manager.gui',
                'finance_manager.objects',
                'finance_manager.utils',
                'finance_manager.gui.dialogs',
                'finance_manager.gui.widgets'],
      install_requires=[],
      dependency_links=['https://git.gnome.org/browse/pygobject'],
      test_suite='nose.collector',
      tests_require=['nose'],
      scripts=['bin/financemanager'],
      zip_safe=False)
