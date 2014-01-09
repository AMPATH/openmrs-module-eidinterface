OpenMRS EID Interface Module
===========================

This module provides the following endpoints for access to information requested by EID.

getPatientStatus
----------------
```
curl -i -X POST --data-binary @identifiers.txt -H "Content-type: text/plain" -u jkeiper https://amrsresearch1.ampath.or.ke:8443/amrs/module/eidinterface/getPatientStatus.htm
```

Accepts a list of identifiers for validation, one per line.


Returns a CSV in the following format:

```
"Identifier","Status","CCC Number"
"101-6","NOT FOUND",""
"1-2","INVALID IDENTIFIER",""
"999-3","NOT ENROLLED",""
"XXAS-X","ENROLLED","12345-67890"
```
