#!/bin/bash
smartthings capabilities:update imageafter45121.acAutoClean --input ../resource/capability/acAutoClean.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acAutoClean 1 --yaml --input=../resource/capability/acAutoClean-presentation.yaml
sleep 2

smartthings capabilities:update imageafter45121.acCoMode --input ../resource/capability/acCoMode.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acCoMode 1 --yaml --input=../resource/capability/acCoMode-presentation.yaml
sleep 2

smartthings capabilities:update imageafter45121.acDirection --input ../resource/capability/acDirection.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acDirection 1 --yaml --input=../resource/capability/acDirection-presentation.yaml
sleep 2

smartthings capabilities:update imageafter45121.acOperation --input ../resource/capability/acOperation.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acOperation 1 --yaml --input=../resource/capability/acOperation-presentation.yaml
sleep 2

smartthings capabilities:update imageafter45121.acOpMode --input ../resource/capability/acOpMode.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acOpMode 1 --yaml --input=../resource/capability/acOpMode-presentation.yaml
sleep 2

smartthings capabilities:update imageafter45121.acTempSet --input ../resource/capability/acTempSet.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acTempSet 1 --yaml --input=../resource/capability/acTempSet-presentation.yaml
sleep 2

smartthings capabilities:update imageafter45121.acVolume --input ../resource/capability/acVolume.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acVolume 1 --yaml --input=../resource/capability/acVolume-presentation.yaml
sleep 2

smartthings capabilities:update imageafter45121.acWindLevel --input ../resource/capability/acWindLevel.yaml
sleep 2
smartthings capabilities:presentation:update imageafter45121.acWindLevel 1 --yaml --input=../resource/capability/acWindLevel-presentation.yaml
