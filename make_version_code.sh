#!/usr/bin/env bash
read -a a <<<`git describe --tags | sed "s/-/ /g;s/-g.*//g;s/\./ /g;s/-/ /g"`
m=`printf "%2d" "${a[1]:-0}"`;
j=`printf "%02d" "${a[2]:-0}"`;
r=`printf "%04d" "${a[3]:-0}"`;
echo $m$j$r