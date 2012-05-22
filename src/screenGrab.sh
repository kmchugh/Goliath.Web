#!/bin/bash
# setup: firefox: nopopups, restore session flash installed, 1024x800 no NavBar, ToolBar
#echo "Format: screenGrab URL Directory ImageName.jpg SleepSeconds"
echo "Example screenGrab.sh http://slashdot.org /tmp/ myImage"
echo "Website: $1"
echo "Directory: $2"
echo "File Name: $3"
echo "Seconds of Sleeping: $4"
echo "Loading website..."
firefox $1 &
echo "Sleeping..."
# improve firefox loading time!?
sleep 4
mkdir /tmp/$3
wget -p --convert-links -P $2/$3/content/ $1
echo "Capturing screen with command: import -window root $2/$3/images/full.jp"
sleep 5
import -window root "$2/$3/images/full.jpg"
echo "Screen Captured, stopping firefox"
killall firefox-bin
#Ubuntu dependent settings
#Get rid of top border and ubuntu app
echo "Cropping image (-15+50)"
mogrify -crop -15+55 "$2/$3/images/full.jpg"
echo "Cropping image (-0-30)"
mogrify -crop -0-30 "$2/$3/images/full.jpg"
convert -resize 128 "$2/$3/images/full.jpg"  "$2/$3/images/small.jpg"
convert -resize 320 "$2/$3/images/full.jpg"  "$2/$3/images/medium.jpg"
cd "$2$3"
zip -r "content.zip" *