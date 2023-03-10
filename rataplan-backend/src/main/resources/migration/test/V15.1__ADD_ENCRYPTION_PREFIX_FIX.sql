UPDATE appointment
SET url = 'RAW__##__' || url
WHERE url IS NOT NULL
AND url NOT LIKE 'ENC\_\_##\_\_%'
AND url NOT LIKE 'RAW\_\_##\_\_%';