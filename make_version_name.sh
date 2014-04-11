#!/usr/bin/env bash
read -a a <<<`git describe --tags | sed "s/-/ /g;s/-g.*//g;s/\./ /g;s/-/ /g"`
echo ${a[1]:-0}.${a[2]:-0}.${a[3]:-0}