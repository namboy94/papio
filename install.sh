#!/bin/bash

gradle build
rm -f ~/.local/bin/papio-cli.jar
mv papio-cli/build/libs/papio-cli.jar ~/.local/bin/papio-cli.jar

echo '
#!/bin/bash
java -jar ~/.local/bin/papio-cli.jar "$@"
' > ~/.local/bin/papio-cli
chmod 755 ~/.local/bin/papio-cli
