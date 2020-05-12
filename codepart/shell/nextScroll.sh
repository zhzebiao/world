curl -XGET -k 'https://127.0.0.1:24118/_search/scroll?scroll=1m' -H 'Content-Type:application/json' -d '
{
  "scroll_id": ""
}'