#!/bin/bash
# This file is part of papio.

# papio is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# papio is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with papio.  If not, see <http://www.gnu.org/licenses/>.

gradle build -x test -x ktlint
rm -f ~/.local/bin/papio-cli.jar
mv papio-cli/build/libs/papio-cli.jar ~/.local/bin/papio-cli.jar

echo '
#!/bin/bash
java -jar ~/.local/bin/papio-cli.jar "$@"
' > ~/.local/bin/papio-cli
chmod 755 ~/.local/bin/papio-cli

echo "Done! Use \"papio-cli\" to run the program."