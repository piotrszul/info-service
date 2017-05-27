#!/bin/bash


WORK_DIR=target
SPOOL_DIR=${WORK_DIR}/spool
mkdir -p ${SPOOL_DIR}

TIMESTAMP=$(date "+%Y%m%d%H%M%S")
echo "Running at timestamp: ${TIMESTAMP}"

#IDD60920    Weather Observations Current Summary - Northern Territory - XML (NT)    Observation 24
#IDN60920    Weather Observations Current Summary - New South Wales and Australian Capital Territory - XML (NSW) Observation 24
#IDQ60920    Weather Observations Current Summary - Queensland - XML (QLD)   Observation 24
#IDS60920    Weather Observations Current Summary - South Australia - XML (SA)   Observation 24
#IDT60920    Weather Observations Current Summary - Tasmania - XML (TAS) Observation 24
#IDV60920    Weather Observations Current Summary - Victoria - XML (VIC) Observation 24
#IDW60920    Weather Observations Current Summary - Western Australia - XML (WA) Observation 24


curl  'ftp://ftp.bom.gov.au/anon/gen/fwo/ID{D,N,Q,S,T,V,W}60920.xml' -o "${WORK_DIR}/${TIMESTAMP}_ID#1_60920.xml"

for INPUT_NAME in ${WORK_DIR}/${TIMESTAMP}_*.xml ;do 

	ISSUE_TIME_UTC_TAG=$(grep -o '<issue-time-utc>[^<]*</issue-time-utc>' ${INPUT_NAME})
	echo $ISSUE_TIME_UTC_TAG

	ISSUE_TIME_UTC_TMP=${ISSUE_TIME_UTC_TAG#<*>}
	ISSUE_TIME_UTC=${ISSUE_TIME_UTC_TMP%<*>}

	echo $ISSUE_TIME_UTC

	INPUT=${INPUT_NAME#*_}
	echo "Input: ${INPUT}"

	SPOOL_FILE_NAME=${SPOOL_DIR}/${INPUT}_${ISSUE_TIME_UTC}
	COMPLETED_FILE_NAME=${SPOOL_FILE_NAME}.COMPLETED

	echo $SPOOL_FILE_NAME

	if [[ -f ${SPOOL_FILE_NAME} || -f ${COMPLETED_FILE_NAME} ]]; then
		echo "Alread in spool ${SPOOL_FILE_NAME}"
		rm ${INPUT_NAME}
	else
		echo "Moving ${INPUT_NAME} to spool"
		mv ${INPUT_NAME} ${SPOOL_FILE_NAME}
	fi
done


