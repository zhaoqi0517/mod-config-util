mvn clean install -DskipTests
rm -fr ../zyeeda-cdeio-setup/mods/com.zyeeda~mod-property-util~0.1
cp -fr target/mods/com.zyeeda~mod-property-util~0.1 ../../zyeeda-cdeio-setup/mods/
