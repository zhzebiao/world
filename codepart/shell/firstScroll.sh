
curl -XGET -k 'https://127.0.0.1:24118/tb_face-*/_search?scroll=1m' -H 'Content-Type:application/json' -d '
{
  "size": 10000, 
  "query": {
      "bool": {
        "must": [
          {
            "range": {
              "CreateTime": {
                "gte": "20200506000000",
                "lte": "20200507000000"
              }
            }
          },
          {
            "term": {
              "DeviceID": {
                "value": "VALUE"
              }
            }
          }
        ]
      }
  },
  "_source": ["LaneNo","DeviceID"]
}'
